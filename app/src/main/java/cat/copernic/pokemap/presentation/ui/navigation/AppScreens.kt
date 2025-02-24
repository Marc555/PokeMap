package cat.copernic.pokemap.presentation.ui.navigation

sealed class AppScreens(val rute: String) {
    object Login: AppScreens("login")
    object Home: AppScreens("home")
    object Register: AppScreens("register")
    object Profile: AppScreens("profile")
    object ProfileUid: AppScreens("profile") {
        fun createRoute(userUid: String) = "profileUid/$userUid"
    }
    object Rankings: AppScreens("rankings")
    object Notifications: AppScreens("notifications")
    object Settings: AppScreens("settings")
    object EditProfile: AppScreens("editProfile/{userUid}") {
        fun createRoute(userUid: String) = "editProfile/$userUid"
    }
    object SearchUsers: AppScreens("searchUsers")
    object AdminSearchUsers: AppScreens("adminSearchUsers")
    object Items: AppScreens("items/{categoryId}")
    object ContactForm: AppScreens("contact")
    object Onboarding : AppScreens("onboarding_screen")
    object FollowersUsersScreen : AppScreens("followersUsersScreen") {
        fun createRoute(email: String) = "followersUsersScreen/$email"
    }    object FollowingUsersScreen : AppScreens("followingUsersScreen") {
        fun createRoute(email: String) = "followingUsersScreen/$email"
    }
}