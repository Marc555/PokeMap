package cat.copernic.pokemap.presentation.ui.screens

import android.net.Uri
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.R
import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.data.DTO.Rol
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.presentation.ui.components.AddCategoryDialog
import cat.copernic.pokemap.presentation.ui.components.EditCategoryDialog
import cat.copernic.pokemap.presentation.viewModel.CategoryViewModel
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.utils.LanguageManager
import com.google.firebase.auth.FirebaseAuth
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@Composable
fun Home(navController: NavController) {
    var context = LocalContext.current
    val categoryViewModel: CategoryViewModel = viewModel()
    LanguageManager.setLanguage(context)
    val usersViewModel: UsersViewModel = viewModel()

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userUid = currentUser?.uid
    val isLoading by usersViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        categoryViewModel.fetchCategories()
    }

    LaunchedEffect(userUid) {
        if (userUid != null) {
            usersViewModel.fetchUserByUid(userUid)
        }
    }

    val categories by categoryViewModel.categories.collectAsState()

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showEditCategoryDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val user by usersViewModel.user.collectAsState()

    var filter by remember { mutableStateOf<String>("") }
    var showFilters by remember { mutableStateOf(false) }

    val filteredCategories = categories.filter { item ->
        item.name.contains(filter, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Title()

                Row {
                    //Boton nueva cat
                    if (user?.rol == Rol.ADMIN) {
                        IconButton(
                            onClick = { showAddCategoryDialog = true },
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = LanguageManager.getText("add category"),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Botón para mostrar la barra de busqueda
                    IconButton(onClick = { showFilters = !showFilters }) {
                        val icon: ImageVector = if (!showFilters){
                            Icons.Default.Search
                        } else{
                            Icons.Default.Close
                        }
                        Icon(imageVector = icon, contentDescription = LanguageManager.getText("show filters"))
                    }
                }

                // Mostrar barra busqueda si el desplegable está abierto
                if (showFilters) {
                    FilterOptions(
                        filter = filter,
                        onFilterChange = { filter = it }
                    )
                }

                CategoryList(
                    navController,
                    categories = filteredCategories,
                    user = user,
                    onEditCategory = { category ->
                        categoryToEdit = category
                        showEditCategoryDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }


    if (showAddCategoryDialog) {
        AddCategoryDialog(
            errorMessage = errorMessage,
            onDismiss = {
                showAddCategoryDialog = false
                errorMessage = null
            },
            onConfirm = { categoryName, imageUri ->
                if (nameExistsAdd(categoryName, categories)) {
                    errorMessage = LanguageManager.getText("name not available")
                } else {
                    uploadCategoryImage(imageUri) { url ->
                        val newCategory = Category(
                            name = categoryName,
                            imageUrl = url
                        )
                        categoryViewModel.addCategory(newCategory)
                        showAddCategoryDialog = false
                        errorMessage = null
                    }
                }
            }
        )
    }

    if (showEditCategoryDialog && categoryToEdit != null) {
        EditCategoryDialog(
            categories = categories,
            errorMessage = errorMessage,
            category = categoryToEdit!!,
            categoryViewModel = categoryViewModel,
            onDismiss = {
                showEditCategoryDialog = false
                errorMessage = null
            },
            onConfirm = { updatedCategory, imageUri ->
                if (nameExistsEdit(updatedCategory.name, categories, updatedCategory.id)) {
                    errorMessage = LanguageManager.getText("name not available")
                } else {
                    if (imageUri == null) {
                        val category = categoryToEdit!!.copy(
                            name = updatedCategory.name,
                            imageUrl = updatedCategory.imageUrl
                        )
                        categoryViewModel.updateCategory(categoryToEdit!!.id, category)
                        showEditCategoryDialog = false
                    } else {
                        uploadCategoryImage(imageUri) { url ->
                            val category = categoryToEdit!!.copy(
                                name = updatedCategory.name,
                                imageUrl = url
                            )
                            categoryViewModel.updateCategory(categoryToEdit!!.id, category)
                            showEditCategoryDialog = false
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun CategoryList(
    navController: NavController,
    categories: List<Category>,
    user: Users?,
    onEditCategory: (Category) -> Unit
) {
    if (categories.isEmpty()) {
        Text(text = LanguageManager.getText("area empty"))
    } else {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            itemsIndexed(categories.sortedBy { it.name.lowercase() }) { _, category ->
                CategoryItem(
                    navController = navController,
                    category = category,
                    user = user,
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
    user: Users?,
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
                contentDescription = LanguageManager.getText("category image"),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f), // Ajustar imagen a toda la tarjeta
                contentScale = ContentScale.Crop
            )

            // Titulo y descripción
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

                if (user?.rol == Rol.ADMIN) {
                    // Botón de editar
                    EditCategoryIcon(onClick = { onEditCategory(category) })
                }

            }
        }
    }
}

@Composable
fun EditCategoryIcon(onClick: () -> Unit){
    IconButton(onClick = onClick) {
        Icon(imageVector = Icons.Default.Edit,
            contentDescription = LanguageManager.getText("edit category"),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterOptions(
    filter: String,
    onFilterChange: (String) -> Unit,
){
    OutlinedTextField(
        value = filter,
        onValueChange = onFilterChange,
        label = { Text(LanguageManager.getText("search")) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
    )
}

@Composable
fun Title() {
    Image(
        painter = painterResource(id = R.drawable.nombreapp),
        contentDescription = "PokeMap",
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
    )
}

fun nameExistsAdd(name: String, categories: List<Category>): Boolean {
    return categories.any { it.name.equals(ignoreCase = true, other = name)  }
}

fun nameExistsEdit(name: String, categories: List<Category>, currentCategoryId: String): Boolean {
    return categories.any { it.id != currentCategoryId && it.name.equals(ignoreCase = true, other = name) }
}

fun uploadCategoryImage(uri: Uri, onSuccess: (String) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("categories/${UUID.randomUUID()}.jpg")

    imageRef.putFile(uri)
        .addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { url ->
                onSuccess(url.toString())
            }
        }
        .addOnFailureListener {
            println("Error al subir la imagen: ${it.message}")
        }
}

