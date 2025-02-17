package cat.copernic.pokemap.data.DTO

data class Follow (
    val followed: String, // El UID del usuario seguido
    val follower: String // El UID del usuario que sigue
)