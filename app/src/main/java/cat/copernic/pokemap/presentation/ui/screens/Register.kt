package cat.copernic.pokemap.presentation.ui.screens

import RegisterViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.presentation.viewModel.AuthViewModel
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.data.Repository.UsersRepository
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import cat.copernic.pokemap.utils.LanguageManager
import kotlinx.coroutines.launch

@Composable
fun Register(navController: NavController, viewModel: RegisterViewModel = viewModel()) {
    Box (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        // Botón "Atrás" en la esquina superior izquierda
        BotonAtras(
            navController = navController,
            modifier = Modifier
                .align(Alignment.TopStart) // Alinea el botón en la esquina superior izquierda
                .padding(16.dp) // Añade un padding para que no esté pegado al borde
        )

        // Contenido principal centrado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Logo()
            when (viewModel.orden) {
                1 -> PantallaRegistro1(
                    viewModel.email,
                    onEmailChange = { viewModel.email = it },
                    onMensajeErrorChange = { viewModel.messageError = it }
                ) { viewModel.orden = 2 }

                2 -> PantallaRegistro2(
                    viewModel.name,
                    viewModel.surname,
                    onNameChange = { viewModel.name = it },
                    onSurnameChange = { viewModel.surname = it },
                    onMensajeErrorChange = { viewModel.messageError = it }
                ) { viewModel.orden = 3 }

                3 -> PantallaRegistro3(
                    viewModel.username,
                    onUsernameChange = { viewModel.username = it },
                    onMensajeErrorChange = { viewModel.messageError = it }
                ) { viewModel.orden = 4 }

                4 -> PantallaRegistro4(
                    viewModel.password,
                    onPasswordChange = { viewModel.password = it },
                    viewModel.confirmPassword,
                    onConfirmPasswordChange = { viewModel.confirmPassword = it },
                    onMensajeErrorChange = { viewModel.messageError = it }
                ) { viewModel.orden = 5 }

                5 -> PantallaRegistroFinal(
                    viewModel.email,
                    viewModel.password,
                    viewModel.username,
                    viewModel.name,
                    viewModel.surname,
                    onMensajeErrorChange = { viewModel.messageError = it },
                    navController = navController
                )
            }
            ErrorMessage(viewModel.messageError)
        }
    }
}

@Composable
fun BotonAtras(navController: NavController,modifier: Modifier = Modifier, viewModel: RegisterViewModel = viewModel()) {
    Text(
        text = "<- "+LanguageManager.getText("back"),
        color = MaterialTheme.colorScheme.onBackground, // Color del texto
        modifier = modifier
            .clickable {
                viewModel.orden--
                if (viewModel.orden < 1) {
                    navController.navigate(AppScreens.Login.rute)
                }
            }
            .padding(8.dp) // Añade un padding para que sea más fácil de tocar
    )
}

@Composable
fun TextoTitulo(text: String){
    Text(text, color = MaterialTheme.colorScheme.onBackground)
}

@Composable
fun PantallaRegistro1(
    email: String,
    onEmailChange: (String) -> Unit,
    onMensajeErrorChange: (String) -> Unit,
    viewModel: UsersViewModel = viewModel(),
    onClick: () -> Unit
) {
    var isChecking by remember { mutableStateOf(false) }

    TextoTitulo(LanguageManager.getText("type email"))
    EmailInput(email, onEmailChange)

    BotonSiguiente(
        onClick = {
            if (email.isEmpty()) {
                onMensajeErrorChange(LanguageManager.getText("email empty"))
            } else if (!isValidEmail(email)) {
                onMensajeErrorChange(LanguageManager.getText("email invalid"))
            } else {
                isChecking = true // Indica que estamos verificando
                viewModel.viewModelScope.launch {
                    val existe = isRepeatingEmail(email)
                    isChecking = false // La verificación ha terminado
                    if (existe) {
                        onMensajeErrorChange(LanguageManager.getText("email taken"))
                    } else {
                        onMensajeErrorChange("") // Limpia el mensaje de error
                        onClick() // Continúa con el flujo normal
                    }
                }
            }
        },
        isChecking = isChecking // Deshabilitar el botón mientras se verifica
    )
}


@Composable
fun PantallaRegistro2(name: String, surname: String, onNameChange: (String) -> Unit, onSurnameChange: (String) -> Unit, onMensajeErrorChange: (String) -> Unit, onClick: () -> Unit){
    TextoTitulo(LanguageManager.getText("name and lastname"))
    NameInput(name, onNameChange)
    SurnameInput(surname, onSurnameChange)
    BotonSiguiente(onClick = {
        if (name.isEmpty() || surname.isEmpty()) {
            onMensajeErrorChange("")
        } else{
            onMensajeErrorChange("")
            onClick()
        }
    })
}

@Composable
fun NameInput(name: String, onNameChange: (String) -> Unit){
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text(LanguageManager.getText("name")) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
    )
}

@Composable
fun SurnameInput(surname: String, onSurnameChange: (String) -> Unit){
    OutlinedTextField(
        value = surname,
        onValueChange = onSurnameChange,
        label = { Text(LanguageManager.getText("lastname")) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
    )
}

@Composable
fun PantallaRegistro3(username: String, viewModel: UsersViewModel = viewModel(), onUsernameChange: (String) -> Unit, onMensajeErrorChange: (String) -> Unit, onClick: () -> Unit){
    TextoTitulo(LanguageManager.getText( "choose username"))
    UsernameInput(username, onUsernameChange)

    var isChecking by remember { mutableStateOf(false) }

    BotonSiguiente(onClick = {
        if (username.isEmpty()) {
            onMensajeErrorChange("")
        } else{
            isChecking = true // Indica que estamos verificando
            viewModel.viewModelScope.launch {
                val existe = isRepeatingUsername(username)
                isChecking = false // La verificación ha terminado
                if (existe) {
                    onMensajeErrorChange(LanguageManager.getText( "username taken"))
                } else {
                    onMensajeErrorChange("") // Limpia el mensaje de error
                    onClick() // Continúa con el flujo normal
                }
            }
        }
    },isChecking = isChecking)
}

@Composable
fun UsernameInput(username: String, onUsernameChange: (String) -> Unit){
    OutlinedTextField(
        value = username,
        onValueChange = onUsernameChange,
        label = { LanguageManager.getText( "username") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
    )
}

@Composable
fun PantallaRegistro4(password: String, onPasswordChange: (String) -> Unit, confirmPassword: String, onConfirmPasswordChange: (String) -> Unit, onMensajeErrorChange: (String) -> Unit, onClick: () -> Unit
){
    TextoTitulo(LanguageManager.getText( "create password"))
    PasswordInput(password, onPasswordChange, LanguageManager.getText( "password"))
    PasswordInput(confirmPassword, onConfirmPasswordChange, LanguageManager.getText( "confirm password"))
    BotonSiguiente(onClick = {
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            onMensajeErrorChange("")
        } else if (password.trim().length >= 6) {
            onMensajeErrorChange(LanguageManager.getText( "The password is to short"))
        }else if (password != confirmPassword) {
            onMensajeErrorChange(LanguageManager.getText( "password not match"))
        } else{
            onMensajeErrorChange("")
            onClick()
        }
    })
}

@Composable
fun PantallaRegistroFinal(
    email: String,
    password: String,
    username: String,
    name: String,
    surname: String,
    viewModel: AuthViewModel = viewModel(),
    onMensajeErrorChange: (String) -> Unit,
    navController: NavController
) {
    var registroExitoso by remember { mutableStateOf<Boolean?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.registerUser(
            email,
            password,
            username,
            name,
            surname
        ) { success -> registroExitoso = success }
    }

    if (isLoading) {
        CircularProgressIndicator() // Indicador de carga
    } else {
        when (registroExitoso) {
            true -> {
                // Registro exitoso, navegar a la siguiente pantalla
                TextoTitulo(LanguageManager.getText( "sing up success message"))
                BotonSiguiente(
                    onClick = { navController.navigate(AppScreens.Home.rute) },
                    text = LanguageManager.getText( "finish")
                )
            }
            false -> {
                // Mostrar mensaje de error
                onMensajeErrorChange(LanguageManager.getText( "error message"))
                BotonSiguiente(
                    onClick = { navController.navigate(AppScreens.Login.rute) },
                    text = LanguageManager.getText( "go back")
                )
            }
            null -> {
                // Estado inicial, no hacer nada
            }
        }
    }
}

suspend fun isRepeatingEmail(email: String, userRepository: UsersRepository = UsersRepository()): Boolean {
    return try {
        val users = userRepository.getUsers()
        users.any { it.email.equals(email, ignoreCase = true) }
    } catch (e: Exception) {
        false
    }
}

suspend fun isRepeatingUsername(username: String, userRepository: UsersRepository = UsersRepository()): Boolean {
    return try {
        val users = userRepository.getUsers()
        users.any { it.username.equals(username, ignoreCase = true) }
    } catch (e: Exception) {
        false
    }
}

@Composable
fun BotonSiguiente(onClick: () -> Unit, text: String = LanguageManager.getText( "next"), isChecking: Boolean = false) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary) ,
        enabled = !isChecking // Deshabilitar mientras se verifica
    ) {
        Text(if (isChecking) LanguageManager.getText( "loading") else LanguageManager.getText( "next"))
    }
}
