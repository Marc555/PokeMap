package cat.copernic.pokemap.data.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthRepository {

    private val auth: FirebaseAuth = Firebase.auth

    /**
     * Registra un usuario con correo y contraseña.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param onResult Callback que devuelve un booleano (éxito o fallo) y el UID del usuario (o un mensaje de error).
     */
    fun registerUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registro exitoso
                    val uid = auth.currentUser?.uid ?: ""
                    onResult(true, uid) // Devuelve el UID del usuario
                } else {
                    // Error en el registro
                    onResult(false, task.exception?.message)
                }
            }
    }

    /**
     * Obtiene el UID del usuario actualmente autenticado.
     */
    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }
}