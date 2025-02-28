package cat.copernic.pokemap.data.DTO;

data class Contact(
    val name: String = "",
    val emailFrom: String? = "",
    val emailTo: String = "",
    val subject: String = "",
    val description: String = "",
    val imageUrl: String? = null, // Store image URL
    val read:Boolean = false,
    val documentId: String? = null,
    val timestamp: Long? = null // Added timestamp

)
