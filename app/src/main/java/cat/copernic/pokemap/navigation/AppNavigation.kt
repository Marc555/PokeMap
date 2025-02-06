package cat.copernic.pokemap.navigation


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.*
import cat.copernic.pokemap.components.Hamburger
import cat.copernic.pokemap.screens.Home
import cat.copernic.pokemap.screens.Login
import cat.copernic.pokemap.screens.Notifications
import cat.copernic.pokemap.screens.Profile
import cat.copernic.pokemap.screens.Rankings
import cat.copernic.pokemap.screens.Settings
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentRoute = getCurrentRoute(navController)


    // List of screens where the menu should NOT be shown
    val hideMenuScreens = listOf("login", "register")

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true, // Allow swipe gestures
        drawerContent = {
            if (currentRoute !in hideMenuScreens) {
                DrawerMenu({
                    scope.launch { drawerState.close() }
                }, navController)
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (currentRoute !in hideMenuScreens) {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            Hamburger {
                                scope.launch { drawerState.open() }
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(innerPadding),
            ) {
                composable("home") { Home(navController) }
                composable(AppScreens.Login.rute) { Login(navController) }
                composable(AppScreens.Profile.rute) { Profile(navController) }
                composable(AppScreens.Notifications.rute) { Notifications(navController) }
                composable(AppScreens.Rankings.rute) { Rankings(navController) }
                composable(AppScreens.Settings.rute) { Settings(navController) }
                composable(AppScreens.Logout.rute) {
                    val auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    navController.navigate(AppScreens.Login.rute)
                }
            }
        }
    }

}

// Function to get the current route
@Composable
fun getCurrentRoute(navController: NavController): String? {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    return currentBackStackEntry?.destination?.route
}
