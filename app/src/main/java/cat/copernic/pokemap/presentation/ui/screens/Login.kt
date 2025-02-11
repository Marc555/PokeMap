package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cat.copernic.pokemap.R
import cat.copernic.pokemap.presentation.ui.components.RestorePassword
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun Login(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showResetPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo()
        Spacer(modifier = Modifier.height(90.dp))

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
            navController.navigate(AppScreens.Home.rute)
        }, onErrorMessageChange = {
            errorMessage = it
        }, onLoadingChange = {
            isLoading = it
        })

        if (showResetPasswordDialog) {
            RestorePassword(
                email = email,
                onDismissRequest = { showResetPasswordDialog = false }
            )
        }
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
        label = { Text("Correo electrónico") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
    )
}

@Composable
fun PasswordInput(password: String, onPasswordChange: (String) -> Unit, labelPassword: String = "Contraseña") {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(labelPassword) },
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
        text = "Crear Cuenta",
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
        text = "Recuperar Contraseña",
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

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Image(
            painter = painterResource(id = R.drawable.go),
            contentDescription = "Botón de inicio de sesión",
            modifier = Modifier
                .height(90.dp).width(180.dp)
                .clickable {
                    if (email.isBlank() || password.isBlank()) {
                        onErrorMessageChange("Correo y contraseña no pueden estar vacíos")
                        return@clickable
                    }
                    if (!isValidEmail(email)) {
                        onErrorMessageChange("Correo electrónico no válido")
                        return@clickable
                    }
                    onLoadingChange(true)
                    coroutineScope.launch {
                        loginWithEmail(email, password, onLoginSuccess, onErrorMessageChange, onLoadingChange)
                    }
                }
        )
    }
}

suspend fun loginWithEmail(
    email: String,
    password: String,
    onLoginSuccess: () -> Unit,
    onErrorMessageChange: (String) -> Unit,
    onLoadingChange: (Boolean) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    try {
        if (auth == null) {
            throw IllegalStateException("FirebaseAuth no está inicializado")
        }

        auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
        onLoadingChange(false)  // Asegurar que se detiene la carga antes de navegar
        onLoginSuccess()
    } catch (e: Exception) {
        val errorMsg = e.localizedMessage ?: "Error desconocido al iniciar sesión"
        onErrorMessageChange("Error: $errorMsg")
//      onErrorMessageChange("Credenciales Incorectas")
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
