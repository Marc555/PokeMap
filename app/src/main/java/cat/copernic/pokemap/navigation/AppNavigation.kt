package cat.copernic.pokemap.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cat.copernic.pokemap.screens.Home
import cat.copernic.pokemap.screens.Login

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.Login.rute) {
        composable(AppScreens.Login.rute) { Login(navController) }
        composable(AppScreens.Home.rute) { Home(navController) }
    }
}