package cat.copernic.pokemap.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender // ✅ Use IntentSender instead of Intent
import android.util.Log
import cat.copernic.pokemap.MyApp
import cat.copernic.pokemap.R
import cat.copernic.pokemap.data.DTO.UserFromGoogle
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GoogleAuthHelper(private val context: Context) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val signInClient: SignInClient = Identity.getSignInClient(context)
    private val signInMutex = Mutex()

    // ✅ Launch Google Sign-In Using `PendingIntent`
    fun launchSignIn(
        activity: Activity,
        onSignInStarted: (IntentSender) -> Unit, // ✅ Use IntentSender instead of Intent
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            signInMutex.withLock { // ✅ Prevent multiple clicks
                val signInRequest = BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setServerClientId(context.getString(R.string.default_web_client_id))
                            .setFilterByAuthorizedAccounts(false)
                            .build()
                    )
                    .setAutoSelectEnabled(false) // ✅ Forces manual selection & consent
                    .build()

                signInClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(activity) { result ->
                        try {
                            onSignInStarted(result.pendingIntent.intentSender) // ✅ Send IntentSender
                        } catch (e: Exception) {
                            onError("Failed to start Google Sign-In: ${e.localizedMessage}")
                        }
                    }
                    .addOnFailureListener { e ->
                        onError("Google Sign-In failed: ${e.localizedMessage}")
                    }
            }
        }
    }

    fun handleSignInResult(
        data: Intent?,
        onSignUp: (UserFromGoogle) -> Unit, // ✅ Trigger onboarding if Firestore profile is missing
        onLogin: (UserFromGoogle) -> Unit, // ✅ Redirect to Home if already exists
        onShowMessage: (String) -> Unit, // ✅ Show message if account exists with another method
        onError: (String) -> Unit,
    ) {
        try {
            CoroutineScope(Dispatchers.IO).launch {

                val credential: SignInCredential = signInClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val email = credential.id
                val name = credential.givenName ?: "NULL"
                val surname = credential.familyName ?: "NULL"
                val imageUrl = credential.profilePictureUri ?: "NULL"

                if (idToken != null) {
                    firebaseAuth.signInWithCredential(firebaseCredential)
                        .addOnSuccessListener { authResult ->
                            val uid: String = authResult.user?.uid ?: "NULL"
                            StoredGoogleAuthCred.fillUserFromGoogle(
                                uid,
                                idToken,
                                email,
                                name,
                                surname,
                                imageUrl.toString()
                            )

                            if (authResult.additionalUserInfo?.isNewUser == true) {
                                Log.d(
                                    "GoogleAuth",
                                    "User signed in successfully. Proceeding to onboarding."
                                )
                                Log.d("GoogleAuth", "New user detected: $email")

                                if (!MyApp.prefs.isAnyBiometricOn()) {
                                    MyApp.prefs.saveGoogleAuthToken(idToken)
                                }
                                onSignUp(StoredGoogleAuthCred.waitForUsername())
                            } else {
                                if (!MyApp.prefs.isAnyBiometricOn()) {
                                    MyApp.prefs.saveGoogleAuthToken(idToken)
                                }
                                onLogin(StoredGoogleAuthCred.waitForUsername())
                            }
                        }.addOnFailureListener { e ->
                            Log.e("GoogleAuth", "Sign-in failed: ${e.localizedMessage}")
                            onError(e.localizedMessage ?: "Google Sign-In failed")
                        }
                } else {
                    Log.d("GoogleAuth", "User already exists: $email")
                    onShowMessage("This email is registered with password login. Please log in manually or link Google in settings.")
                    return@launch
                }
            }
        } catch (e: ApiException) {
            onError("Google Sign-In failed: ${e.localizedMessage}")
        }
    }


}


