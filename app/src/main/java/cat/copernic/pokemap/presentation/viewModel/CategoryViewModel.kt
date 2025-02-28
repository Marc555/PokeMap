package cat.copernic.pokemap.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.data.Repository.CategoryRepository
import cat.copernic.pokemap.data.Repository.CommentRepository
import cat.copernic.pokemap.data.Repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val repository = CategoryRepository()
    private val itemRepository = ItemRepository()
    private val commentRepository = CommentRepository()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    fun fetchCategories() {
        viewModelScope.launch {
            _categories.value = repository.getCategories()
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            try {
                repository.addCategory(category)
                fetchCategories()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateCategory(id: String, category: Category) {
        viewModelScope.launch {
            try {
                repository.updateCategory(id, category)
                fetchCategories()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            try {
                val items = itemRepository.getItems(id)
                items.forEach { item ->
                    // Obtener los comentarios asociados al ítem
                    val comments = commentRepository.getComments(item.id)

                    // Eliminar cada comentario asociado al ítem
                    comments.forEach { comment ->
                        commentRepository.deleteComment(comment.id)
                    }

                    // Eliminar el ítem
                    itemRepository.deleteItem(item.id)
                }

                // Eliminar la categoría
                repository.deleteCategory(id)
                fetchCategories()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
