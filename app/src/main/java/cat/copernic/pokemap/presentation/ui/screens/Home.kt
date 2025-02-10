package cat.copernic.pokemap.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.R
import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.presentation.viewModel.CategoryViewModel

@Composable
fun Home(navController: NavController) {
    val categoryViewModel: CategoryViewModel = viewModel()

    LaunchedEffect(Unit) {
        categoryViewModel.fetchCategories()
    }

    val categories by categoryViewModel.categories.collectAsState()

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showEditCategoryDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        AddCategoryButton(onClick = { showAddCategoryDialog = true },
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 25.dp, end = 15.dp))


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Titulo()

            Spacer(modifier = Modifier.height(25.dp))

            CategoryList(
                categories = categories,
                onEditCategory = { category ->
                    categoryToEdit = category
                    showEditCategoryDialog = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { newCategory ->
                categoryViewModel.addCategory(newCategory)
            }
        )
    }

    if (showEditCategoryDialog && categoryToEdit != null) {
        EditCategoryDialog(
            category = categoryToEdit!!,
            categoryViewModel = categoryViewModel,
            onDismiss = { showEditCategoryDialog = false },
            onConfirm = { updatedCategory ->
                categoryViewModel.updateCategory(updatedCategory.id, updatedCategory)
                showEditCategoryDialog = false
            }
        )
    }
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
fun EditCategoryDialog(
    category: Category,
    categoryViewModel: CategoryViewModel,
    onDismiss: () -> Unit,
    onConfirm: (Category) -> Unit
) {
    var categoryName by remember { mutableStateOf(category.name) }
    var description by remember { mutableStateOf(category.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar categoría") },
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
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        categoryViewModel.deleteCategory(category.id)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Eliminar categoría")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedCategory = category.copy(name = categoryName, description = description)
                    onConfirm(updatedCategory)
                }
            ) {
                Text("Guardar cambios")
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
fun CategoryList(
    categories: List<Category>,
    onEditCategory: (Category) -> Unit
) {
    LazyColumn(modifier = Modifier.wrapContentHeight()) {
        itemsIndexed(categories) { _, category ->
            CategoryItem(
                category = category,
                onEditCategory = onEditCategory
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onEditCategory: (Category) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Imagen de la categoría
            /*category.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagen de la categoría",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp), // Ajusta la altura según sea necesario
                    contentScale = ContentScale.Crop
                )
            }*/

            // Titulo y descripción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically, // Asegura que todo esté alineado
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f) // Permite que los textos ocupen el espacio disponible
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                // Botón de editar
                EditCategoryImage(onClick = { onEditCategory(category) })
            }
        }
    }
}



@Composable
fun EditCategoryImage(onClick: () -> Unit){
    Image(
        painter = painterResource(R.drawable.edit_icon),
        contentDescription = "Botón de editar categoria",
        modifier = Modifier
            .height(20.dp)
            .width(20.dp)
            .clickable { onClick() },
    )
}

@Composable
fun Titulo() {
    Image(
        painter = painterResource(id = R.drawable.nombreapp),
        contentDescription = "Nombre de la aplicación",
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    )
}