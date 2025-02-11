package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.pokemap.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Profile(navController: NavController, viewModel: UsersViewModel = viewModel()){

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userEmail = currentUser?.email
    val user =  viewModel.users.value.find { it.email == userEmail }

    Box (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ){
        if (user != null) {
            Row (
                modifier = Modifier
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageProfile()

                Spacer(modifier = Modifier.width(16.dp))

                Nombres(user = user)
            }
        } else {
            Text(text = "Error al cargar los datos del usuario")
        }
    }
}

@Composable
fun Nombres(user: Users) {
    Column {
        Text(text = "${user.name} ${user.surname}")
        Text(text = "@${user.username}")
        Text(text = "${user.codeFriend}")
    }
}

@Composable
fun ImageProfile(){
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Imagen de parfil",
        modifier = Modifier
            .size(100.dp) // Ajusta el tamaño de la imagen
            .clip(CircleShape) // Redondea la imagen
            .border(2.dp, color = MaterialTheme.colorScheme.onBackground, CircleShape)

    )
}