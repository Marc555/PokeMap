package cat.copernic.pokemap.data.DTO

import com.google.firebase.Timestamp
data class Users(
    val email: String = "",
    val username: String = "",
    val name: String = "",
    val surname: String = "",
    val codeFriend: String = "",
    val imageUrl: String = "",
    val language: String? = "",
    val lastLogin: Timestamp = Timestamp.now(),
    val rol: Rol = Rol.USER,
)
