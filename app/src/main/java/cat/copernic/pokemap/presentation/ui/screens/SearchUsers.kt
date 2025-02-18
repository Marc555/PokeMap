package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import coil.compose.AsyncImage

@Composable
fun SearchUsers(
    navController: NavController,
    viewModel: UsersViewModel = viewModel() // Instancia del ViewModel
) {
    LaunchedEffect( Unit ) {
        viewModel.fetchUsersWithIds()
    }

    var searchQuery by remember { mutableStateOf("") }
    val users by viewModel.usersWithIds.collectAsState() // Observar la lista de usuarios del ViewModel

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        // Barra de búsqueda
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de usuarios (solo se muestra si hay texto en la barra de búsqueda)
        if (searchQuery.isNotEmpty()) {
            LazyColumn {
                items(
                    users.filter { (id, user) ->
                        user.username.contains(searchQuery, ignoreCase = true) // Filtrado "LIKE"
                    }
                ) { (id, user) ->
                    UserItem(
                        id = id,
                        user = user,
                        modifier = Modifier.fillMaxWidth(),
                        navController
                    )
                }
            }
        } else {
            // Mensaje o estado inicial cuando no se ha escrito nada
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Escribe para buscar usuarios", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Buscar usuario") },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") }
    )
}

@Composable
fun UserItem(
    id: String,
    user: Users,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Row(
        modifier = modifier
            .clickable { navController.navigate(AppScreens.ProfileUid.createRoute(id)) } // Usar el UID del usuario
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen de perfil (usando Coil para cargar imágenes desde una URL)
        ImageProfile(user.imageUrl, Modifier.size(40.dp))

        Spacer(modifier = Modifier.width(8.dp))

        // Nombre de usuario
        Text(text = user.username, style = MaterialTheme.typography.bodyMedium)
    }
}
