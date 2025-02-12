package cat.copernic.pokemap.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.data.DTO.Item
import cat.copernic.pokemap.data.Repository.CategoryRepository
import cat.copernic.pokemap.data.Repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemViewModel : ViewModel() {

    private val repository = ItemRepository()
    private val categoryRepository = CategoryRepository()

    private val _category = MutableStateFlow<Category?>(null)
    val category = _category.asStateFlow()

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    var items = _items.asStateFlow()

    fun fetchItems(categoryId: String) {
        viewModelScope.launch {
            _items.value = repository.getItems(categoryId)
        }
    }

    fun addItem(item: Item) {
        viewModelScope.launch {
            try {
                repository.addItem(item)
                fetchItems(item.category.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateItem(itemId: String, updatedItem: Item) {
        viewModelScope.launch {
            try {
                repository.updateItem(itemId, updatedItem)
                fetchItems(updatedItem.category.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteItem(id: String, categoryId: String) {
        viewModelScope.launch {
            try {
                repository.deleteItem(id)
                fetchItems(categoryId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getCategoryById(categoryId: String) {
        viewModelScope.launch {
            // Obtener la categoría del repositorio
            val result = categoryRepository.getCategoryById(categoryId)
            _category.value = result  // Actualizar el estado
        }
    }
}
