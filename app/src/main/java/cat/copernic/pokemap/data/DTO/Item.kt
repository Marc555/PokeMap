package cat.copernic.pokemap.data.DTO

import java.util.Date

data class Item(
    var id: String = "",

    val name: String = "",
    val description: String = "",

    val imageUrl: String = "",

    val likes: Long = 0,
    val dislikes: Long = 0,

    val userId: String = "",
    val categoryId: String = "",

    //val location: Location
)