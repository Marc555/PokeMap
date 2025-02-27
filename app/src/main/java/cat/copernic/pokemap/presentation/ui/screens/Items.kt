package cat.copernic.pokemap.presentation.ui.screens

import android.location.Location
import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import cat.copernic.pokemap.presentation.ui.theme.LocalCustomColors
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.utils.rememberCurrentLocation
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

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

    val context = LocalContext.current
    val userLocation = rememberCurrentLocation(context)

    var showAddItemDialog by remember { mutableStateOf(false) }
    var showEditItemDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<Item?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var filter by remember { mutableStateOf<String>("") }
    var minDistance by remember { mutableStateOf(0f) }  // Mínimo de distancia
    var minLikes by remember { mutableStateOf(0) }      // Mínimo de likes
    var showFilters by remember { mutableStateOf(false) } // Estado del desplegable de filtros
    var showSortMenu by remember { mutableStateOf(false) } // Menú de ordenación
    var sortOption by remember { mutableStateOf<String>("") } // Opción de ordenación seleccionada

    val sortedItems = when (sortOption) {
        "Ordenar de menor a mayor distancia" -> items.sortedBy { calculateDistance(userLocation!!, it) }
        "Ordenar de mayor a menor distancia" -> items.sortedByDescending { calculateDistance(userLocation!!, it) }
        "Ordenar de menor a mayor numero de likes" -> items.sortedBy { it.likes }
        "Ordenar de mayor a menor numero de likes" -> items.sortedByDescending { it.likes }
        "Ordenar de menor a mayor numero de dislikes" -> items.sortedBy { it.dislikes }
        "Ordenar de mayor a menor numero de dislikes" -> items.sortedByDescending { it.dislikes }
        "Ordenar por fecha de mayor a menor" -> items.sortedByDescending { it.creationDate }
        "Ordenar por fecha de menor a mayor" -> items.sortedBy { it.creationDate }
        else -> items
    }

    // Filtrando los items según el texto, distancia y likes
    val filteredItems = sortedItems.filter { item ->
        item.name.contains(filter, ignoreCase = true) &&
                item.likes >= minLikes
                && (minDistance == 0f || calculateDistance(userLocation!!, item) <= minDistance)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Title()

        category?.let { CategoryName(it) }

        Row {
            // Botón nuevo item
            IconButton(onClick = { showAddItemDialog = true }) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = LanguageManager.getText("add item"),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { showSortMenu = !showSortMenu }) {
                val icon: ImageVector = if (!showSortMenu) {
                    Icons.Default.Sort
                } else {
                    Icons.Default.Close
                }
                Icon(imageVector = icon, contentDescription = LanguageManager.getText("show sort menu"))
            }

            Spacer(modifier = Modifier.width(8.dp)) // Espacio entre los botones

            IconButton(onClick = { showFilters = !showFilters }) {
                val icon: ImageVector = if (!showFilters){
                    Icons.Default.Search
                } else{
                    Icons.Default.Close
                }
                Icon(imageVector = icon, contentDescription = LanguageManager.getText("show filters"))
            }
        }

        // Mostrar opciones de ordenación si el desplegable está abierto
        if (showSortMenu) {
            SortOptions(
                sortOption = sortOption,
                onSortOptionChange = { newOption -> sortOption = newOption }
            )
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

        // Mostrar menú de nuevo item
        if (showAddItemDialog) {
            AddItemDialog(
                context = LocalContext.current,
                userLocation = userLocation!!,
                errorMessage = errorMessage,
                onDismiss = {
                    showAddItemDialog = false
                    errorMessage = null
                },
                onConfirm = { itemName, description, latitude, longitude, imageUri ->
                    if (!itemExistsAdd(itemName, items)) {
                        uploadItemImage(imageUri) { url ->
                            val newItem =
                                Item(
                                    name = itemName,
                                    description = description,
                                    categoryId = categoryId,
                                    userId = getCurrentUserId(),
                                    latitude = latitude,
                                    longitude = longitude,
                                    imageUrl = url,
                                )
                            itemViewModel.addItem(newItem, categoryId)
                            showAddItemDialog = false
                            errorMessage = null
                        }
                    } else {
                        errorMessage = LanguageManager.getText("name not available")
                    }
                },
            )
        }

        // Mostrar menú de editar item
        if (showEditItemDialog && itemToEdit != null) {
            EditItemDialog(
                context = LocalContext.current,
                errorMessage = errorMessage,
                item = itemToEdit!!,
                itemViewModel = itemViewModel,
                onDismiss = {
                    showEditItemDialog = false
                    errorMessage = null
                },
                onConfirm = { updatedItem, categoryId, imageUri ->
                    if (!itemExistsEdit(updatedItem.name, items, updatedItem.id)) {
                        if (imageUri == null) {
                            itemViewModel.updateItem(itemToEdit!!.id, updatedItem, categoryId)
                            showEditItemDialog = false
                        } else {
                            uploadItemImage(imageUri) { url ->
                                val updatedItemNewImage = itemToEdit!!.copy(
                                    name = updatedItem.name,
                                    description = updatedItem.description,
                                    imageUrl = url
                                )
                                itemViewModel.updateItem(itemToEdit!!.id, updatedItemNewImage, categoryId)
                                showEditItemDialog = false
                            }
                        }
                    } else {
                        errorMessage = LanguageManager.getText("name not available")
                    }
                }
            )
        }
    }
}

@Composable
fun SortOptions(
    sortOption: String,
    onSortOptionChange: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    // Contenedor de opciones de ordenación
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, colorScheme.onBackground.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .background(colorScheme.surfaceVariant)
    ) {
        // Título de la sección
        Text(
            text = LanguageManager.getText("sort options"),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(8.dp)
        )

        // Opciones de ordenación con botones de estilo moderno
        val sortOptionsList = listOf(
            "Ordenar de menor a mayor distancia" to Icons.Default.ArrowUpward,
            "Ordenar de mayor a menor distancia" to Icons.Default.ArrowDownward,
            "Ordenar de menor a mayor número de likes" to Icons.Default.ThumbUp,
            "Ordenar de mayor a menor número de likes" to Icons.Default.ThumbDown,
            "Ordenar por fecha de mayor a menor" to Icons.Default.Today,
            "Ordenar por fecha de menor a mayor" to Icons.Default.Schedule
        )

        sortOptionsList.forEach { (option, icon) ->
            SortOptionItem(
                option = option,
                icon = icon,
                isSelected = sortOption == option,
                onClick = { onSortOptionChange(option) }
            )
        }
    }
}

@Composable
fun SortOptionItem(
    option: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
            .background(
                if (isSelected) colorScheme.primary else colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = option,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.weight(1f)
        )
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

    // Contenedor de filtros
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, colorScheme.onBackground.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .background(colorScheme.surfaceVariant)
    ) {
        // Título de la sección
        Text(
            text = LanguageManager.getText("filter options"),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(8.dp)
        )

        // Filtro por nombre
        FilterOptionItem(
            option = LanguageManager.getText("filter by name"),
            value = filter,
            onValueChange = onFilterChange,
            icon = Icons.Default.Search
        )

        // Filtro por distancia
        val distanceValue = if (minDistance == 0f) {
            "∞ km" // Mostrar infinito si la distancia es 0
        } else {
            "${minDistance.toInt()} km" // De lo contrario, mostrar la distancia en km
        }

        FilterOptionItem(
            option = LanguageManager.getText("filter by distance"),
            value = distanceValue,
            onValueChange = { onMinDistanceChange(it.toFloat()) },
            icon = Icons.Default.LocationOn,
            isSlider = true,
            sliderValue = minDistance
        )

        // Filtro por likes
        FilterOptionItem(
            option = LanguageManager.getText("filter by likes"),
            value = "$minLikes likes",
            onValueChange = { onMinLikesChange(it.toInt()) },
            icon = Icons.Default.ThumbUp,
            isSlider = true,
            sliderValue = minLikes.toFloat()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterOptionItem(
    option: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    isSlider: Boolean = false,
    sliderValue: Float = 0f
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Título de la opción
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = option,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.weight(1f)
            )
        }

        // Campo de valor o slider
        if (isSlider) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Normal,
                    color = colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Slider(
                value = sliderValue,
                onValueChange = { onValueChange(it.toString()) },
                valueRange = 0f..1000f,
                steps = 999,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = colorScheme.primary,
                    activeTrackColor = colorScheme.primary,
                    inactiveTrackColor = colorScheme.onSurface.copy(alpha = 0.3f)
                )
            )
        } else {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(LanguageManager.getText("search")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.5f),
                    focusedLabelColor = colorScheme.primary,
                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp) // Bordes suaves
            )
        }
    }
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

// Función para subir imágenes a Firebase
fun uploadItemImage(uri: Uri, onSuccess: (String) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("items/${UUID.randomUUID()}.jpg")

    imageRef.putFile(uri)
        .addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { url ->
                onSuccess(url.toString())
            }
        }
        .addOnFailureListener {
            println("Error al subir la imagen: ${it.message}")
        }
}

fun calculateDistance(userLocation: Location, item: Item): Float {
    val results = FloatArray(1)
    Location.distanceBetween(
        userLocation.latitude, userLocation.longitude,  // Coordenadas del usuario
        item.latitude, item.longitude,  // Coordenadas del ítem
        results
    )
    return results[0] / 1000  // Convertir a kilómetros
}
