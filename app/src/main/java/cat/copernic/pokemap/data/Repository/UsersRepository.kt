package cat.copernic.pokemap.data.Repository

import cat.copernic.pokemap.data.DTO.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsersRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    suspend fun addUser(user: Users) {
        usersCollection.add(user).await()
    }

    suspend fun getUsers(): List<Users> {
        return usersCollection.get().await().toObjects(Users::class.java)
    }

    suspend fun updateUser(user: Users) {
        usersCollection.document(user.username).set(user).await()
    }

    suspend fun deleteUser(username: String) {
        usersCollection.document(username).delete().await()
    }
}