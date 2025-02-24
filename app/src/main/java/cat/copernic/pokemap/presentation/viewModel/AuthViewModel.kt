package cat.copernic.pokemap.presentation.viewModel

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.MyApp
import cat.copernic.pokemap.data.DTO.Rol
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.data.Repository.AuthRepository
import cat.copernic.pokemap.data.Repository.UsersRepository
import cat.copernic.pokemap.utils.LanguageManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.intellij.lang.annotations.Language

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
        language:String?,
        onResult: (Boolean) -> Unit
    ) {
        _isLoading.value = true // Iniciar carga
        authRepository.registerUser(email, password) { success, uid ->
            if (success && uid != null) {
                val user = Users(
                    email = email.trim(),
                    username = username.trim(),
                    name = name.trim(),
                    surname = surname.trim(),
                    rol = Rol.USER,
                    language = language
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
