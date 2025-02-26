package cat.copernic.pokemap.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.data.DTO.Item
import cat.copernic.pokemap.data.Repository.CategoryRepository
import cat.copernic.pokemap.data.Repository.ItemRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ItemViewModel : ViewModel() {

    private val repository = ItemRepository()
    private val categoryRepository = CategoryRepository()

    private val _category = MutableStateFlow<Category?>(null)
    val category = _category.asStateFlow()

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    var items = _items.asStateFlow()

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item

    private val _userLike = MutableStateFlow(false)
    val userLike: StateFlow<Boolean> = _userLike

    private val _userDislike = MutableStateFlow(false)
    val userDislike: StateFlow<Boolean> = _userDislike

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun fetchItems(categoryId: String) {
        viewModelScope.launch {
            _items.value = repository.getItems(categoryId)
        }
    }

    fun addItem(item: Item, categoryId: String) {
        viewModelScope.launch {
            try {
                repository.addItem(item)
                fetchItems(categoryId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateItem(itemId: String, updatedItem: Item, categoryId: String) {
        viewModelScope.launch {
            try {
                repository.updateItem(itemId, updatedItem)
                fetchItems(categoryId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteItem(id: String, categoryId: String) {
        viewModelScope.launch {
            try {
                repository.deleteItem(id)
                fetchItems(categoryId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getCategoryById(categoryId: String) {
        viewModelScope.launch {
            _category.value = categoryRepository.getCategoryById(categoryId)
        }
    }

    fun getItemById(itemId: String) {
        db.collection("items").document(itemId).addSnapshotListener { snapshot, _ ->
            snapshot?.toObject(Item::class.java)?.let { itemData ->
                _item.value = itemData
                val userId = auth.currentUser?.uid ?: ""

                // Verificamos si el usuario ya dio like o dislike
                _userLike.value = itemData.likedBy.contains(userId)
                _userDislike.value = itemData.dislikedBy.contains(userId)
            }
        }
    }

    fun likeItem() {
        val userId = auth.currentUser?.uid ?: return
        val currentItem = _item.value ?: return
        val itemRef = db.collection("items").document(currentItem.id)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(itemRef)
            val updatedItem = snapshot.toObject(Item::class.java) ?: return@runTransaction

            val newLikedBy = updatedItem.likedBy.toMutableList()
            val newDislikedBy = updatedItem.dislikedBy.toMutableList()
            var newLikes = updatedItem.likes
            var newDislikes = updatedItem.dislikes

            if (newLikedBy.contains(userId)) {
                newLikedBy.remove(userId)
                newLikes--
            } else {
                newLikedBy.add(userId)
                newLikes++
                newDislikedBy.remove(userId)
                newDislikes = maxOf(0, newDislikes - 1)
            }

            // Actualiza en Firebase
            transaction.update(
                itemRef, mapOf(
                    "likes" to newLikes,
                    "likedBy" to newLikedBy,
                    "dislikes" to newDislikes,
                    "dislikedBy" to newDislikedBy
                )
            )

            // Actualiza localmente el estado
            _item.value = updatedItem.copy(
                likes = newLikes,
                likedBy = newLikedBy,
                dislikes = newDislikes,
                dislikedBy = newDislikedBy
            )
            _userLike.value = newLikedBy.contains(userId)
            _userDislike.value = newDislikedBy.contains(userId)
        }
    }

    fun dislikeItem() {
        val userId = auth.currentUser?.uid ?: return
        val currentItem = _item.value ?: return
        val itemRef = db.collection("items").document(currentItem.id)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(itemRef)
            val updatedItem = snapshot.toObject(Item::class.java) ?: return@runTransaction

            val newLikedBy = updatedItem.likedBy.toMutableList()
            val newDislikedBy = updatedItem.dislikedBy.toMutableList()
            var newLikes = updatedItem.likes
            var newDislikes = updatedItem.dislikes

            if (newDislikedBy.contains(userId)) {
                newDislikedBy.remove(userId)
                newDislikes--
            } else {
                newDislikedBy.add(userId)
                newDislikes++
                newLikedBy.remove(userId)
                newLikes = maxOf(0, newLikes - 1)
            }

            // Actualiza en Firebase
            transaction.update(
                itemRef, mapOf(
                    "likes" to newLikes,
                    "likedBy" to newLikedBy,
                    "dislikes" to newDislikes,
                    "dislikedBy" to newDislikedBy
                )
            )

            // Actualiza localmente el estado
            _item.value = updatedItem.copy(
                likes = newLikes,
                likedBy = newLikedBy,
                dislikes = newDislikes,
                dislikedBy = newDislikedBy
            )
            _userLike.value = newLikedBy.contains(userId)
            _userDislike.value = newDislikedBy.contains(userId)
        }
    }
}