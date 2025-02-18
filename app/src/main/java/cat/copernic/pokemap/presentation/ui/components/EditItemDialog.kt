package cat.copernic.pokemap.presentation.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cat.copernic.pokemap.data.DTO.Item
import cat.copernic.pokemap.presentation.viewModel.ItemViewModel
import cat.copernic.pokemap.utils.LanguageManager
import coil.compose.rememberAsyncImagePainter

@Composable
fun EditItemDialog(
    errorMessage: String?,
    item: Item,
    itemViewModel: ItemViewModel,
    onDismiss: () -> Unit,
    onConfirm: (Item, String) -> Unit
) {
    var itemName by remember { mutableStateOf(item.name) }
    var description by remember { mutableStateOf(item.description) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf(item.imageUrl) }

    var localErrorMessage by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    // Estado para la confirmación de eliminación
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
        }
    }

    // Mostrar el dialogo de confirmación de eliminación
    if (showDeleteConfirmation) {
        ConfirmDeleteDialog(
            title = "Confirmar eliminación",
            message = "¿Estás seguro de que deseas eliminar este ítem?",
            onConfirm = {
                itemViewModel.deleteItem(item.id, item.categoryId)
                onDismiss()
                showDeleteConfirmation = false
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = LanguageManager.getText("edit item")) },
        text = {
            Column {
                TextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text(LanguageManager.getText("name")) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )
                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(5.dp))

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(LanguageManager.getText("description")) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUri),
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (imageUrl.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUrl),
                            contentDescription = "Imagen actual",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("Seleccionar imagen", color = Color.DarkGray)
                    }
                }
                localErrorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }
            }
        },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        isUploading = true

                        if (imageUri != null) {
                            uploadCategoryImage(imageUri!!) { url ->
                                val updatedItem = item.copy(
                                    name = itemName,
                                    description = description,
                                    imageUrl = url
                                )
                                onConfirm(updatedItem, item.categoryId)
                                isUploading = false
                            }
                        } else {
                            val updatedItem = item.copy(
                                name = itemName,
                                description = description,
                                imageUrl = imageUrl
                            )
                            onConfirm(updatedItem, item.categoryId)
                            isUploading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(),
                    enabled = !isUploading
                ) {
                    Text(LanguageManager.getText("save"))
                }

                Button(
                    onClick = { showDeleteConfirmation = true }, // Mostrar la confirmación
                    colors = ButtonDefaults.buttonColors(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                ) {
                    Text(text = LanguageManager.getText("delete"), color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    )
}
