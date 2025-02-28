package cat.copernic.pokemap.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cat.copernic.pokemap.MyApp
import cat.copernic.pokemap.utils.LanguageManager

@Composable
fun BiometricLoginButtonWithIcon(
    navController: NavController,
    onErrorMessageChange: (String) -> Unit,
    enabled: Boolean = true,
) {
    var showBiometricAuth by remember { mutableStateOf(false) }
    Button(
        onClick = {
            showBiometricAuth = true
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.Filled.Fingerprint, // ✅ Default fingerprint icon
            contentDescription = "Biometric Login",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = LanguageManager.getText("login biometric"),
            style = MaterialTheme.typography.bodyLarge
        )
    }

    if (showBiometricAuth) {
        BiometricPrompt(navController, onErrorMessageChange)
    }

}


@Composable
fun BiometricToggleButton(onError: (String) -> Unit, onSuccess: (Boolean) -> Unit) {
    var isEnableBioAuth by remember { mutableStateOf(MyApp.prefs.isBiometricEnabled()) }

    Button(
        onClick = {
            if (isEnableBioAuth) {
                try {
                    MyApp.prefs.clearBiometricCredentials()
                    MyApp.prefs.clearGoogleAuthToken()
                    MyApp.prefs.setBiometricEnabled(false)
                    isEnableBioAuth = MyApp.prefs.isBiometricEnabled()
                } catch (e: Exception) {
                    onError(LanguageManager.getText("error message"))
                }
            }else{
                try {
                    MyApp.prefs.setBiometricEnabled(true)
                    isEnableBioAuth = MyApp.prefs.isBiometricEnabled()
                } catch (e: Exception) {
                    onError(LanguageManager.getText("error message"))
                }
            }
            onSuccess(isEnableBioAuth)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Fingerprint,
            contentDescription = "Biometric Login",
            modifier = Modifier.size(24.dp)
        )
        if (isEnableBioAuth) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                modifier = Modifier
                    .size(24.dp)
                    .offset(x = (-24).dp, y = (0).dp),
                tint = Color.Red
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isEnableBioAuth) {
                LanguageManager.getText("clean biometric")
            } else {
                LanguageManager.getText("login biometric")
            },
            style = MaterialTheme.typography.bodyLarge
        )
    }

}

