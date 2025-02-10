package cat.copernic.pokemap.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.data.Repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val repository = CategoryRepository()

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
                repository.deleteCategory(id)
                fetchCategories()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
