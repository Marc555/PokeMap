package cat.copernic.pokemap

import android.app.Application
import cat.copernic.pokemap.utils.SharedPreferencesManager
import com.google.firebase.FirebaseApp

class MyApp : Application() {
    companion object {
        lateinit var prefs: SharedPreferencesManager
    }
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        prefs = SharedPreferencesManager(this)
    }

}
