package cat.copernic.pokemap.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.copernic.pokemap.R
import cat.copernic.pokemap.utils.LanguageManager

@Composable
fun ContinueWithGoogleButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(50.dp)
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal =20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google), // ✅ Google logo
            contentDescription = "Google Logo",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = LanguageManager.getText("google auth"), // ✅ Custom text
            style = TextStyle(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        )
    }
}
