package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import cat.copernic.pokemap.utils.LanguageManager
import cat.copernic.pokemap.utils.LanguageManager.viewModel
import cat.copernic.pokemap.utils.saveUserToFirestore
import com.google.firebase.auth.FirebaseUser
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(navController: NavController) {
    val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var username by remember { mutableStateOf("") }
    var isChecking by remember { mutableStateOf(false) }
    var onMensajeErrorChange by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(text = "${LanguageManager.getText("welcome")}, ${user?.displayName ?: "New User"}!")

        Spacer(modifier = Modifier.height(10.dp))

        user?.photoUrl?.let { url ->
            Image(
                painter = rememberAsyncImagePainter(url),
                contentDescription = LanguageManager.getText("profile picture"),
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = LanguageManager.getText("choose username"))
        UsernameInput(username,onUsernameChange ={username = it})

        Spacer(modifier = Modifier.height(10.dp))

        // Save and Proceed Button
        Button(
            onClick = {
                viewModel.viewModelScope.launch {
                    val existe = isRepeatingUsername(username)
                    isChecking = false // La verificación ha terminado
                    if (existe) {
                        onMensajeErrorChange = true
                    } else {
                        user?.let {
                            saveUserToFirestore(
                                it.uid,
                                username,
                                it.email,
                                it.photoUrl.toString()
                            )
                            navController.navigate(AppScreens.Home.rute) // ✅ Move to Home Screen
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
        ) {
            Text(text = LanguageManager.getText("save"))
        }
    }
    if (onMensajeErrorChange){
    ErrorMessage(LanguageManager.getText("username taken"))}
}


