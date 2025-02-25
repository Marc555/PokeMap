package cat.copernic.pokemap.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Aqui podeis definir variables de colores extra por si las necesitais
data class CustomColors(
    val confirmButton: Color,
    val deleteButton: Color,
    val popUpsMenu: Color
)

// Esquema de colores en modo oscuro
private val DarkColorScheme = darkColorScheme(
    surface = Color.Black, //Items/menus directamente encima del fondo
    onSurface = Color.White, //Textos/icono encima de primarios

    surfaceVariant = Color.Gray, //Items/menus secundarios
    onSurfaceVariant = Color.White, //Textos/icomos encima

    background = Color(0xFF26264D), //Color fondo
    onBackground = Color.White, //Texto/iconos directamente encima del fondo

    primary = Color.Gray, //Botones normales
    onPrimary = Color.Black, //Texto encima de los botones

    secondary = Color(0x7EABABAB), //Botones deshabilitados
    onSecondary = Color.White, //Texto encima de los botones deshabilitados
)

// Esquema de colores en modo claro
private val LightColorScheme = lightColorScheme(
    surface = Color.White, //Items/menus directamente encima del fondo
    onSurface = Color.Black, //Textos/icono encima de primarios

    surfaceVariant = Color.LightGray, //Items/menus secundarios
    onSurfaceVariant = Color.Black, //Textos/icomos encima

    background = Color(0xFFCEDAFF), //Color fondo
    onBackground = Color.Black, //Texto/iconos directamente encima del fondo

    primary = Color.LightGray, //Botones normales
    onPrimary = Color.White, //Texto encima de los botones

    secondary = Color(0x7EABABAB), //Botones deshabilitados
    onSecondary = Color.White, //Texto encima de los botones deshabilitados
)

private val DarkCustomColors = CustomColors(
    confirmButton = Color(0xFF005900),
    deleteButton = Color(0xFF930000),
    popUpsMenu = Color(0xFFCEBB0B)
)

private val LightCustomColors = CustomColors(
    confirmButton = Color(0xFFBBFABE),
    deleteButton = Color(0xFFF8C6C1),
    popUpsMenu = Color(0xFFAEE8D7)
)


val LocalCustomColors = staticCompositionLocalOf { LightCustomColors }

@Composable
fun PokeMapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
