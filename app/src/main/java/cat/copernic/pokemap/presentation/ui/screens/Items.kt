package cat.copernic.pokemap.presentation.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.presentation.ui.components.AddItemDialog
import cat.copernic.pokemap.presentation.ui.components.EditItemDialog
import cat.copernic.pokemap.presentation.viewModel.ItemViewModel
import cat.copernic.pokemap.utils.LanguageManager
import com.google.firebase.auth.FirebaseAuth
import cat.copernic.pokemap.data.DTO.Item
import cat.copernic.pokemap.data.DTO.Rol
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.auth.User

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Items(
    navController: NavController,
    categoryId: String,
    userId: String? = FirebaseAuth.getInstance().currentUser?.uid
) {

    val userId = userId ?: run {
        navController.navigate(AppScreens.Login.rute){
            popUpTo(AppScreens.Login.rute) {inclusive = true}
        }

    }

    val itemViewModel: ItemViewModel = viewModel()
    val usersViewModel: UsersViewModel = viewModel()

    LaunchedEffect(Unit) {
        itemViewModel.getCategoryById(categoryId)
        itemViewModel.fetchItems(categoryId)
        usersViewModel.fetchUserByUid(userId.toString())
    }

    val category by itemViewModel.category.collectAsState()
    val items by itemViewModel.items.collectAsState()
    val user by usersViewModel.user.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }
    var showEditItemDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<Item?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var filter by remember { mutableStateOf<String>("") }
    var minDistance by remember { mutableStateOf(0f) }  // Mínimo de distancia
    var minLikes by remember { mutableStateOf(0) }      // Mínimo de likes
    var showFilters by remember { mutableStateOf(false) } // Estado del desplegable de filtros

    // Filtrando los items según el texto, distancia y likes
    val filteredItems = items.filter { item ->
        item.name.contains(filter, ignoreCase = true) &&
                item.likes >= minLikes //&&
                //calculateDistance(item) >= minDistance
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Title()

        category?.let { CategoryName(it) }

        Row {
            //Boton nuevo item
            IconButton(onClick = { showAddItemDialog = true }) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = LanguageManager.getText("add item"),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón para mostrar los filtros
            IconButton(onClick = { showFilters = !showFilters }) {
                val icon: ImageVector = if (!showFilters){
                    Icons.Default.Search
                } else{
                    Icons.Default.Close
                }
                Icon(imageVector = icon, contentDescription = LanguageManager.getText("show filters"))
            }
        }

        // Mostrar filtros si el desplegable está abierto
        if (showFilters) {
            FilterOptions(
                filter = filter,
                onFilterChange = { filter = it },
                minDistance = minDistance,
                onMinDistanceChange = { minDistance = it },
                minLikes = minLikes,
                onMinLikesChange = { minLikes = it }
            )
        }

        user?.let {
            ItemsList(navController, it, userId.toString(), filteredItems) { item ->
                itemToEdit = item
                showEditItemDialog = true
            }
        }

        //Mostrar menu de nuevo item
        if (showAddItemDialog) {
            AddItemDialog(
                context =  LocalContext.current,
                errorMessage = errorMessage,
                onDismiss = {
                    showAddItemDialog = false
                    errorMessage = null
                },
                onConfirm = { itemName, description, latitude, longitude, imageUrl ->
                    if (!itemExistsAdd(itemName, items)) {
                        val newItem =
                            Item(
                                name = itemName,
                                description = description,
                                categoryId = categoryId,
                                userId = getCurrentUserId(),
                                latitude = latitude,
                                longitude = longitude,
                                imageUrl = imageUrl,
                            )
                        itemViewModel.addItem(newItem, categoryId)
                        showAddItemDialog = false
                        errorMessage = null
                    } else {
                        errorMessage = LanguageManager.getText("name not available")
                    }
                },
            )
        }

        //Mostrar menu de editar item
        if (showEditItemDialog && itemToEdit != null) {
            EditItemDialog(
                context =  LocalContext.current,
                errorMessage = errorMessage,
                item = itemToEdit!!,
                itemViewModel = itemViewModel,
                onDismiss = {
                    showEditItemDialog = false
                    errorMessage = null
                },
                onConfirm = { updatedItem, categoryId ->
                    if (!itemExistsEdit(updatedItem.name, items, updatedItem.id)) {
                        itemViewModel.updateItem(updatedItem.id, updatedItem, categoryId)
                        showEditItemDialog = false
                    } else {
                        errorMessage = LanguageManager.getText("name not available")
                    }
                }
            )
        }
    }
}

@Composable
fun FilterOptions(
    filter: String,
    onFilterChange: (String) -> Unit,
    minDistance: Float,
    onMinDistanceChange: (Float) -> Unit,
    minLikes: Int,
    onMinLikesChange: (Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(width = 2.dp, color = MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(20.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Filtro por nombre
            SectionTitle(title = LanguageManager.getText("filter by name"))
            OutlinedTextField(
                value = filter,
                onValueChange = onFilterChange,
                label = { Text(LanguageManager.getText("search")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Filtro por distancia
            SectionTitle(title = LanguageManager.getText("filter by distance"))
            Text(
                text = "${minDistance.toInt()} km",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = minDistance,
                onValueChange = onMinDistanceChange,
                valueRange = 0f..1000f,
                steps = 999,
                modifier = Modifier
                    .fillMaxWidth()
            )

            // Filtro por likes
            SectionTitle(title = LanguageManager.getText("filter by likes"))
            Text(
                text = "$minLikes likes",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = minLikes.toFloat(),
                onValueChange = { newValue -> onMinLikesChange(newValue.toInt()) },
                valueRange = 0f..1000f,
                steps = 999,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    val colorScheme = MaterialTheme.colorScheme
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurfaceVariant
        ),
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
    )
}

@Composable
fun ItemsList(
    navController: NavController,
    user: Users,
    userId: String?,
    items: List<Item>,
    onEditItem: (Item) -> Unit
) {
    if (items.isEmpty()) {
        Text(
            text = LanguageManager.getText("area empty"),
            modifier = Modifier.padding(start = 35.dp)
        )
    } else {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            itemsIndexed(items) { _, item ->
                ItemCard(
                    userId = userId,
                    user = user,
                    item = item,
                    onClick = { navController.navigate("itemInside/${item.id}") },
                    onEditClick = { onEditItem(item) },
                )
            }
        }
    }
}


@Composable
fun ItemCard(item: Item, onClick: () -> Unit, onEditClick: () -> Unit, user: Users, userId: String?) {
    val imageUrl = item.imageUrl
    val painter = rememberAsyncImagePainter(model = imageUrl)
    val fondoTexto = MaterialTheme.colorScheme.surface
    val colorTexto = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painter,
                contentDescription = LanguageManager.getText("image"),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f), // Ajuste de la imagen
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(fondoTexto) // Fondo de la parte inferior
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = colorTexto, // Color del texto
                    modifier = Modifier.weight(1f)
                )

                if(item.userId == userId || user.rol == Rol.ADMIN){
                    IconButton(onClick = { onEditClick() }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = LanguageManager.getText("edit item"),
                            tint = colorTexto
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryName(category: Category) {
    Text(
        text = category.name,
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 50.sp
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 10.dp)
    )
}

fun itemExistsAdd(name: String, items: List<Item>): Boolean {
    return items.any { it.name.equals(ignoreCase = true, other = name) }
}

fun itemExistsEdit(name: String, items: List<Item>, id: String): Boolean {
    return items.any { it.name.equals(ignoreCase = true, other = name) && it.id != id }
}

fun getCurrentUserId(): String{
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userUid = currentUser?.uid

    return userUid.toString()
}
