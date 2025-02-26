package cat.copernic.pokemap.data.Repository

import cat.copernic.pokemap.data.DTO.Comment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CommentRepository {

    private val db = FirebaseFirestore.getInstance()
    private val commentCollection = db.collection("comments")

    suspend fun getAllComments(): List<Comment> {
        return try {
            commentCollection.get().await().toObjects(Comment::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Error, devuelve una lista vacía
        }
    }

    suspend fun getComments(itemId: String): List<Comment> {
        return try {
            commentCollection
                .whereEqualTo("itemId", itemId) // Filtra por el itemId
                .get()
                .await()
                .toObjects(Comment::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Error, devuelve una lista vacía
        }
    }

    suspend fun addComment(comment: Comment): String? {
        return try {
            //Crear documento vacio
            val documentReference = commentCollection.document()

            //Asignar el id generado por firestore al objeto item
            comment.id = documentReference.id

            //Guardar el objeto item en firestore
            documentReference.set(comment).await()

            //Devolver el id generado
            documentReference.id
        } catch (e: Exception) {
            e.printStackTrace()
            null // Fallo, devuelve null
        }
    }

    suspend fun updateComment(commentId: String, updatedComment: Comment): Boolean {
        return try {
            // Obtener el documento de Firestore
            val documentReference = commentCollection.document(commentId)

            // Actualizar el documento con los nuevos datos
            documentReference.set(updatedComment).await()
            true // Éxito
        } catch (e: Exception) {
            e.printStackTrace()
            false // Fallo
        }
    }

    suspend fun deleteComment(id: String): Boolean {
        return try {
            // Eliminar el documento de Firestore
            commentCollection.document(id).delete().await()
            true // Éxito
        } catch (e: Exception) {
            false // Fallo
        }
    }
}
