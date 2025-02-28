package cat.copernic.pokemap.presentation.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.MyApp
import cat.copernic.pokemap.data.DTO.Rol
import cat.copernic.pokemap.presentation.ui.components.BiometricMessage
import cat.copernic.pokemap.presentation.ui.components.BiometricToggleButton
import cat.copernic.pokemap.presentation.ui.components.CrashButton
import cat.copernic.pokemap.presentation.ui.components.LanguageSelector
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.utils.LanguageManager
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Settings(navController: NavController) {
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(MyApp.prefs.isBiometricEnabled()) }
    var textButtonAuthBio by remember {
        mutableStateOf(
            {
                if (isEnabled) {
                    LanguageManager.getText("biometric is enable")
                } else {
                    LanguageManager.getText("biometric is disable")
                }
            }
        )
    }
    val user = FirebaseAuth.getInstance().currentUser
    val userUid = user?.uid
    val usersViewModel: UsersViewModel = viewModel()

    LaunchedEffect(userUid) {
        if (userUid != null) {
            usersViewModel.fetchUserByUid(userUid)
        }
    }
    val userToCheck by usersViewModel.user.collectAsState()
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Top
    )

    {
        Text(
            LanguageManager.getText("settings"),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textAlign = Center,
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            LanguageManager.getText("language"),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),

            )
        LanguageSelector {}

        Spacer(modifier = Modifier.height(15.dp))


        BiometricMessage(textButtonAuthBio())
        BiometricToggleButton(
            onSuccess = {
                isEnabled = it;
            },
            onError = {
                Toast.makeText(
                    context,
                    "Error: $it",
                    Toast.LENGTH_SHORT
                ).show()
            })

        Spacer(modifier = Modifier.height(15.dp))

        if (userToCheck?.rol == Rol.ADMIN) {
            Text(text = "See messages", modifier = Modifier
                .clickable { navController.navigate("contactMessages") }
                .padding(16.dp)
                .fillMaxWidth()
            )
        } else {
            Text(text = "Contact administrators", modifier = Modifier
                .clickable { navController.navigate("contact") }
                .padding(16.dp)
                .fillMaxWidth()
            )
        }
    }
}







