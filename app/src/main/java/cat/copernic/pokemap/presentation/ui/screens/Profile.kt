package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Profile(navController: NavController, viewModel: UsersViewModel = viewModel()){

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userEmail = currentUser?.email
    val user =  viewModel.users.value.find { it.email == userEmail }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        if (user != null) {
            Nombres(user = user)
        } else {
            Text(text = "Error al cargar los datos del usuario")
        }
    }
}

@Composable
fun Nombres(user: Users) {
    Text(text = "${user.name} ${user.surname}")
    Text(text = "@${user.username}")
    Text(text = "${user.codeFriend}")
}
