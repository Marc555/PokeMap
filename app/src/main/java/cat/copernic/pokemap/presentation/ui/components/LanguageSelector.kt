package cat.copernic.pokemap.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cat.copernic.pokemap.R
import cat.copernic.pokemap.utils.LanguageManager

@Composable
fun LanguageSelector(onLanguageSelected: (String) -> Unit ) {
    val context = LocalContext.current // Get context from a Composable function
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        val languages = listOf("en", "es", "ca")
        val flagResources = listOf(
            R.drawable.flag_en,
            R.drawable.flag_es,
            R.drawable.flag_ca
        )

        languages.zip(flagResources).forEach { (lang, flag) ->
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp)
                    .clip(CircleShape) // Makes it a circle
                    .background(Color.LightGray) // Optional background color
                    .clickable { LanguageManager.setLanguage(context, lang) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = flag),
                    contentDescription = "Flag of $lang",
                    modifier = Modifier.fillMaxSize(), // Adjust size as needed
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}
