package cat.copernic.pokemap

import android.app.Application
import cat.copernic.pokemap.utils.SharedPreferencesManager
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MyApp : Application() {
    companion object {
        lateinit var prefs: SharedPreferencesManager
    }
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        prefs = SharedPreferencesManager(this)
        // Enable Firebase Crashlytics crash reporting
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        // Log app startup event for debugging
        FirebaseCrashlytics.getInstance().log("App started")

    }

}
