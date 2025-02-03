package cat.copernic.pokemap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cat.copernic.pokemap.navigation.AppScreens

@Composable
fun Register(navController: NavController) {

    var orden: Int by remember { mutableStateOf(1) }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var usurname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var messageError by remember { mutableStateOf("") }
    var ifError by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo()
        when (orden) {
            1 -> PantallaRegistro1(email, onEmailChange = { email = it }, onMensajeErrorChange = { messageError = it }, ifError, onIfErrorChange = { ifError = it }) { orden = 2 }
            2 -> PantallaRegistro2(name, surname, onNameChange = { name = it }, onSurnameChange = { surname = it }, onMensajeErrorChange = { messageError = it }, ifError, onIfErrorChange = { ifError = it }) { orden = 3 }
            3 -> PantallaRegistro3(usurname, onUsurnameChange = { usurname = it }, onMensajeErrorChange = { messageError = it }, ifError, onIfErrorChange = { ifError = it }) { orden = 4 }
            4 -> PantallaRegistro4(password, onPasswordChange = { password = it }, confirmPassword, onConfirmPasswordChange = { confirmPassword = it }, onMensajeErrorChange = { messageError = it }, ifError, onIfErrorChange = { ifError = it }) { orden = 5 }
        }
        ErrorMessage(messageError)
    }
}

@Composable
fun TextoTitulo(text: String){
    Text(text)
}

@Composable
fun PantallaRegistro1(email: String, onEmailChange: (String) -> Unit, onMensajeErrorChange: (String) -> Unit, ifError: Boolean, onIfErrorChange: (Boolean) -> Unit, onClick: () -> Unit){
    TextoTitulo("Ingresa tu email:")
    EmailInput(email, onEmailChange)
    if (email.isEmpty()) {
        onMensajeErrorChange("")
        onIfErrorChange(true)
    }else if (!isValidEmail(email)) {
        onMensajeErrorChange("Correo electrónico no válido")
        onIfErrorChange(true)
    }else{onIfErrorChange(false)
        onMensajeErrorChange("")
    }
    BotonSiguiente(onClick = onClick, ifError)
}

@Composable
fun PantallaRegistro2(name: String, surname: String, onNameChange: (String) -> Unit, onSurnameChange: (String) -> Unit, onMensajeErrorChange: (String) -> Unit, ifError: Boolean, onIfErrorChange: (Boolean) -> Unit, onClick: () -> Unit){
    TextoTitulo("Ahora dinos como te llamas::")
    NameInput(name, onNameChange)
    SurnameInput(surname, onSurnameChange)
    if (name.isEmpty() || surname.isEmpty()) {
        onMensajeErrorChange("")
        onIfErrorChange(true)
    } else{onIfErrorChange(false)
        onMensajeErrorChange("")
    }
    BotonSiguiente(onClick = onClick, ifError)
}

@Composable
fun NameInput(name: String, onNameChange: (String) -> Unit){
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Nombre") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
    )
}

@Composable
fun SurnameInput(surname: String, onSurnameChange: (String) -> Unit){
    OutlinedTextField(
        value = surname,
        onValueChange = onSurnameChange,
        label = { Text("Apellidos") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
    )
}

@Composable
fun PantallaRegistro3(usurname: String, onUsurnameChange: (String) -> Unit, onMensajeErrorChange: (String) -> Unit, ifError: Boolean, onIfErrorChange: (Boolean) -> Unit, onClick: () -> Unit){
    TextoTitulo("Elige un nombre de usuario:")
        UsernameInput(usurname, onUsurnameChange)
    if (usurname.isEmpty()) {
        onMensajeErrorChange("")
        onIfErrorChange(true)
    } else{onIfErrorChange(false)
        onMensajeErrorChange("")
    }
    BotonSiguiente(onClick = onClick, ifError)
}

@Composable
fun UsernameInput(usurname: String, onUsurnameChange: (String) -> Unit){
    OutlinedTextField(
        value = usurname,
        onValueChange = onUsurnameChange,
        label = { Text("Nombre de usuario") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
    )
}

@Composable
fun PantallaRegistro4(password: String, onPasswordChange: (String) -> Unit, confirmPassword: String, onConfirmPasswordChange: (String) -> Unit, onMensajeErrorChange: (String) -> Unit, ifError: Boolean, onIfErrorChange: (Boolean) -> Unit, onClick: () -> Unit
){
    TextoTitulo("Elige una contraseña:")
    PasswordInput(password, onPasswordChange, "Contraseña")
    PasswordInput(confirmPassword, onConfirmPasswordChange, "Confirmar contraseña")
    if (password.isEmpty() || confirmPassword.isEmpty()) {
        onMensajeErrorChange("")
        onIfErrorChange(true)
    } else if (password != confirmPassword) {
        onIfErrorChange(true)
        onMensajeErrorChange("Las contraseñas no coinciden")
    } else{onIfErrorChange(false)
        onMensajeErrorChange("")
    }
    BotonSiguiente(onClick = onClick, ifError)
}

@Composable
fun BotonSiguiente(onClick: () -> Unit, ifError: Boolean){
    Button(onClick = {
        if (!ifError) {
            onClick()
        }
    }
    ) {
        Text(text = "Siguiente")
    }
}