package cat.copernic.pokemap.data.Repository

import cat.copernic.pokemap.data.DTO.Contact
import cat.copernic.pokemap.data.DTO.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ContactRepository {
    private val db = FirebaseFirestore.getInstance()
    private val contactCollection = db.collection("contact") // Ensure collection name matches

    suspend fun getContacts(): List<Contact> {
        return try {
            contactCollection.get().await().toObjects(Contact::class.java)
        } catch (e: Exception) {
            emptyList() // Devuelve una lista vacía en caso de error
        }
    }

    suspend fun getContactsWithId(): List<Pair<String, Contact>>{
        return try{
            contactCollection.get().await().documents.mapNotNull { document ->
                document.toObject(Contact::class.java)?.let { contact ->
                    document.id to contact
                }
            }
        }catch(e: Exception){
            emptyList()
        }

    }
    suspend fun addContact(contact: Contact): String? {
        return try {
            val contactData = contact.copy(timestamp = System.currentTimeMillis()) // Added timestamp
            val documentRef = contactCollection.add(contactData).await()
            documentRef.id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun updateReadState(uid:String): Boolean {
        return try {
            contactCollection.document(uid).update("read", true).await() // ✅ Set read to true
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



}
