package cat.copernic.pokemap.presentation.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.google.firebase.crashlytics.FirebaseCrashlytics

@Composable
fun CrashButton() {
    Button(onClick = {
        FirebaseCrashlytics.getInstance().log("Crash button clicked")
        throw RuntimeException("Test Crash")
    }) {
        Text("Crash App")
    }
}
