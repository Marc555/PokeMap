package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.R
import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.presentation.ui.components.AddCategoryDialog
import cat.copernic.pokemap.presentation.ui.components.EditCategoryDialog
import cat.copernic.pokemap.presentation.viewModel.CategoryViewModel
import cat.copernic.pokemap.utils.LanguageManager
import coil.compose.rememberAsyncImagePainter

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
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Title()

            IconButton(onClick = { showAddCategoryDialog = true }, modifier = Modifier.align(Alignment.Start)) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = LanguageManager.getText("add category"),
                )
            }

            CategoryList(
                navController = navController,
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
            errorMessage = errorMessage,
            onDismiss = {
                showAddCategoryDialog = false
                errorMessage = null
            },
            onConfirm = { categoryName, description, imageUrl ->
                if (!nameExistsAdd(categoryName, categories)) {
                    val newCategory = Category(
                        name = categoryName,
                        description = description,
                        imageUrl = imageUrl
                    )
                    categoryViewModel.addCategory(newCategory)
                    showAddCategoryDialog = false
                    errorMessage = null
                } else {
                    errorMessage = LanguageManager.getText("name not available")
                }
            }
        )
    }

    if (showEditCategoryDialog && categoryToEdit != null) {
        EditCategoryDialog(
            errorMessage = errorMessage,
            category = categoryToEdit!!,
            categoryViewModel = categoryViewModel,
            onDismiss = {
                showEditCategoryDialog = false
                errorMessage = null
            },
            onConfirm = { updatedCategory ->
                if (!nameExistsEdit(updatedCategory.name, categories, updatedCategory.id)) {
                    categoryViewModel.updateCategory(updatedCategory.id, updatedCategory)
                    showEditCategoryDialog = false
                } else {
                    errorMessage = LanguageManager.getText("name not available")
                }
            }
        )
    }
}

@Composable
fun CategoryList(
    navController: NavController,
    categories: List<Category>,
    onEditCategory: (Category) -> Unit
) {
    if (categories.isEmpty()) {
        Text(text = LanguageManager.getText("area empty"))
    } else {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            itemsIndexed(categories) { _, category ->
                CategoryItem(
                    navController = navController,
                    category = category,
                    onEditCategory = onEditCategory
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    navController: NavController,
    category: Category,
    onEditCategory: (Category) -> Unit
) {
    val imageUrl = category.imageUrl
    val painter = rememberAsyncImagePainter(model = imageUrl)
    val fondoTexto = MaterialTheme.colorScheme.surface
    val colorTexto = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("items/${category.id}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painter,
                contentDescription = "Imagen de la categoría",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f), // Ajustar imagen a toda la tarjeta
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(fondoTexto) //Color del fondo del texto
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = colorTexto, //Color del texto
                    modifier = Modifier.weight(1f)
                )

                EditCategoryIcon(onClick = { onEditCategory(category) })
            }
        }
    }
}

@Composable
fun EditCategoryIcon(onClick: () -> Unit){
    IconButton(onClick = onClick) {
        Icon(imageVector = Icons.Default.Edit,
            contentDescription = "Editar categoria",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun Title() {
    Image(
        painter = painterResource(id = R.drawable.nombreapp),
        contentDescription = "Nombre de la aplicación",
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
    )
}

fun nameExistsAdd(name: String, categories: List<Category>): Boolean {
    return categories.any { it.name == name }
}

fun nameExistsEdit(name: String, categories: List<Category>, currentCategoryId: String): Boolean {
    return categories.any { it.id != currentCategoryId && it.name == name }
}
