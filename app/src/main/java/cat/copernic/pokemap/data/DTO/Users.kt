package cat.copernic.pokemap.data.DTO

data class Users(
    val email: String = "",
    val username: String = "",
    val name: String = "",
    val surname: String = "",
    val codeFriend: String = "",
    val imageUrl: String = "",
    val rol: Rol = Rol.USER,
)