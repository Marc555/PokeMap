package cat.copernic.pokemap.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cat.copernic.pokemap.presentation.ui.components.BiometricLoginButton
import cat.copernic.pokemap.presentation.ui.components.LanguageSelector
import cat.copernic.pokemap.utils.LanguageManager

@Composable
fun Settings(navController: NavController){
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            LanguageManager.getText("settings")
        )
        LanguageSelector{}

        Text(text ="Contact administrators", modifier = Modifier
            .clickable { navController.navigate("contact") } )

        Text(text = LanguageManager.getText("auth biometric"))
        BiometricLoginButton(context,navController) { }
        Log.d("BiometricCheck", "Biometric availability: ${checkBiometricAvailability(context)}")

    }
}
