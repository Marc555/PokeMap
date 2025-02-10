package cat.copernic.pokemap.data.DTO

data class Location(
    val uid: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val item: Item,
)
