package cat.copernic.pokemap.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.data.DTO.Follow
import cat.copernic.pokemap.data.Repository.FollowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    // Obtener todos los seguimientos
    fun fetchFollows() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _follows.value = repository.getFollows()
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
                val result = repository.addFollow(follow) // Devuelve Pair<Boolean, String?>
                val success = result.first
                val documentId = result.second

                if (success && documentId != null) {
                    _isFollowed.value = true
                    fetchFollows() // Actualiza la lista de seguidores y seguidos
                    _followedObjectId.value = documentId // Guardamos la ID del documento
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