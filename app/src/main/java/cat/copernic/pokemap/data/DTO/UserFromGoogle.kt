package cat.copernic.pokemap.data.DTO

import android.net.Uri

data class UserFromGoogle(
    var uid: String?= "",
    var email: String? = "",
    var username: String? = "",
    var name: String? = "",
    var surname: String? = "",
    var codeFriend: String? = "",
    var imageUrl: String? = null,
    var language: String? = "",
    var rol: Rol = Rol.USER,

    )
