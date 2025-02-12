package cat.copernic.pokemap.data.Repository

import cat.copernic.pokemap.data.DTO.Category
import cat.copernic.pokemap.data.DTO.Item
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ItemRepository {

    private val db = FirebaseFirestore.getInstance()
    private val itemsCollection = db.collection("items")

    suspend fun getItems(categoryId: String): List<Item> {
        return try {
            itemsCollection
                .whereEqualTo("categoryId", categoryId) // Filtra por la categoría
                .get()
                .await()
                .toObjects(Item::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Error, devuelve una lista vacía
        }
    }


    suspend fun addItem(item: Item): String? {
        return try {
            //Crear documento vacio
            val documentReference = itemsCollection.document()

            //Asignar el id generado por firestore al objeto item
            item.id = documentReference.id

            //Guardar el objeto item en firestore
            documentReference.set(item).await()

            //Devolver el id generado
            documentReference.id
        } catch (e: Exception) {
            e.printStackTrace()
            null // Fallo, devuelve null
        }
    }

    suspend fun updateItem(itemId: String, updatedItem: Item): Boolean {
        return try {
            // Obtener el documento de Firestore
            val documentReference = itemsCollection.document(itemId)

            // Actualizar el documento con los nuevos datos
            documentReference.set(updatedItem).await()
            true // Éxito
        } catch (e: Exception) {
            e.printStackTrace()
            false // Fallo
        }
    }

    suspend fun deleteItem(id: String): Boolean {
        return try {
            // Eliminar el documento de Firestore
            itemsCollection.document(id).delete().await()
            true // Éxito
        } catch (e: Exception) {
            false // Fallo
        }
    }
}