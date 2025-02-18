package cat.copernic.pokemap.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cat.copernic.pokemap.MyApp
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.utils.LanguageManager
import com.google.firebase.auth.FirebaseAuth

@Composable
fun BiometricLoginButtonWithIcon(navController : NavController,onErrorMessageChange: (String) -> Unit ,enabled : Boolean = true) {
    var showBiometricAuth by remember { mutableStateOf(false) }

    Button(
        onClick = {
            showBiometricAuth = true},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        enabled =enabled
    ) {
        Icon(
            imageVector = Icons.Filled.Fingerprint, // ✅ Default fingerprint icon
            contentDescription = "Biometric Login",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = LanguageManager.getText(  "login biometric"), style = MaterialTheme.typography.bodyLarge)
    }

    if (showBiometricAuth){
        BiometricPrompt(navController,onErrorMessageChange)
    }

}


@Composable
fun BiometricLoginButtonWithIconUnable() {
    Button(
        onClick = {
           MyApp.prefs.clearBiometricCredentials()},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Fingerprint, // ✅ Default fingerprint icon
            contentDescription = "Biometric Login",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = LanguageManager.getText(  "disable biometric"), style = MaterialTheme.typography.bodyLarge)
    }

}
