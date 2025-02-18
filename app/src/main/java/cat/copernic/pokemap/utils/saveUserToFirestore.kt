package cat.copernic.pokemap.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

fun saveUserToFirestore(uid: String, username: String, email: String?, profilePicture: String) {
    val db = FirebaseFirestore.getInstance()
    val userProfile = hashMapOf(
        "uid" to uid,
        "username" to username,
        "email" to email,
        "profilePicture" to profilePicture,
        "createdAt" to System.currentTimeMillis()
    )

    db.collection("users").document(uid)
        .set(userProfile)
        .addOnSuccessListener {
            Log.d("Firestore", "User profile saved successfully!")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error saving user profile: ${e.localizedMessage}")
        }

}

suspend fun checkIfUserExists(email: String): Boolean {
    return try {
        val db = FirebaseFirestore.getInstance()
        val querySnapshot = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await() // ✅ Wait for Firestore response

        !querySnapshot.isEmpty // ✅ Returns true if email exists, false if not
    } catch (e: Exception) {
        Log.e("FirestoreCheck", "Error checking Firestore: ${e.localizedMessage}")
        false // Treat errors as "user does not exist"
    }
}

suspend fun getSignInMethodsForEmail(email: String): List<String> {
    return try {
        val auth = FirebaseAuth.getInstance()
        val result = auth.fetchSignInMethodsForEmail(email).await()
        val providers = result.signInMethods ?: emptyList()

        Log.d("FirebaseAuth", "Providers for $email: $providers")
        providers
    } catch (e: Exception) {
        Log.e("FirebaseAuth", "Error checking sign-in methods: ${e.localizedMessage}")
        emptyList()
    }
}

