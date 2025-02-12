package cat.copernic.pokemap.data.DTO

data class Item(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val image: String = "",
    var rate: Int = 0,

    val user: Users,
    val location: Location,
    val category: Category
)