package cat.copernic.pokemap.presentation.ui.navigation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import cat.copernic.pokemap.presentation.ui.components.Hamburger
import cat.copernic.pokemap.presentation.ui.components.DrawerMenu
import cat.copernic.pokemap.presentation.ui.screens.EditProfile
import cat.copernic.pokemap.presentation.ui.screens.Home
import cat.copernic.pokemap.presentation.ui.screens.Login
import cat.copernic.pokemap.presentation.ui.screens.Notifications
import cat.copernic.pokemap.presentation.ui.screens.Profile
import cat.copernic.pokemap.presentation.ui.screens.Rankings
import cat.copernic.pokemap.presentation.ui.screens.Register
import cat.copernic.pokemap.presentation.ui.screens.Settings
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
        gesturesEnabled = currentRoute !in hideMenuScreens, // Allow swipe gestures
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
                        colors = topAppBarColors(MaterialTheme.colorScheme.background),
                        title = {}, // Empty title
                        navigationIcon = {
                            Hamburger {
                                scope.launch { drawerState.open() }
                            }
                        }
                    )
                }
            }
        )
        { innerPadding ->
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
                composable(AppScreens.Register.rute) { Register(navController) }
                composable(
                    route = AppScreens.EditProfile.rute,
                    arguments = listOf(navArgument("userUid") { type = NavType.StringType })
                ) { backStackEntry ->
                    val userUid = backStackEntry.arguments?.getString("userUid") ?: ""
                    EditProfile(navController, userUid)
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
