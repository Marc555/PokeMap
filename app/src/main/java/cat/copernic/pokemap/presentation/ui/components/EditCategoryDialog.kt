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
import coil.compose.rememberAsyncImagePainter
import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.presentation.ui.screens.nameExistsEdit
import cat.copernic.pokemap.presentation.ui.screens.uploadCategoryImage
import cat.copernic.pokemap.presentation.ui.theme.LocalCustomColors
import cat.copernic.pokemap.presentation.viewModel.CategoryViewModel
import cat.copernic.pokemap.utils.LanguageManager

@Composable
fun EditCategoryDialog(
    categories: List<Category>,
    errorMessage: String?,
    category: Category,
    categoryViewModel: CategoryViewModel,
    onDismiss: () -> Unit,
    onConfirm: (Category) -> Unit
) {
    val customColors = LocalCustomColors.current

    var categoryName by remember { mutableStateOf(category.name) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf(category.imageUrl) }

    var localErrorMessage by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    // Estado para la confirmación de eliminación
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { imageUri = it } }

    // Mostrar el dialogo de confirmación de eliminación
    if (showDeleteConfirmation) {
        ConfirmDeleteDialog(
            title = LanguageManager.getText("delete confirmation"),
            message = LanguageManager.getText("delete question"),
            onConfirm = {
                categoryViewModel.deleteCategory(category.id)
                onDismiss()
                showDeleteConfirmation = false
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }

    AlertDialog(
        containerColor = customColors.popUpsMenu,
        onDismissRequest = onDismiss,
        title = { Text(text = LanguageManager.getText("edit category")) },
        text = {
            Column {
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
                localErrorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }

                // Contador de caracteres
                Text(
                    text = "${categoryName.length}/50",
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
                            contentDescription = LanguageManager.getText("selected image"),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (imageUrl.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUrl),
                            contentDescription = LanguageManager.getText("current image"),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(LanguageManager.getText("select image"), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        if (nameExistsEdit(category.name, categories, category.id)){
                            localErrorMessage = "Nombre existe"
                        } else if (imageUri != null) {
                            uploadCategoryImage(imageUri!!) { url ->
                                val updatedCategory = category.copy(
                                    name = categoryName,
                                    imageUrl = url
                                )
                                onConfirm(updatedCategory)
                                isUploading = false
                            }
                        } else {
                            val updatedCategory = category.copy(
                                name = categoryName,
                                imageUrl = imageUrl
                            )
                            isUploading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = customColors.confirmButton,
                    ),
                    enabled = !isUploading
                ) {
                    Text(text= LanguageManager.getText("save"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = customColors.deleteButton,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                ) {
                    Text(text = LanguageManager.getText("delete"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    )
}

