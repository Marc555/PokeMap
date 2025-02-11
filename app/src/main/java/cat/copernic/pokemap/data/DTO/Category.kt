package cat.copernic.pokemap.data.DTO

data class Category(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val image: String = "",
    val items: List<Item> = emptyList()
)