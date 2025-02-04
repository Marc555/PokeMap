package cat.copernic.pokemap.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.data.Repository.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersViewModel: ViewModel(){
    private val repository = UsersRepository()

    private val _users = MutableStateFlow<List<Users>>(emptyList())
    val users: StateFlow<List<Users>> = _users

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _users.value = repository.getUsers()
            _isLoading.value = false
        }
    }
}