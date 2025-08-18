package com.example.guiadeviajes_android_gpt.navigation

/**
 * Rutas centralizadas (sin strings mágicos).
 * Incluye helpers para construir rutas con argumentos.
 */
sealed class NavRoutes(val route: String) {

    // Auth & entry
    data object Splash : NavRoutes("splash")
    data object Login : NavRoutes("login")
    data object Register : NavRoutes("register")
    data object EmailVerification : NavRoutes("email_verification")
    data object ForgotPassword : NavRoutes("forgot_password")

    // Frame (BottomBar + Drawer)
    data object Home : NavRoutes("home")
    data object Map : NavRoutes("map")
    data object Profile : NavRoutes("profile")
    data object Promotions : NavRoutes("promotions")
    data object PlacesList : NavRoutes("places_list")

    // Resultados con argumentos
    data object Result : NavRoutes("result_screen/{city}/{country}/{interests}") {
        fun build(city: String, country: String, interests: String): String =
            "result_screen/${city}/${country}/${interests}"
    }

    // Guardados
    data object SavedList : NavRoutes("saved_list")
    data object SavedDetail : NavRoutes("saved_detail/{id}") {
        fun build(id: String): String = "saved_detail/$id"
    }
}
