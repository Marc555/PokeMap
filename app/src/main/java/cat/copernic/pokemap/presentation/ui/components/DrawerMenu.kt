package cat.copernic.pokemap.presentation.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cat.copernic.pokemap.presentation.ui.navigation.AppScreens
import cat.copernic.pokemap.utils.LanguageManager
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DrawerMenu(onClose: () -> Unit, navController: NavController) {
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color(0xFFFF7E7E))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.50f) // Takes 1/3 of the screen
                .clickable { onClose() }
                .background(Color(0xFFFF7E7E))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp), // Added top padding
                verticalArrangement = Arrangement.Top, // Aligns content to the top
                horizontalAlignment = Alignment.CenterHorizontally // Centers items horizontally
            ) {
                // Logo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = interactionSource, // Prevents default ripple effect
                            indication = null, // Disables the ripple effect
                            onClick = {

                            }
                        )
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,

                ) {
                    SvgLogo()
                }

                // Menu Items with Background and Rounded Corners
                MenuItem(LanguageManager.getText("home"), "home", Color(0xFF00AE14), navController, onClose)
                PerfileItem(LanguageManager.getText("profile"), Color(0xFF0060AE), navController, onClose)
                MenuItem("Ranking", "rankingMenu", Color(0xFF0060AE), navController, onClose)
                MenuItem(LanguageManager.getText("searchUsers"), "searchUsers", Color(0xFF0060AE), navController, onClose)

                // Pushes bottom items to the bottom
                Spacer(modifier = Modifier.weight(1f).clickable(
                    interactionSource = interactionSource, // Prevents default ripple effect
                    indication = null, // Disables the ripple effect
                    onClick = {

                    }).fillMaxHeight().fillMaxWidth())

                // Bottom Items
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MenuItem(LanguageManager.getText("settings"), "settings", Color(0xFF555555), navController, onClose)
                    SiginOutItem(LanguageManager.getText("logout"), Color(0xFFD32F2F), navController)
                }
            }
        }
    }
}

@Composable
fun PerfileItem(
    title: String,
    bgColor: Color,
    navController: NavController,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(AppScreens.Profile.rute)
                onClose()
            }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(1f) // Ensures all buttons have the same width
                .background(bgColor, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(), // Ensures text stays centered
                maxLines = 1,
                overflow = TextOverflow.Ellipsis // Prevents text from breaking into multiple lines
            )
        }
    }
}

@Composable
fun MenuItem(
    title: String,
    route: String,
    bgColor: Color,
    navController: NavController,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(route)
                onClose()
            }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(1f) // Ensures all buttons have the same width
                .background(bgColor, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(), // Ensures text stays centered
                maxLines = 1,
                overflow = TextOverflow.Ellipsis // Prevents text from breaking into multiple lines
            )
        }
    }
}

@Composable
fun SiginOutItem(
    title: String,
    bgColor: Color,
    navController: NavController,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val auth = FirebaseAuth.getInstance()
                auth.signOut()
                navController.navigate(AppScreens.Login.rute){
                    popUpTo(AppScreens.Login.rute) { inclusive = true }
                }
            }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(1f) // Ensures all buttons have the same width
                .background(bgColor, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(), // Ensures text stays centered
                maxLines = 1,
                overflow = TextOverflow.Ellipsis // Prevents text from breaking into multiple lines
            )
        }
    }
}
