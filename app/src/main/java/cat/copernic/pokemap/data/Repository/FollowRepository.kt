package cat.copernic.pokemap.data.Repository

import cat.copernic.pokemap.data.DTO.Follow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FollowRepository {

    private val db = FirebaseFirestore.getInstance()
    private val followCollection = db.collection("follows")

    // Añadir un nuevo seguimiento y guardar la ID en el propio Follow
    suspend fun addFollow(follow: Follow): Pair<Boolean, String?> {
        return try {
            val documentRef = followCollection.document() // Generar una nueva ID
            val followWithId = follow.copy(Uid = documentRef.id) // Asignar la ID al campo Uid
            documentRef.set(followWithId).await() // Guardar el documento con la ID
            Pair(true, documentRef.id) // Retorna éxito y la ID del documento
        } catch (e: Exception) {
            Pair(false, null) // Retorna fallo y null si hay un error
        }
    }

    // Obtener todos los seguimientos
    suspend fun getFollows(): List<Follow> {
        return try {
            followCollection.get().await().toObjects(Follow::class.java)
        } catch (e: Exception) {
            emptyList() // Devuelve una lista vacía en caso de error
        }
    }

    // Eliminar un seguimiento
    suspend fun deleteFollow(followId: String): Boolean {
        return try {
            followCollection.document(followId).delete().await()
            true // Éxito
        } catch (e: Exception) {
            false // Fallo
        }
    }

    // Obtener un seguimiento por ID
    suspend fun getFollowById(followId: String): Follow? {
        return try {
            followCollection.document(followId).get().await().toObject(Follow::class.java)
        } catch (e: Exception) {
            null // Devuelve null en caso de error
        }
    }

    // Verificar si un usuario x es seguido por un usuario y
    suspend fun isUserXFollowedByUserY(xEmail: String, yEmail: String): Pair<Boolean, String?> {
        return try {
            val result = followCollection
                .whereEqualTo("followed", xEmail) // x es el usuario seguido
                .whereEqualTo("follower", yEmail) // y es el seguidor
                .get()
                .await()

            if (!result.isEmpty) {
                val documentId = result.documents.first().id // Obtener el ID del primer documento que cumple con la condición
                Pair(true, documentId) // Devolver true y el ID del documento
            } else {
                Pair(false, null) // Devolver false y null si no hay ningún documento que cumpla con la condición
            }
        } catch (e: Exception) {
            Pair(false, null) // Devolver false y null en caso de error
        }
    }
}