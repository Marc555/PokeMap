package cat.copernic.pokemap.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cat.copernic.pokemap.presentation.ui.screens.EmailInput
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RestorePassword(
    email: String,
    onDismissRequest: () -> Unit
) {
    var email by remember { mutableStateOf(email) }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Box(
            modifier = Modifier
                .size(300.dp)  // Tamaño del contenedor del diálogo
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextTitulo()

                Spacer(modifier = Modifier.height(16.dp))

                EmailInput(email, onEmailChange = { email = it })

                Spacer(modifier = Modifier.height(16.dp))

                SendButton(email, onMessageChange = { errorMessage = it }, onDismissRequest)

                Spacer(modifier = Modifier.height(8.dp))

                TextError(errorMessage)
            }
        }
    }
}

@Composable
fun TextTitulo(){
    Text(
        text = "Escribe el correo de tu cuenta",
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun TextError(errorMessage: String){
    Text(
        text = errorMessage,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
fun SendButton(email: String, onMessageChange: (String) -> Unit, onDismissRequest: () -> Unit) {
    Button(
        onClick = {
            sendPasswordResetEmail(email) { isSuccessful ->
                if (isSuccessful) {
                    onDismissRequest()
                    onMessageChange("Correo enviado")
                } else {
                    onMessageChange("Error al enviar el correo")
                }
            }
        }
    ) {
        Text(text = "Enviar")
    }
}

fun sendPasswordResetEmail(
    email: String,
    callback: (Boolean) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    auth.sendPasswordResetEmail(email.trim())
        .addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
}
