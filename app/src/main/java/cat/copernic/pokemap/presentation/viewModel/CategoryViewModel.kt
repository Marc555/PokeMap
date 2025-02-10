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

    private val _operationStatus = MutableStateFlow<Result<Unit>?>(null)
    val operationStatus = _operationStatus.asStateFlow()

    fun fetchCategories() {
        viewModelScope.launch {
            val getCategories = repository.getCategories()
            _categories.value = getCategories // ✅ Updates with all records
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            try {
                repository.addCategory(category)
                fetchCategories() // ✅ Refreshes the full list
                _operationStatus.value = Result.success(Unit) // Notify success
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e) // Notify failure
            }
        }
    }

    fun updateCategory(id: String, category: Category) {
        viewModelScope.launch {
            try {
                repository.updateCategory(id, category)
                fetchCategories() // ✅ Refreshes the full list
                _operationStatus.value = Result.success(Unit) // Notify success
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e) // Notify failure
            }
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(id)
                fetchCategories() // ✅ Refreshes the full list
                _operationStatus.value = Result.success(Unit) // Notify success
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e) // Notify failure
            }
        }
    }
}
