package cat.copernic.pokemap.utils

import cat.copernic.pokemap.MyApp
import cat.copernic.pokemap.data.DTO.Rol
import cat.copernic.pokemap.data.DTO.UserFromGoogle

object StoredGoogleAuthCred{

    fun fillUserFromGoogle(uid :String,idToken: String,email: String, name: String,surname: String,imageUrl: String){
        cleanUserFromGoogle()
        MyApp.prefs.saveString("TEMPORAL_UID", uid)
        MyApp.prefs.saveString("TEMPORAL_TOKEN",idToken)
        MyApp.prefs.saveString("TEMPORAL_EMAIL_GOOGLE",email)
        MyApp.prefs.saveString("TEMPORAL_NAME_GOOGLE",name)
        MyApp.prefs.saveString("TEMPORAL_SURNAME_GOOGLE",surname)
        MyApp.prefs.saveString("TEMPORAL_IMAGEURL_GOOGLE",imageUrl)

    }

    fun cleanUserFromGoogle(){
        MyApp.prefs.removeKey("TEMPORAL_UID")
        MyApp.prefs.removeKey("TEMPORAL_TOKEN")
        MyApp.prefs.removeKey("TEMPORAL_EMAIL_GOOGLE")
        MyApp.prefs.removeKey("TEMPORAL_NAME_GOOGLE")
        MyApp.prefs.removeKey("TEMPORAL_SURNAME_GOOGLE")
        MyApp.prefs.removeKey("TEMPORAL_IMAGEURL_GOOGLE")

    }

    fun waitForUsername(username : String = "") : UserFromGoogle {
        return UserFromGoogle(
            uid =   MyApp.prefs.getString("TEMPORAL_UID"),
            email = MyApp.prefs.getString("TEMPORAL_EMAIL_GOOGLE"),
            name =  MyApp.prefs.getString("TEMPORAL_NAME_GOOGLE"),
            username =  username,
            surname =  MyApp.prefs.getString("TEMPORAL_SURNAME_GOOGLE"),
            imageUrl =  MyApp.prefs.getString("TEMPORAL_IMAGEURL_GOOGLE"),
        )
    }
}
