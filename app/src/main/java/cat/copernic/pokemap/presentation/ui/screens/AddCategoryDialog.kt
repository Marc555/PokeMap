package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.pokemap.R
import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.presentation.viewModel.CategoryViewModel

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (Category) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Agregar categoría") },
        text = {
            Column {
                TextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Nombre de la categoría") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Describe la categoría") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newCategory = Category(name = categoryName, description = description)
                    onConfirm(newCategory)
                    onDismiss()
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddCategoryButton(onClick: () -> Unit, modifier: Modifier) {
    Image(
        painter = painterResource(R.drawable.add_category_icon),
        contentDescription = "Botón de agregar categoria",
        modifier = modifier
            .height(30.dp)
            .width(30.dp)
            .clickable { onClick() },
    )
}
