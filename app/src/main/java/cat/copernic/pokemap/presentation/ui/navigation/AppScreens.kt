package cat.copernic.pokemap.presentation.ui.navigation

sealed class AppScreens(val rute: String) {
    object Login: AppScreens("login")
    object Home: AppScreens("home")
    object Register: AppScreens("register")
    object Profile: AppScreens("profile")
    object Rankings: AppScreens("rankings")
    object Notifications: AppScreens("notifications")
    object Settings: AppScreens("settings")
    object EditProfile: AppScreens("editProfile/{userUid}") {
        fun createRoute(userUid: String) = "editProfile/$userUid"
    }
}