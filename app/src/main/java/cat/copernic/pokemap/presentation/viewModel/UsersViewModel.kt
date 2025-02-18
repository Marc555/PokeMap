package cat.copernic.pokemap.presentation.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.data.Repository.UsersRepository
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersViewModel : ViewModel() {

    private val repository = UsersRepository()

    private val _users = MutableStateFlow<List<Users>>(emptyList())
    val users: StateFlow<List<Users>> = _users

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _user = MutableStateFlow<Users?>(null)
    val user: StateFlow<Users?> = _user

    private val _isUploadingImage = MutableStateFlow(false) // Estado para la subida de imágenes
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage

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

    private val _usersWithIds = MutableStateFlow<List<Pair<String, Users>>>(emptyList())
    val usersWithIds: StateFlow<List<Pair<String, Users>>> = _usersWithIds

    fun fetchUsersWithIds() {
        viewModelScope.launch {
            _isLoading.value = true
            _usersWithIds.value = repository.getUsersWithIds()
            _isLoading.value = false
        }
    }


    fun fetchUserByUid(userUid: String) {
        viewModelScope.launch {
            _isLoading.value = true // Iniciar carga
            try {
                _user.value = repository.getUserByUid(userUid)
            } catch (e: Exception) {
                // Manejar errores
            } finally {
                _isLoading.value = false // Finalizar carga
            }
        }
    }

    fun updateUser(uid: String, user: Users) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.updateUser(uid, user)
                if (result) {
                    _user.value = user
                } else {
                    // Maneja el caso de fallo
                }
            } catch (e: Exception) {
                // Manejar errores
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadImageToStorage(uri: Uri, previousImageUrl: String?, onSuccess: (String) -> Unit) {
        _isUploadingImage.value = true // Iniciar subida de imagen
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("profile_images/${System.currentTimeMillis()}_${uri.lastPathSegment}.jpg") // Usar un nombre único

        viewModelScope.launch {
            imageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { url ->
                        _isUploadingImage.value = false // Finalizar subida de imagen
                        onSuccess(url.toString())
                    }
                }
                .addOnFailureListener {
                    _isUploadingImage.value = false // Finalizar subida de imagen (incluso en caso de error)
                    println("Error al subir la imagen: ${it.message}")
                }
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