package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.R
import cat.copernic.pokemap.data.DTO.Rol
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.utils.LanguageManager
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AdminSearchUsers(
    navController: NavController,
    viewModel: UsersViewModel = viewModel(),
    abeeZee: FontFamily = FontFamily(Font(R.font.abeezee)),
    userUid: String? = FirebaseAuth.getInstance().currentUser?.uid
) {

    val userUid = userUid ?: run {
        navController.navigate(AppScreens.Login.rute){
            popUpTo(AppScreens.Login.rute) { inclusive = true }
        }
        return
    }

    LaunchedEffect( Unit ) {
        viewModel.fetchUsersWithIds()
        viewModel.fetchUserByUid(userUid)
    }

    var searchQuery by remember { mutableStateOf("") }
    val users by viewModel.usersWithIds.collectAsState()
    val user by viewModel.user.collectAsState()
    val numUsers = users.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        if (user?.rol == Rol.ADMIN) {
            NamberUsersAdmin(numUsers)
        }

        // Barra de búsqueda
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(
                users.filter { (id, user) ->
                    user.username.contains(searchQuery, ignoreCase = true) // Filtrado "LIKE"
                }
                .toList() // Convierte el Map a una lista de pares (id, user)
                .sortedByDescending { (_, user) -> user.lastLogin } // Ordena por lastLogin

            ) { (id, user) ->
                UserItemAdmin(
                    id = id,
                    user = user,
                    modifier = Modifier.fillMaxWidth(),
                    navController
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp), // Espacio vertical alrededor de la línea
                    thickness = 1.dp, // Grosor de la línea
                    color = MaterialTheme.colorScheme.onBackground // Color de la línea
                )
            }
        }
    }
}

@Composable
fun NamberUsersAdmin(numUsers: Int, abeeZee: FontFamily = FontFamily(Font(R.font.abeezee))) {
    Text(
        text = "${LanguageManager.getText("Total registered users")} $numUsers", fontFamily = abeeZee)
}

@Composable
fun UserItemAdmin(
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

        Spacer(modifier = Modifier.width(8.dp))
        val lastLoginTimestamp = user.lastLogin.toDate() // Convierte Timestamp a Date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(lastLoginTimestamp)

        // Nombre de usuario
        Text(text = "${user.username}, $formattedDate", style = MaterialTheme.typography.bodyMedium)
    }
}
