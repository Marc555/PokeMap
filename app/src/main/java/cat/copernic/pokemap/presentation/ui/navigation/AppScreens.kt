package cat.copernic.pokemap.presentation.ui.navigation

sealed class AppScreens(val rute: String) {
    object Login: AppScreens("login")
    object Home: AppScreens("home")
    object Register: AppScreens("register")
    object Profile: AppScreens("profile")
    object Rankings: AppScreens("rankings")
    object Notifications: AppScreens("notifications")
    object Settings: AppScreens("settings")
    object ContactForm: AppScreens("contact")
    object Onboarding : AppScreens("onboarding_screen") // ✅ Onboarding Screen
}
