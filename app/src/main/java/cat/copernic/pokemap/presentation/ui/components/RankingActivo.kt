package cat.copernic.pokemap.presentation.ui.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.pokemap.R
import cat.copernic.pokemap.data.DTO.FilterOption
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import cat.copernic.pokemap.utils.LanguageManager

@Composable
fun RankingActivo(
    userViewModel: UsersViewModel = viewModel(),
    abeeZee: FontFamily = FontFamily(Font(R.font.abeezee))
) {
    var selectedFilter by remember { mutableStateOf(FilterOption.DAY) }

    LaunchedEffect(selectedFilter) {
        when (selectedFilter) {
            FilterOption.DAY -> userViewModel.fetchTopUsersByPosts("lastDay")
            FilterOption.MONTH -> userViewModel.fetchTopUsersByPosts("lastMonth")
            FilterOption.YEAR -> userViewModel.fetchTopUsersByPosts("lastYear")
        }
    }

    val topUsersByPosts by userViewModel.topUsersByPosts.collectAsState()
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
            LanguageManager.getText("most_active"),
            fontWeight = FontWeight.Bold,
            fontFamily = abeeZee,
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filtro de selección
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterOption.values().forEach { option ->
                FilterButton(
                    text = when (option) {
                        FilterOption.DAY -> "Día"
                        FilterOption.MONTH -> "Mes"
                        FilterOption.YEAR -> "Año"
                    },
                    isSelected = selectedFilter == option,
                    onClick = { selectedFilter = option }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar spinner si está cargando
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            // Mostrar usuarios según el filtro seleccionado
            when (selectedFilter) {
                FilterOption.DAY -> {
                    Text(
                        "Día",
                        fontWeight = FontWeight.Bold,
                        fontFamily = abeeZee,
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    topUsersByPosts["lastDay"]?.let { counts ->
                        UserList(counts, abeeZee)
                    }
                }
                FilterOption.MONTH -> {
                    Text(
                        "Mes",
                        fontWeight = FontWeight.Bold,
                        fontFamily = abeeZee,
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    topUsersByPosts["lastMonth"]?.let { counts ->
                        UserList(counts, abeeZee)
                    }
                }
                FilterOption.YEAR -> {
                    Text(
                        "Año",
                        fontWeight = FontWeight.Bold,
                        fontFamily = abeeZee,
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    topUsersByPosts["lastYear"]?.let { counts ->
                        UserList(counts, abeeZee)
                    }
                }
            }
        }
    }
}

    @Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(text)
    }
}

@Composable
fun UserList(counts: List<Pair<Users, Int>>, abeeZee: FontFamily) {
    LazyColumn {
        items(counts.take(5)) { (user, count) ->
            UserItem(user, count, abeeZee)
        }
    }
}