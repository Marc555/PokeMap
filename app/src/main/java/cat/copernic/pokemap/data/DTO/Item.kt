package cat.copernic.pokemap.data.DTO

import com.google.firebase.Timestamp

data class Item(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val creationDate: Timestamp = Timestamp.now(),
    val imageUrl: String = "",

    val likes: Long = 0,
    val dislikes: Long = 0,

    val userId: String = "",
    val categoryId: String = "",

    val latitude: Double = 41.3851, // Default Barcelona
    val longitude: Double = 2.1734,

    val likedBy: List<String> = emptyList(),  // Lista de usuarios que han dado like
    val dislikedBy: List<String> = emptyList() // Lista de usuarios que han dado dislike
)
