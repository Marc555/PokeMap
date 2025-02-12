package cat.copernic.pokemap.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.data.DTO.Item
import cat.copernic.pokemap.presentation.viewModel.ItemViewModel

@Composable
fun Items(navController: NavController, categoryId: String) {
    val itemViewModel: ItemViewModel = viewModel()

    LaunchedEffect(categoryId) {
        itemViewModel.getCategoryById(categoryId)
    }

    val category by itemViewModel.category.collectAsState()

    val items by itemViewModel.items.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }
    var showEditItemDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<Item?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Titulo()

        category?.let { CategoryName(it) }

        FilterButton()

        ItemsList(items) { item ->
            itemToEdit = item
            showEditItemDialog = true
        }
    }
}


@Composable
fun ItemsList(items: List<Item>, onEditItem: (Item) -> Unit) {
    Column {
        items.forEach { item ->
            ItemCard(item) { onEditItem(item) }
        }
    }
}

@Composable
fun ItemCard(item: Item, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = item.name, style = MaterialTheme.typography.bodyLarge)

        Text(
            text = "Editar",
            modifier = Modifier.padding(4.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun FilterButton() {
    Text(
        text = "Filtrar",
        modifier = Modifier.padding(4.dp),
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun CategoryName(category: Category) {
    Text(
        text = category.name,
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 50.sp
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
    )
}

fun filter(selection: String){

}
