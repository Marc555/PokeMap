package cat.copernic.pokemap.data.DTO;

data class Contact(
    val name: String = "",
    val emailFrom: String? = "",
    val emailTo: String = "",
    val subject: String = "",
    val description: String = "",
    val imageUrl: String? = null // Store image URL
    )
