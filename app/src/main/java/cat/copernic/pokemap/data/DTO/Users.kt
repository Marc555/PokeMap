package cat.copernic.pokemap.data.DTO

data class Users(
    val username: String = "",
    val name: String = "",
    val surname: String = "",
    val rol: Rol = Rol.USER,
)