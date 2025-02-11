package cat.copernic.pokemap.data.Repository

import cat.copernic.pokemap.data.DTO.Category
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CategoryRepository {

    private val db = FirebaseFirestore.getInstance()
    private val categoriesCollection = db.collection("categories")

    suspend fun addCategory(category: Category): String? {
        return try {
            // Crear una referencia a un nuevo documento con un ID generado automáticamente
            val documentReference = categoriesCollection.document()

            // Asignar el ID generado por Firestore al objeto Category
            category.id = documentReference.id

            // Guardar el objeto Category en Firestore
            documentReference.set(category).await()

            // Devolver el ID generado
            documentReference.id
        } catch (e: Exception) {
            e.printStackTrace()
            null // Fallo, devuelve null
        }
    }

    suspend fun getCategories(): List<Category> {
        return try {
            categoriesCollection.get().await().toObjects(Category::class.java)
        } catch (e: Exception) {
            emptyList() // Devuelve una lista vacía en caso de error
        }
    }

    suspend fun updateCategory(categoryId: String, updatedCategory: Category): Boolean {
        return try {
            val documentReference = categoriesCollection.document(categoryId)
            documentReference.set(updatedCategory).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteCategory(id: String): Boolean {
        return try {
            categoriesCollection.document(id).delete().await()
            true // Éxito
        } catch (e: Exception) {
            false // Fallo
        }
    }

    suspend fun getCategoryById(categoryId: String): Category? {
        return try {
            // Obtener el documento de Firestore
            val documentSnapshot = categoriesCollection.document(categoryId).get().await()

            // Convertir el documento a un objeto Category
            documentSnapshot.toObject(Category::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}