package cat.copernic.pokemap.presentation.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.R
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import cat.copernic.pokemap.presentation.viewModel.FollowViewModel
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.utils.LanguageManager
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FollowersUsersScreen(
    navController: NavController,
    email: String,
    followViewModel: FollowViewModel = viewModel(),
    usersViewModel: UsersViewModel = viewModel(), // Usar UsersViewModel
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
        usersViewModel.fetchUsersWithIds()
        followViewModel.setEmail(email)
        followViewModel.fetchFollows()
//        viewModel.fetchUserByUid(userUid)
    }

    val users by usersViewModel.usersWithIds.collectAsState()
    val followersList by followViewModel.followersList.collectAsState()
//    val user by usersViewModel.user.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(
            text = "<- "+ LanguageManager.getText("back"),
            color = MaterialTheme.colorScheme.onBackground, // Color del texto
            modifier = Modifier
                .align(Alignment.Start)
                .clickable {
                    navController.popBackStack()
                }
                .padding(8.dp) // Añade un padding para que sea más fácil de tocar
        )
//        if (user?.rol == Rol.ADMIN) {
//            NamberUsers(numUsers, navController)
//        }
        Text(LanguageManager.getText("followers"), fontFamily = abeeZee, fontSize = 25.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de usuarios (solo se muestra si hay texto en la barra de búsqueda)
        LazyColumn {
            items(
                users.filter { (id, user) ->
                    followersList.any { follower -> follower.follower == user.email }
                }
            ) { (id, user) ->
                UserItem(
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