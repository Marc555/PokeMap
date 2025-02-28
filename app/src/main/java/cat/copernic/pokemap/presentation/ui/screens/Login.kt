package cat.copernic.pokemap.presentation.ui.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cat.copernic.pokemap.R
import cat.copernic.pokemap.presentation.ui.components.LanguageSelector
import cat.copernic.pokemap.presentation.ui.components.RestorePassword
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import cat.copernic.pokemap.utils.LanguageManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.biometric.BiometricManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.pokemap.MyApp
import cat.copernic.pokemap.data.DTO.UserFromGoogle
import cat.copernic.pokemap.presentation.ui.components.BiometricLoginButtonWithIcon
import cat.copernic.pokemap.presentation.ui.components.ContinueWithGoogleButton
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.utils.GoogleAuthHelper
import cat.copernic.pokemap.utils.SharedUserViewModel
import cat.copernic.pokemap.utils.checkIfUserExists


@Composable
fun Login(navController: NavController, userViewModel: UsersViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showResetPasswordDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val canUseBiometrics = remember {
        checkBiometricAvailability(context)
    }
    var userEmailToCheck by remember { mutableStateOf("") }
    var doesAccountExists by remember { mutableStateOf(false) }
    var isSigningIn by remember { mutableStateOf(false) } // ✅ Track sign-in state
    val activity = context as? Activity
    val googleAuthHelper = remember { GoogleAuthHelper(context) }

    LaunchedEffect(userEmailToCheck) {
        if (userEmailToCheck.isNotEmpty()) { // ✅ Ensure it's set before checking
            doesAccountExists = checkIfUserExists(userEmailToCheck)

            Log.d("doesAccountExists", doesAccountExists.toString())

            if (!doesAccountExists) {
                navController.popBackStack()
                navController.navigate(AppScreens.Onboarding.rute)
            } else {
                navController.popBackStack()
                navController.navigate(AppScreens.Home.rute)
            }
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        isSigningIn = false
        if (result.resultCode == Activity.RESULT_OK) {
            googleAuthHelper.handleSignInResult(result.data,
                onSignUp = { _ ->
                    navController.navigate(AppScreens.Onboarding.rute)
                },
                onLogin = { user ->
                    Log.d("GoogleAuth", "Returning User: ${user.email}")
                    userEmailToCheck = user.email
                        ?: "" // ✅ Update userEmailToCheck, let LaunchedEffect handle navigation
                },
                onShowMessage = { message ->
                    dialogMessage = message
                    showDialog = true
                },
                onError = { errorMessage ->
                    Log.e("GoogleAuth", errorMessage)
                    dialogMessage = errorMessage
                    showDialog = true
                }
            )
        }

    }
    // ✅ Show Dialog If Email Already Exists with Another Method
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Error") },
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate(AppScreens.Login.rute) // ✅ Redirect to Login
                }) {
                    Text(LanguageManager.getText("login"))
                }
            },
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Logo()
        Spacer(modifier = Modifier.height(10.dp))
        ContinueWithGoogleButton(
            onClick = {
                if (!isSigningIn) {
                    isSigningIn = true
                    activity?.let {
                        googleAuthHelper.launchSignIn(
                            activity = it,
                            onSignInStarted = { intentSender ->
                                Log.e("GoogleAuth", errorMessage)
                                googleSignInLauncher.launch(
                                    IntentSenderRequest.Builder(intentSender).build()
                                )
                            },
                            onError = { errorMessage ->
                                Log.e("GoogleAuth", errorMessage)
                            }
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(15.dp))
        EmailInput(email, onEmailChange = { email = it })
        Spacer(modifier = Modifier.height(15.dp))

        PasswordInput(password, onPasswordChange = { password = it })
        Spacer(modifier = Modifier.height(15.dp))

        ErrorMessage(errorMessage)
        RegisterButton(navController)
        RestorePasswordButton(onClick = { showResetPasswordDialog = true })
        Spacer(modifier = Modifier.height(15.dp))

        ButtonLogin(email, password, isLoading, onLoginSuccess = {
            isLoading = false  // Asegurar que la carga se detiene antes de navegar

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            userViewModel.updateLastLogin(uid)

            navController.navigate(AppScreens.Home.rute)
        }, onErrorMessageChange = {
            errorMessage = it
        }, onLoadingChange = {
            isLoading = it
        })

        if (MyApp.prefs.isBiometricEnabled() && canUseBiometrics) {
            BiometricLoginButtonWithIcon(
                navController,
                onErrorMessageChange = { errorMessage = it }
            )
        }
        if (showResetPasswordDialog) {
            RestorePassword(
                email = email,
                onDismissRequest = { showResetPasswordDialog = false }
            )
        }

        LanguageSelector {}
    }
}

@Composable
fun Logo() {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo de la aplicación",
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    )
    Image(
        painter = painterResource(id = R.drawable.nombreapp),
        contentDescription = "Nombre de la aplicación",
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    )
}

@Composable
fun EmailInput(email: String, onEmailChange: (String) -> Unit) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text(LanguageManager.getText("email")) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
    )
}

@Composable
fun PasswordInput(
    password: String,
    onPasswordChange: (String) -> Unit,
    labelPassword: String = "Contraseña"
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(LanguageManager.getText("password")) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
    )
}

@Composable
fun RegisterButton(navController: NavController) {
    Text(
        text = LanguageManager.getText("sign up"),
        modifier = Modifier
            .clickable {
                navController.navigate(AppScreens.Register.rute)
            }
            .padding(6.dp),
        color = MaterialTheme.colorScheme.onBackground,
        style = TextStyle(
            textDecoration = TextDecoration.Underline,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        )
    )
}

@Composable
fun RestorePasswordButton(onClick: () -> Unit) {
    Text(
        text = LanguageManager.getText("forgot password"),
        modifier = Modifier
            .clickable { onClick() }
            .padding(6.dp),
        color = Color.Red,
        style = TextStyle(
            textDecoration = TextDecoration.Underline,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        )
    )
}

@Composable
fun ButtonLogin(
    email: String,
    password: String,
    isLoading: Boolean,
    onLoginSuccess: () -> Unit,
    onErrorMessageChange: (String) -> Unit,
    onLoadingChange: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Image(
            painter = painterResource(id = R.drawable.go),
            contentDescription = "Botón de inicio de sesión",
            modifier = Modifier
                .height(90.dp)
                .width(180.dp)
                .clickable {
                    if (email.isBlank() || password.isBlank()) {
                        onErrorMessageChange(LanguageManager.getText("email or password empty"))
                        return@clickable
                    }
                    if (!isValidEmail(email)) {
                        onErrorMessageChange(LanguageManager.getText("email invalid"))
                        return@clickable
                    }
                    onLoadingChange(true)
                    coroutineScope.launch {
                        loginWithEmail(
                            context,
                            email,
                            password,
                            onLoginSuccess,
                            onErrorMessageChange,
                            onLoadingChange
                        )
                    }
                }
        )
    }
}


suspend fun loginWithEmail(
    context: Context,
    email: String,
    password: String,
    onLoginSuccess: () -> Unit,
    onErrorMessageChange: (String) -> Unit,
    onLoadingChange: (Boolean) -> Unit,
) {
    val auth = FirebaseAuth.getInstance()
    try {
        if (auth == null) {
            throw IllegalStateException(LanguageManager.getText("firebaseAuth not innit"))
        }
        auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
        onLoadingChange(false)  // Asegurar que se detiene la carga antes de navegar

        if (!MyApp.prefs.isAnyBiometricOn()) {
            MyApp.prefs.saveBiometricCredentials(email, password)
        }

        onLoginSuccess()
        LanguageManager.setLanguage(context)
    } catch (e: Exception) {
        val errorMsg = e.localizedMessage ?: LanguageManager.getText("login error")
        onErrorMessageChange("Credenciales Incorectas")
        onLoadingChange(false) // Asegurar que se actualiza el estado
    }
}

fun isValidEmail(email: String): Boolean {
    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    return email.matches(Regex(emailPattern))
}

@Composable
fun ErrorMessage(errorMessage: String) {
    if (errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = Color.Red,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
        )
    }
}

fun checkBiometricAvailability(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        else -> false
    }

}

