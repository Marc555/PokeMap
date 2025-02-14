package cat.copernic.pokemap.presentation.ui.components

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import cat.copernic.pokemap.presentation.ui.screens.loginWithEmail
import cat.copernic.pokemap.utils.LanguageManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Composable
fun BiometricLoginButton(
    context: Context,
    navController: NavController,
    onErrorMessageChange: (String) -> Unit
) {
    val executor: Executor = Executors.newSingleThreadExecutor()
    var authMessage by remember { mutableStateOf("") }
    val firebaseAuth = FirebaseAuth.getInstance()

    // Ensure the context is a FragmentActivity
    val activity = context as? FragmentActivity ?: return

    val biometricPrompt = remember {
        BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    authMessage = "Authentication Successful!"


                    val biometricEmail = retrieveStoredEmail(context)
                    val biometricPassword = retrieveStoredPassword(context)
                    Log.d("BiometricAuth\n", "Attempting sign-in with email: $biometricEmail")

                    if (biometricEmail != null && biometricPassword != null) {
                        Log.d("BiometricAuth\n", "Attempting sign-in with email: $biometricEmail")

                        val credential =
                            EmailAuthProvider.getCredential(biometricEmail, biometricPassword)
                        firebaseAuth.signInWithCredential(credential)
                            .addOnSuccessListener {
                                Log.d("BiometricAuth\n", "Sign-in successful! Navigating to Home.")
                                navController.navigate(AppScreens.Home.rute) // ✅ Navigate only after success
                            }
                            .addOnFailureListener { e ->
                                Log.e("BiometricAuth\n", "Sign-in failed: ${e.message}")
                                onErrorMessageChange("Biometric login failed: ${e.message}")
                            }
                    } else {
                        Log.e("BiometricAuth\n", "No stored credentials found.")
                        onErrorMessageChange("No biometric account found. Please log in manually.")
                    }
                }


                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onErrorMessageChange("Authentication Failed. Try again!")
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onErrorMessageChange("Error: $errString")
                }
            }
        )
    }

    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Use fingerprint or face to log in")
            .setNegativeButtonText("Cancel")
            .build()
    }

    Button(
        onClick = {
            val biometricManager = BiometricManager.from(context)
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    biometricPrompt.authenticate(promptInfo)

                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    onErrorMessageChange("No biometric hardware available")
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    onErrorMessageChange("Biometric hardware is unavailable")
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    onErrorMessageChange("No biometric data enrolled. Set it up in settings.")
                }

                else -> {
                    onErrorMessageChange("Biometric authentication is not available")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        Text(LanguageManager.getText("auth biometric"))
    }
}

fun saveCredentials(context: Context, email: String, password: String) {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "biometric_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    val storedEmail = sharedPreferences.getString("email", null)

    if (storedEmail == null) { // ✅ Only store credentials if none exist
        sharedPreferences.edit().apply {
            putString("email", email)
            putString("password", password)
            apply()
        }
        Log.d("BiometricAuth\n", "Stored credentials for user: $email")
    } else {
        Log.d(
            "BiometricAuth\n",
            "User already linked to biometrics: $storedEmail. Not overwriting."
        )
    }
}


fun retrieveStoredEmail(context: Context): String? {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "biometric_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    return sharedPreferences.getString("email", null)
}


fun retrieveStoredPassword(context: Context): String? {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "biometric_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    return sharedPreferences.getString("password", null)
}

fun clearStoredCredentials(context: Context) {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "biometric_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    sharedPreferences.edit().clear().apply() // Clear stored email and password
}
