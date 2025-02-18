package cat.copernic.pokemap.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.data.Repository.UsersRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersViewModel: ViewModel(){

    private val repository = UsersRepository()

    private val _users = MutableStateFlow<List<Users>>(emptyList())
    val users: StateFlow<List<Users>> = _users

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _user = MutableStateFlow<Users?>(null)
    val user: StateFlow<Users?> = _user

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

    fun fetchUserByUid(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _user.value = repository.getUserByUid(uid)
            _isLoading.value = false
        }
    }

    fun editUserLanguage(lang: String){
        val userUid : String = FirebaseAuth.getInstance().currentUser?.uid.toString()
        viewModelScope.launch {
            _isLoading.value = true

        if(!repository.updateUserLanguage(userUid,lang)){
            return@launch
        }
            _isLoading.value = false
        }
    }
}
