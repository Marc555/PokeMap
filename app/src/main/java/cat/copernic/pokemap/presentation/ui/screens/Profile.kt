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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.pokemap.R
import com.google.firebase.auth.FirebaseAuth
import cat.copernic.pokemap.utils.LanguageManager

@Composable
fun Profile(navController: NavController, viewModel: UsersViewModel = viewModel()){

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userUid = currentUser?.uid
    if (userUid != null) {
        viewModel.fetchUserByUid(userUid)
    }
    val user =  viewModel.user.value

    Column (
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

            Row (
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SeguidosSeguidores()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row (
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PublicationsNumber()
            }

        } else {
            Text(text = "Cargando datos del usuario...")
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
            .size(150.dp) // Ajusta el tamaño de la imagen
            .clip(CircleShape) // Redondea la imagen
            .border(2.dp, color = MaterialTheme.colorScheme.onBackground, CircleShape)

    )
}

@Composable
fun SeguidosSeguidores() {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .border(1.dp, color = MaterialTheme.colorScheme.onBackground)
    ) {
        Text(text = "${LanguageManager.getText("followers")} 344", modifier = Modifier.padding(5.dp))
    }

    Box(
        modifier = Modifier
            .wrapContentSize()
            .border(1.dp, color = MaterialTheme.colorScheme.onBackground)
    ) {
        Text(text = "${LanguageManager.getText("following")} 238", modifier = Modifier.padding(5.dp))
    }
}

@Composable
fun PublicationsNumber() {
    Text(text = "${LanguageManager.getText("publications")} ")
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Text(text = "12", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(3.dp),)
    }
}

@Preview
@Composable
fun ProfilePreview() {
    PublicationsNumber()
}