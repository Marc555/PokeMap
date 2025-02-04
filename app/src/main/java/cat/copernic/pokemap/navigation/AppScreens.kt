package cat.copernic.pokemap.navigation

sealed class AppScreens(val rute: String) {
    object Login: AppScreens("login")
    object Home: AppScreens("home")
}