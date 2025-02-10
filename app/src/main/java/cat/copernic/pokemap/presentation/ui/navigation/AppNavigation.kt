package cat.copernic.pokemap.presentation.ui.navigation


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import cat.copernic.pokemap.R
import cat.copernic.pokemap.presentation.ui.components.Hamburger
import cat.copernic.pokemap.presentation.ui.screens.AddCategoryDialog
import cat.copernic.pokemap.presentation.ui.screens.DrawerMenu
import cat.copernic.pokemap.presentation.ui.screens.Home
import cat.copernic.pokemap.presentation.ui.screens.Login
import cat.copernic.pokemap.presentation.ui.screens.Notifications
import cat.copernic.pokemap.presentation.ui.screens.Profile
import cat.copernic.pokemap.presentation.ui.screens.Rankings
import cat.copernic.pokemap.presentation.ui.screens.Register
import cat.copernic.pokemap.presentation.ui.screens.Settings
import cat.copernic.pokemap.presentation.ui.screens.Home
import cat.copernic.pokemap.presentation.viewModel.CategoryViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val categoryViewModel: CategoryViewModel = viewModel()
    val currentRoute = getCurrentRoute(navController)
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    // List of screens where the menu should NOT be shown
    val hideMenuScreens = listOf("login", "register")

    val showAddCategory = listOf("home")

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
                        title = {}, // Empty title
                        navigationIcon = {
                            Hamburger {
                                scope.launch { drawerState.open() }
                            }
                        },
                        actions = { // Add actions to the right
                            if (currentRoute in showAddCategory) {
                                IconButton(onClick = { showAddCategoryDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.AddCircle, // Replace with your drawable
                                        contentDescription = "Add category",
                                    )
                                }
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
                composable(AppScreens.Logout.rute) {
                    val auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    navController.navigate(AppScreens.Login.rute)
                }
                composable(AppScreens.Register.rute) { Register(navController) }
            }

            if (showAddCategoryDialog) {
                AddCategoryDialog(
                    onDismiss = { showAddCategoryDialog = false },
                    onConfirm = { newCategory ->
                        categoryViewModel.addCategory(newCategory)
                    }
                )
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
