package cat.copernic.pokemap.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.presentation.viewModel.CategoryViewModel
import cat.copernic.pokemap.utils.LanguageManager

@Composable
fun EditCategoryDialog(
    errorMessage: String?,
    category: Category,
    categoryViewModel: CategoryViewModel,
    onDismiss: () -> Unit,
    onConfirm: (Category) -> Unit
) {
    var categoryName by remember { mutableStateOf(category.name) }
    var description by remember { mutableStateOf(category.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = LanguageManager.getText("edit category")) },
        text = {
            Column {
                TextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text(LanguageManager.getText("name")) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)

                )
                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(LanguageManager.getText("description")) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )
            }
        },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Botón de guardar cambios
                Button(
                    onClick = {
                        val updatedCategory = category.copy(name = categoryName, description = description)
                        onConfirm(updatedCategory)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text(LanguageManager.getText("save"))
                }

                // Botón de eliminar
                Button(
                    onClick = {
                        categoryViewModel.deleteCategory(category.id)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = LanguageManager.getText("delete"), color = MaterialTheme.colorScheme.onError)
                }
            }
        }, containerColor = MaterialTheme.colorScheme.background
    )
}
