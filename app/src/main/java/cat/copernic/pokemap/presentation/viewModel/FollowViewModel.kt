package cat.copernic.pokemap.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.data.DTO.Follow
import cat.copernic.pokemap.data.Repository.FollowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FollowViewModel : ViewModel() {

    private val repository = FollowRepository()

    // Estado para almacenar la lista de seguimientos
    private val _follows = MutableStateFlow<List<Follow>>(emptyList())
    val follows: StateFlow<List<Follow>> = _follows

    private val _following = MutableStateFlow<List<Follow>>(emptyList())
    val following: StateFlow<List<Follow>> = _following

    // Estado para indicar si se está cargando
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado para almacenar si un usuario es seguido por otro
    private val _isFollowed = MutableStateFlow(false)
    val isFollowed: StateFlow<Boolean> = _isFollowed

    private val _followedObjectId = MutableLiveData<String?>()
    val followedObjectId: LiveData<String?> get() = _followedObjectId

    // Estado para manejar errores
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Estado para almacenar el email
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> get() = _email

    // followersCount y followingCount se calculan en función del email almacenado
    val followersList: StateFlow<List<Follow>> = _follows
        .asStateFlow()
        .map { follows -> follows.filter { it.followed == _email.value } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val followingList: StateFlow<List<Follow>> = _follows
        .asStateFlow()
        .map { follows -> follows.filter { it.follower == _email.value } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Método para actualizar el email
    fun setEmail(newEmail: String) {
        _email.value = newEmail
    }

    // Obtener todos los seguimientos
    fun fetchFollows() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _follows.value = repository.getFollows()
                Log.d("FollowViewModel", "Seguimientos obtenidos: ${_follows.value}")
            } catch (e: Exception) {
                _errorMessage.value = "Error al obtener los seguimientos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Añadir un seguimiento
    fun addFollow(follow: Follow) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val followId = repository.addFollow(follow)
                if (followId != null) {
                    _isFollowed.value = true
                    _followedObjectId.value = followId
                    fetchFollows()
                } else {
                    _errorMessage.value = "Error al añadir el seguimiento"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al añadir el seguimiento: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Eliminar un seguimiento
    fun deleteFollow(followId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.deleteFollow(followId)
                if (success) {
                    _isFollowed.value = false
                    _followedObjectId.value = null
                    fetchFollows()
                } else {
                    _errorMessage.value = "Error al eliminar el seguimiento"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar el seguimiento: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Verificar si un usuario x es seguido por un usuario y
    fun checkIfUserXIsFollowedByUserY(xEmail: String, yEmail: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.isUserXFollowedByUserY(xEmail, yEmail)
                _isFollowed.value = result.first // Asignar el valor booleano
                _followedObjectId.value = result.second // Asignar el ID del objeto
            } catch (e: Exception) {
                _errorMessage.value = "Error al verificar el seguimiento: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}