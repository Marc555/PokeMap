package cat.copernic.pokemap.data.Repository

import cat.copernic.pokemap.data.DTO.Contact
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ContactRepository {
    private val db = FirebaseFirestore.getInstance()
    private val contactCollection = db.collection("contact") // Ensure collection name matches


    suspend fun addContact(contact: Contact): String? {
        return try {
            val documentRef = contactCollection.add(contact).await()
            documentRef.id // Return the generated document ID
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
