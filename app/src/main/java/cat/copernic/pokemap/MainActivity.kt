package cat.copernic.pokemap

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cat.copernic.pokemap.navigation.AppNavigation //
import cat.copernic.pokemap.ui.theme.PokeMapTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // Lock portrait mode
        FirebaseApp.initializeApp(this)
        setContent {
            PokeMapTheme {
                AppNavigation()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PokeMapTheme {
        AppNavigation()
    }
}
