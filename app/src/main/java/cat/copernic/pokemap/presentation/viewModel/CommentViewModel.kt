package cat.copernic.pokemap.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.pokemap.data.DTO.Comment
import cat.copernic.pokemap.data.Repository.CommentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CommentViewModel : ViewModel() {

    private val repository = CommentRepository()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _comment = MutableStateFlow<Comment?>(null)
    val comment: StateFlow<Comment?> = _comment

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Cargar los comentarios de un ítem
    fun fetchComments(itemId: String) {
        viewModelScope.launch {
            _comments.value = repository.getComments(itemId)
        }
    }

    // Agregar un nuevo comentario
    fun addComment(comment: Comment, itemId: String) {
        viewModelScope.launch {
            try {
                repository.addComment(comment)
                fetchComments(itemId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Actualizar un comentario
    fun updateComment(commentId: String, updatedComment: Comment, itemId: String) {
        viewModelScope.launch {
            try {
                repository.updateComment(commentId, updatedComment)
                fetchComments(itemId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Eliminar un comentario
    fun deleteComment(id: String, itemId: String) {
        viewModelScope.launch {
            try {
                repository.deleteComment(id)
                fetchComments(itemId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Like a comment
    fun likeComment(commentId: String) {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch
            val commentDoc = db.collection("comments").document(commentId)
            val comment = commentDoc.get().await().toObject(Comment::class.java)

            comment?.let {
                val likedBy = it.likedBy.toMutableList()
                if (likedBy.contains(currentUserId)) {
                    likedBy.remove(currentUserId)  // Si ya dio like, lo elimina
                    commentDoc.update("likes", it.likes - 1) // Disminuye el contador de likes
                } else {
                    likedBy.add(currentUserId)  // Si no, lo agrega
                    commentDoc.update("likes", it.likes + 1) // Incrementa el contador de likes
                }
                commentDoc.update("likedBy", likedBy)
                fetchComments(it.itemId)  // Actualiza los comentarios después de hacer el cambio
            }
        }
    }

    // Dislike a comment
    fun dislikeComment(commentId: String) {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch
            val commentDoc = db.collection("comments").document(commentId)
            val comment = commentDoc.get().await().toObject(Comment::class.java)

            comment?.let {
                val dislikedBy = it.dislikedBy.toMutableList()
                if (dislikedBy.contains(currentUserId)) {
                    dislikedBy.remove(currentUserId)  // Si ya dio dislike, lo elimina
                    commentDoc.update("dislikes", it.dislikes - 1) // Disminuye el contador de dislikes
                } else {
                    dislikedBy.add(currentUserId)  // Si no, lo agrega
                    commentDoc.update("dislikes", it.dislikes + 1) // Incrementa el contador de dislikes
                }
                commentDoc.update("dislikedBy", dislikedBy)
                fetchComments(it.itemId)  // Actualiza los comentarios después de hacer el cambio
            }
        }
    }
}

