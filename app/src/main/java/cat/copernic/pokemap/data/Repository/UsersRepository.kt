package cat.copernic.pokemap.data.Repository

import cat.copernic.pokemap.data.DTO.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsersRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    suspend fun addUser(uid: String, user: Users): Boolean {
        return try {
            usersCollection.document(uid).set(user).await()
            true // Éxito
        } catch (e: Exception) {
            false // Fallo
        }
    }

    suspend fun getUsers(): List<Users> {
        return try {
            usersCollection.get().await().toObjects(Users::class.java)
        } catch (e: Exception) {
            emptyList() // Devuelve una lista vacía en caso de error
        }
    }

    suspend fun getUsersWithIds(): List<Pair<String, Users>> {
        return try {
            usersCollection.get().await().documents.mapNotNull { document ->
                document.toObject(Users::class.java)?.let { user ->
                    document.id to user
                }
            }
        } catch (e: Exception) {
            emptyList() // Devuelve una lista vacía en caso de error
        }
    }


    suspend fun updateUser(uid: String, user: Users): Boolean {
        return try {
            usersCollection.document(uid).set(user).await()
            true // Éxito
        } catch (e: Exception) {
            false // Fallo
        }
    }

    suspend fun deleteUser(uid: String): Boolean {
        return try {
            usersCollection.document(uid).delete().await()
            true // Éxito
        } catch (e: Exception) {
            false // Fallo
        }
    }

    suspend fun getUserByUid(uid: String): Users? {
        return try {
            usersCollection.document(uid).get().await().toObject(Users::class.java)
        } catch (e: Exception) {
            null // Devuelve null en caso de error
        }
    }
}