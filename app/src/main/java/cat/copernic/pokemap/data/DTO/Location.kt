package cat.copernic.pokemap.data.DTO

data class Location(
    var id: String = "",
    val latitude: Double,
    val longitude: Double,
    val address: String,
)
