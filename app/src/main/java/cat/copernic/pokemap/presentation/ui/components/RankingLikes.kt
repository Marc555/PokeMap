package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.pokemap.R
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.utils.LanguageManager

@Composable
fun RankingLikes(
    userViewModel: UsersViewModel = viewModel(),
    abeeZee: FontFamily = FontFamily(Font(R.font.abeezee))
) {
    // Lanzar la carga de datos al iniciar el Composable
    LaunchedEffect(Unit) {
        userViewModel.fetchUsersWithMostLikes()
    }

    // Obtener el estado del ViewModel
    val usersWithLikes by userViewModel.usersWithLikes.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()

    // Diseño de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la pantalla
        Title()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            LanguageManager.getText("top_rated"),
            fontWeight = FontWeight.Bold,
            fontFamily = abeeZee,
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar spinner si está cargando
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            // Mostrar la lista de usuarios con más likes
            UserList(usersWithLikes, abeeZee)
        }
    }
}