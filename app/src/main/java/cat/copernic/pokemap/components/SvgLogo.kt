package cat.copernic.pokemap.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import cat.copernic.pokemap.R

@Composable
fun SvgLogo() {
    Image(
        painter = painterResource(id = R.drawable.logosvg),
        contentDescription = "Logo"
    )
}
