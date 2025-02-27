package cat.copernic.pokemap.presentation.ui.navigation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import cat.copernic.pokemap.presentation.ui.components.Hamburger
import cat.copernic.pokemap.presentation.ui.components.DrawerMenu
import cat.copernic.pokemap.presentation.ui.screens.AdminSearchUsers
import cat.copernic.pokemap.presentation.ui.screens.EditProfile
import cat.copernic.pokemap.presentation.ui.screens.ContactForm
import cat.copernic.pokemap.presentation.ui.screens.FollowersUsersScreen
import cat.copernic.pokemap.presentation.ui.screens.FollowingUsersScreen
import cat.copernic.pokemap.presentation.ui.screens.Home
import cat.copernic.pokemap.presentation.ui.screens.ItemInside
import cat.copernic.pokemap.presentation.ui.screens.Items
import cat.copernic.pokemap.presentation.ui.screens.Login
import cat.copernic.pokemap.presentation.ui.screens.Notifications
import cat.copernic.pokemap.presentation.ui.screens.OnboardingScreen
import cat.copernic.pokemap.presentation.ui.screens.Profile
import cat.copernic.pokemap.presentation.ui.screens.Rankings
import cat.copernic.pokemap.presentation.ui.screens.Register
import cat.copernic.pokemap.presentation.ui.screens.SearchUsers
import cat.copernic.pokemap.presentation.ui.screens.Settings
import cat.copernic.pokemap.presentation.viewModel.CategoryViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
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
                composable(
                    route = AppScreens.ProfileUid.createRoute("{userUid}"),
                    arguments = listOf(navArgument("userUid") { type = NavType.StringType })
                ) { backStackEntry ->
                    val userUid = backStackEntry.arguments?.getString("userUid") ?: ""
                    Profile(navController, userUid)
                }
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
                composable(AppScreens.SearchUsers.rute) { SearchUsers(navController) }
                composable(AppScreens.AdminSearchUsers.rute) { AdminSearchUsers(navController) }

                composable(AppScreens.Items.rute) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getString("categoryId")
                    if (categoryId != null) {
                        Items(navController, categoryId)
                    }
                }

                composable(AppScreens.ItemInside.rute) { backStackEntry ->
                    val itemId = backStackEntry.arguments?.getString("itemId")
                    if (itemId != null) {
                        ItemInside(navController, itemId)
                    }
                }

                composable(AppScreens.ContactForm.rute) { ContactForm(navController)}
                composable(AppScreens.Onboarding.rute) { OnboardingScreen(navController) }
                composable(
                    route = AppScreens.FollowersUsersScreen.createRoute("{email}"),
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    FollowersUsersScreen(navController, email)
                }
                composable(
                    route = AppScreens.FollowingUsersScreen.createRoute("{email}"),
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    FollowingUsersScreen(navController, email)
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
