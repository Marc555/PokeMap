package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.utils.LanguageManager

@Composable
fun RankingSeguido(
    userViewModel: UsersViewModel = viewModel(),
    abeeZee: FontFamily = FontFamily(Font(R.font.abeezee))
) {
    // Lanzar la carga de datos al iniciar el Composable
    LaunchedEffect(Unit) {
        userViewModel.fetchUsersWithFollowersCount()
    }

    // Obtener el estado del ViewModel
    val users by userViewModel.usersWithFollowersCount.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            LanguageManager.getText("most_followed"),
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
            // Mostrar la lista de usuarios con más seguidores
            LazyColumn {
                items(users) { (user, followerCount) ->
                    UserItem(user, followerCount, abeeZee)
                }
            }
        }
    }
}

    @Composable
fun UserItem(user: Users, number: Int, abeeZee: FontFamily) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(21.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        ImageProfile(user.imageUrl, Modifier.size(80.dp).padding(8.dp))

        Text(
            text = "@${user.username}",
            fontWeight = FontWeight.Bold,
            fontFamily = abeeZee,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "$number",
            fontFamily = abeeZee,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 10.dp)
        )
    }
}