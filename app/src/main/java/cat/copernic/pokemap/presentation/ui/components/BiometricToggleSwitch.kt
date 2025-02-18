package cat.copernic.pokemap.presentation.ui.components

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import cat.copernic.pokemap.utils.LanguageManager
import cat.copernic.pokemap.utils.SharedPreferencesManager

@Composable
fun BiometricToggleSwitch(context: Context) {
    val sharedPrefs = remember { SharedPreferencesManager(context) }
    var isEnabled by remember { mutableStateOf(sharedPrefs.isBiometricEnabled()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Enable Biometric Login", modifier = Modifier.weight(1f))

        Switch(
            checked = isEnabled,
            onCheckedChange = { newValue ->
                isEnabled = newValue
                sharedPrefs.setBiometricEnabled(newValue)

                // ✅ If disabling biometrics, remove stored credentials
                if (!newValue) {
                    sharedPrefs.clearBiometricCredentials()
                }
            }
        )
    }
}

@Composable
fun BiometricDisable() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(16.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = LanguageManager.getText( "biometric not available"), modifier = Modifier.weight(1f).padding(16.dp))
    }
}
