package cat.copernic.pokemap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.ViewModel.UsersViewModel
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth


@Composable
fun Home(navController: NavController) {
    // Obtén la instancia del UsersViewModel
    val usersViewModel: UsersViewModel = viewModel()

    // Obtén el estado de los usuarios
    val users = usersViewModel.users.collectAsState(initial = emptyList()).value
    var isMenuExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UsersList(users)
        Spacer(modifier = Modifier.height(16.dp))
        LogoutButton(navController)
    }
}

@Composable
fun UsersList(users: List<Users>) {
    LazyColumn(modifier = Modifier.wrapContentHeight()) {
        itemsIndexed(users) { _, user ->
            UserItem(user)
        }
    }
}

@Composable
fun UserItem(user: Users) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Usuario: ${user.username}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Nombre: ${user.name} ${user.surname}")
            Text(text = "Rol: ${user.rol}")
        }
    }
}

@Composable
fun LogoutButton(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    Button(
        onClick = {
            auth.signOut()
            navController.navigate(AppScreens.Login.rute) // Redirige a la pantalla de login
        }
    ) {
        Text(text = "Cerrar sesión")
    }
}
