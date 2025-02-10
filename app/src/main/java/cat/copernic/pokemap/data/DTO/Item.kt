package cat.copernic.pokemap.data.DTO

data class Item(
    val uid: String,
    val name: String,
    val description: String,
    val image: String,
    val location: Location,
    val category: Category
)