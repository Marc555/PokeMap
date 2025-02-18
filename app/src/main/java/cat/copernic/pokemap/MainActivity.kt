package cat.copernic.pokemap

import android.content.res.Configuration
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import cat.copernic.pokemap.presentation.ui.navigation.AppNavigation //
import cat.copernic.pokemap.presentation.ui.theme.PokeMapTheme
import cat.copernic.pokemap.utils.LanguageManager
import com.google.firebase.FirebaseApp

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // Lock portrait mode
        FirebaseApp.initializeApp(this)
        // ✅ Initialize and log language on app start
        LanguageManager.setLanguage(this) // ✅ Ensures language is properly initialized
        setContent {
            PokeMapTheme {
                AppNavigation()
            }
        }
    }
}


@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GreetingPreview() {
    PokeMapTheme {
        AppNavigation()
    }
}
