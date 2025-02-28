package cat.copernic.pokemap.presentation.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cat.copernic.pokemap.presentation.ui.theme.LocalCustomColors
import cat.copernic.pokemap.utils.LanguageManager
import coil.compose.rememberAsyncImagePainter

@Composable
fun AddCategoryDialog(
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, Uri) -> Unit
) {
    val customColors = LocalCustomColors.current // Para colores personalizados

    var categoryName by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            localErrorMessage = null
        }
    }

    AlertDialog(
        containerColor = customColors.popUpsMenu,
        onDismissRequest = onDismiss,
        title = { Text(text = LanguageManager.getText("add category"), color = MaterialTheme.colorScheme.onSurfaceVariant) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = categoryName,
                    onValueChange = {
                        if (it.length <= 15) { // Límite de 50 caracteres
                            categoryName = it
                        }
                    },
                    label = { Text(LanguageManager.getText("name")) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                // Contador de caracteres
                Text(
                    text = "${categoryName.length}/15",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )

                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUri),
                            contentDescription = LanguageManager.getText("select image"),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(LanguageManager.getText("select image"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                localErrorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(categoryName, imageUri!!) },
                enabled = categoryName.isNotBlank()&& imageUri != null && !isUploading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = customColors.confirmButton,
                ),
                modifier = Modifier.fillMaxWidth()

            ) {
                Text(text= LanguageManager.getText("save"), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}


