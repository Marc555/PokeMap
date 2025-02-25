package cat.copernic.pokemap.data.DTO

import com.google.firebase.Timestamp

data class Comment(
    var id: String = "",
    val text: String = "",
    var likes: Int = 0,
    var dislikes: Int = 0,
    val likedBy: List<String> = emptyList(),
    val dislikedBy: List<String> = emptyList(),

    var userId: String = "",
    var itemId: String = "",
)
