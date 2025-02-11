package cat.copernic.pokemap.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80, // Color principal del botón (habilitado)
    onPrimary = Color.White, // Color del contenido del botón (habilitado)
    surfaceVariant = Color.Gray, // Color de fondo del botón (deshabilitado)
    onSurfaceVariant = Color.White, // Color del contenido del botón (deshabilitado)
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color.Black,  // Fondo oscuro
    onBackground = Color.White,  // Texto sobre el fondo oscuro
    surface = Color.White,
    onSurface = Color.Black,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40, // Color principal del botón (habilitado)
    onPrimary = Color.White, // Color del contenido del botón (habilitado)
    surfaceVariant = Color.LightGray, // Color de fondo del botón (deshabilitado)
    onSurfaceVariant = Color.Black, // Color del contenido del botón (deshabilitado)
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFE2E9FF),  // Fondo claro
    onBackground = Color.Black,  // Texto sobre el fondo claro
    surface = Color.Black,
    onSurface = Color.White,
)

@Composable
fun PokeMapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}