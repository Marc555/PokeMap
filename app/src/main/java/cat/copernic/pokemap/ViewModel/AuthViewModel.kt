package cat.copernic.pokemap.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.data.DTO.Rol
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.data.Repository.AuthRepository
import cat.copernic.pokemap.data.Repository.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val usersRepository = UsersRepository()

    // Estado de carga con MutableStateFlow
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun registerUser(
        email: String,
        password: String,
        username: String,
        name: String,
        surname: String,
        onResult: (Boolean) -> Unit
    ) {
        _isLoading.value = true // Iniciar carga
        authRepository.registerUser(email, password) { success, uid ->
            if (success && uid != null) {
                val user = Users(
                    email = email,
                    username = username,
                    name = name,
                    surname = surname,
                    rol = Rol.USER
                )

                viewModelScope.launch {
                    val firestoreSuccess = usersRepository.addUser(uid, user)
                    _isLoading.value = false // Finalizar carga
                    onResult(firestoreSuccess)
                }
            } else {
                _isLoading.value = false // Finalizar carga
                onResult(false)
            }
        }
    }
}