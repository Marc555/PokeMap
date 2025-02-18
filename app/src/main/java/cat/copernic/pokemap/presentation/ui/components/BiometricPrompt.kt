package cat.copernic.pokemap.presentation.ui.components

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import cat.copernic.pokemap.MyApp
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import cat.copernic.pokemap.utils.LanguageManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Composable
fun BiometricPrompt(navController: NavController, onErrorMessageChange: (String) -> Unit) {
    val context = LocalContext.current
    val executor: Executor = Executors.newSingleThreadExecutor()
    val firebaseAuth = FirebaseAuth.getInstance()
    var isLoading by remember { mutableStateOf(false) }
    val isBiometricEnabled = MyApp.prefs.isBiometricEnabled()

    if (!isBiometricEnabled) return // ✅ Hide button if biometrics are disabled

    // ✅ Automatically trigger biometrics on screen load
    LaunchedEffect(Unit) {
        val biometricManager = BiometricManager.from(context)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS) {

            Log.d("BiometricAuth", "Biometric authentication available. Triggering...")

            val activity = context as? FragmentActivity ?: return@LaunchedEffect
            val biometricPrompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Log.d("BiometricAuth", "Biometric authentication successful.")
                        isLoading = true

                        val biometricEmail = MyApp.prefs.getBiometricEmail()
                        val biometricPassword = MyApp.prefs.getBiometricPassword()
                        val googleToken = MyApp.prefs.getGoogleAuthToken()

                        if (biometricEmail != null && biometricPassword != null) {
                            // Authenticate with Firebase using email/password
                            val credential = EmailAuthProvider.getCredential(biometricEmail, biometricPassword)
                            firebaseAuth.signInWithCredential(credential)
                                .addOnSuccessListener {
                                    Log.d("BiometricAuth", "Firebase sign-in successful! Navigating to Home.")

                                    Handler(Looper.getMainLooper()).postDelayed({
                                        isLoading = false
                                        navController.navigate(AppScreens.Home.rute)
                                    }, 1000)
                                }
                                .addOnFailureListener { e ->
                                    Log.e("BiometricAuth", "Firebase sign-in failed: ${e.message}")
                                    isLoading = false
                                    onErrorMessageChange("Biometric login failed: ${e.message}")
                                }
                        } else if (googleToken != null) {
                            // ✅ Authenticate with Firebase using Google credentials
                            val googleCredential = GoogleAuthProvider.getCredential(googleToken, null)
                            firebaseAuth.signInWithCredential(googleCredential)
                                .addOnSuccessListener {
                                    Log.d("BiometricAuth", "Google Sign-In successful! Navigating to Home.")

                                    Handler(Looper.getMainLooper()).postDelayed({
                                        isLoading = false
                                        navController.navigate(AppScreens.Home.rute)
                                    }, 1000)
                                }
                                .addOnFailureListener { e ->
                                    Log.e("BiometricAuth", "Google Sign-In failed: ${e.message}")
                                    isLoading = false
                                    onErrorMessageChange("Biometric login failed: ${e.message}")
                                }
                        } else {
                            Log.e("BiometricAuth", "No stored credentials found.")
                            isLoading = false
                            onErrorMessageChange(LanguageManager.getText("auth biometric error"))
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Log.e("BiometricAuth", "Biometric authentication failed.")
                        onErrorMessageChange("Authentication Failed. Try again!")
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Log.e("BiometricAuth", "Error: $errString")
                        onErrorMessageChange("Error: $errString")
                    }
                }
            )

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(LanguageManager.getText( "auth biometric"))
                .setSubtitle(LanguageManager.getText("auth biometric instructions"))
                .setNegativeButtonText(LanguageManager.getText("cancel"))
                .build()

            biometricPrompt.authenticate(promptInfo)
        } else {
            Log.e("BiometricAuth", "Biometric authentication not available.")
            onErrorMessageChange("Biometric authentication is not available on this device.")
        }
    }

    // ✅ Show loading indicator while authenticating
    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
