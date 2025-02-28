package cat.copernic.pokemap.presentation.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import cat.copernic.pokemap.data.DTO.Comment
import cat.copernic.pokemap.data.DTO.Item
import cat.copernic.pokemap.data.DTO.Users
import cat.copernic.pokemap.data.Repository.CommentRepository
import cat.copernic.pokemap.data.Repository.FollowRepository
import cat.copernic.pokemap.data.Repository.ItemRepository
import cat.copernic.pokemap.data.Repository.UsersRepository
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class UsersViewModel() : ViewModel() {
    private val usersRepository: UsersRepository = UsersRepository()
    private val followRepository: FollowRepository = FollowRepository()
    private val itemRepository: ItemRepository = ItemRepository()
    private val commentRepository: CommentRepository = CommentRepository()

    private val _users = MutableStateFlow<List<Users>>(emptyList())
    val users: StateFlow<List<Users>> = _users

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _user = MutableStateFlow<Users?>(null)
    val user: StateFlow<Users?> = _user

    private val _isUploadingImage = MutableStateFlow(false) // Estado para la subida de imágenes
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage

    private val _usersWithFollowersCount = MutableStateFlow<List<Pair<Users, Int>>>(emptyList())
    val usersWithFollowersCount: StateFlow<List<Pair<Users, Int>>> = _usersWithFollowersCount

    private val _topUsersByPosts = MutableStateFlow<Map<String, List<Pair<Users, Int>>>>(emptyMap())
    val topUsersByPosts: StateFlow<Map<String, List<Pair<Users, Int>>>> = _topUsersByPosts

    private val _usersWithIds = MutableStateFlow<List<Pair<String, Users>>>(emptyList())
    val usersWithIds: StateFlow<List<Pair<String, Users>>> = _usersWithIds

    private val _usersWithLikes = MutableStateFlow<List<Pair<Users, Int>>>(emptyList())
    val usersWithLikes: StateFlow<List<Pair<Users, Int>>> = _usersWithLikes

    private val _topRatedItems = MutableStateFlow<List<Item>>(emptyList())
    val topRatedItems: StateFlow<List<Item>> = _topRatedItems

    private val _topRatedComments = MutableStateFlow<List<Comment>>(emptyList())
    val topRatedComments: StateFlow<List<Comment>> = _topRatedComments

    private val _publicationsCount = MutableStateFlow(0)
    val publicationsCount: StateFlow<Int> = _publicationsCount

    init {
        fetchUsers()
    }

    // Obtener todos los usuarios
    fun fetchUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _users.value = usersRepository.getUsers()
            _isLoading.value = false
        }
    }

    // Obtener usuarios con sus IDs
    fun fetchUsersWithIds() {
        viewModelScope.launch {
            _isLoading.value = true
            _usersWithIds.value = usersRepository.getUsersWithIds()
            _isLoading.value = false
        }
    }

    // Obtener un usuario por su UID
    fun fetchUserByUid(userUid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _user.value = usersRepository.getUserByUid(userUid)
            } catch (e: Exception) {
                // Manejar errores
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Actualizar un usuario
    fun updateUser(uid: String, user: Users) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = usersRepository.updateUser(uid, user)
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

    // Subir una imagen de perfil a Firebase Storage
    fun uploadImageToStorage(uri: Uri, previousImageUrl: String?, onSuccess: (String) -> Unit) {
        _isUploadingImage.value = true
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef =
            storageRef.child("profile_images/${System.currentTimeMillis()}_${uri.lastPathSegment}.jpg")

        viewModelScope.launch {
            imageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { url ->
                        _isUploadingImage.value = false
                        onSuccess(url.toString())
                    }
                }
                .addOnFailureListener {
                    _isUploadingImage.value = false
                    println("Error al subir la imagen: ${it.message}")
                }
        }
    }

    // Actualizar el idioma del usuario
    fun editUserLanguage(lang: String) {
        val userUid: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
        viewModelScope.launch {
            _isLoading.value = true
            if (!usersRepository.updateUserLanguage(userUid, lang)) {
                return@launch
            }
            _isLoading.value = false
        }
    }

    // Actualizar la última vez que el usuario inició sesión
    fun updateLastLogin(uid: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            usersRepository.updateLastLogin(uid)
        }
    }

    // Eliminar las credenciales del usuario
    fun deleteUserCredentials(userInfo: Users, navController: NavController, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = FirebaseAuth.getInstance().currentUser ?: run {
                    navController.navigate(AppScreens.Login.rute) {
                        popUpTo(AppScreens.Login.rute) { inclusive = true }
                    }
                    return@launch
                }
                user.delete().await()
                usersRepository.updateUser(user.uid, userInfo)
                onResult(true) // Notificar éxito
            } catch (e: Exception) {
                onResult(false) // Notificar error
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Obtener los 5 usuarios con más seguidores
    fun fetchUsersWithFollowersCount() {
        viewModelScope.launch {
            _isLoading.value = true
            val users = usersRepository.getUsers()
            val follows = followRepository.getFollows()

            val usersWithCount = users.map { user ->
                val followerCount = follows.count { it.followed == user.email }
                user to followerCount
            }

            // Ordenar y tomar los 5 primeros
            _usersWithFollowersCount.value = usersWithCount
                .sortedByDescending { it.second } // Ordenar por seguidores (descendente)
                .take(5) // Tomar solo los 5 primeros
            _isLoading.value = false
        }
    }

    // Obtener los usuarios con más publicaciones en el último día, mes y año
    fun fetchTopUsersByPosts(filter: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val allItems = itemRepository.getAllItems() // Obtén todas las publicaciones
            val allUsers = usersRepository.getUsersWithIds() // Obtén todos los usuarios con sus IDs

            // Obtén las fechas de inicio y fin para el período seleccionado
            val calendar = Calendar.getInstance()
            val now = Timestamp.now()

            val (startDate, endDate) = when (filter) {
                "lastDay" -> {
                    calendar.time = now.toDate()
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                    Pair(Timestamp(calendar.time), now)
                }
                "lastMonth" -> {
                    calendar.time = now.toDate()
                    calendar.add(Calendar.MONTH, -1)
                    Pair(Timestamp(calendar.time), now)
                }
                "lastYear" -> {
                    calendar.time = now.toDate()
                    calendar.add(Calendar.YEAR, -1)
                    Pair(Timestamp(calendar.time), now)
                }
                else -> Pair(now, now)
            }

            val filteredItems = filterItemsByDateRange(allItems, startDate, endDate)
            val counts = countItemsByUser(filteredItems, allUsers)

            // Almacenar los resultados en el StateFlow
            _topUsersByPosts.value = mapOf(filter to counts)
            _isLoading.value = false
        }
    }

    // Función para filtrar publicaciones por rango de fechas
    private fun filterItemsByDateRange(items: List<Item>, startDate: Timestamp, endDate: Timestamp): List<Item> {
        return items.filter { item ->
            item.creationDate >= startDate && item.creationDate <= endDate
        }
    }

    // Función para contar publicaciones por usuario y mapear a Users
    private fun countItemsByUser(items: List<Item>, users: List<Pair<String, Users>>): List<Pair<Users, Int>> {
        val userMap = users.associate { it.first to it.second } // Mapa de userId a Users
        return items.groupingBy { it.userId }.eachCount() // Contar publicaciones por userId
            .mapNotNull { (userId, count) ->
                userMap[userId]?.let { user -> user to count } // Mapear userId a Users
            }
            .sortedByDescending { it.second } // Ordenar por número de publicaciones
    }

    // Función para obtener los usuarios con más likes
    fun fetchUsersWithMostLikes() {
        viewModelScope.launch {
            _isLoading.value = true
            val allUsers = usersRepository.getUsersWithIds() // Obtener todos los usuarios con sus IDs
            val allItems = itemRepository.getAllItems() // Obtener todos los items
            val allComments = commentRepository.getAllComments() // Obtener todos los comentarios

            // Mapa para almacenar el total de likes por usuario
            val userLikesMap = mutableMapOf<String, Int>()

            // Calcular likes de items
            allItems.forEach { item ->
                val userId = item.userId
                val likes = item.likes
                userLikesMap[userId] = (userLikesMap.getOrDefault(userId, 0) + likes).toInt()
            }

            // Calcular likes de comentarios
            allComments.forEach { comment ->
                val userId = comment.userId
                val likes = comment.likes
                userLikesMap[userId] = userLikesMap.getOrDefault(userId, 0) + likes
            }

            // Mapear a Users y ordenar por likes
            val usersWithCounts = allUsers.mapNotNull { (userId, user) ->
                userLikesMap[userId]?.let { likes -> user to likes }
            }.sortedByDescending { it.second }

            _usersWithLikes.value = usersWithCounts
            _isLoading.value = false
        }
    }

    // Función para obtener los 3 items mejor valorados del usuario
    fun fetchTopRatedItems(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val allItems = itemRepository.getAllItems()
            val userItems = allItems.filter { it.userId == userId }
            val sortedItems = userItems.sortedByDescending { it.likes }.take(3)
            _topRatedItems.value = sortedItems
            _isLoading.value = false
        }
    }

    // Función para obtener los 3 comentarios mejor valorados del usuario
    fun fetchTopRatedComments(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val allComments = commentRepository.getAllComments()
            val userComments = allComments.filter { it.userId == userId }
            val sortedComments = userComments.sortedByDescending { it.likes }.take(3)
            _topRatedComments.value = sortedComments
            _isLoading.value = false
        }
    }

    // Función para obtener el número de publicaciones del usuario
    fun fetchPublicationsCount(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val allItems = itemRepository.getAllItems()
            val userItems = allItems.filter { it.userId == userId }
            _publicationsCount.value = userItems.size
            _isLoading.value = false
        }
    }
}