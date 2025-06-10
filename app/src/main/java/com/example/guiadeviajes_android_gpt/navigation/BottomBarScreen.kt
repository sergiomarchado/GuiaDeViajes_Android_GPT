
package com.example.guiadeviajes_android_gpt.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.guiadeviajes_android_gpt.R
/**
 * BottomBarScreen.kt
 *
 * Define las rutas y metadatos de las pantallas accesibles desde la barra inferior de navegación.
 * Cada objeto representa un ítem de la BottomNavigationBar con su ruta, etiqueta y icono.
 *
 * @property route    Cadena única para la navegación Compose (NavHost).
 * @property labelRes Recurso de cadena para la etiqueta de la pestaña.
 * @property icon     Vector del icono a mostrar en la pestaña.
 */
sealed class BottomBarScreen(
    // Ruta de navegación para NavHost
    val route: String,
    // Recurso de texto para la etiqueta de la pestaña
    @StringRes val labelRes: Int,
    // Icono representativo de la pestaña
    val icon: ImageVector
) {
    /**
     * Pantalla principal Home.
     */
    data object Home       : BottomBarScreen("home",       R.string.nav_home,       Icons.Default.Home)
    /**
     * Pantalla de mapa.
     */
    data object Map        : BottomBarScreen("map",        R.string.nav_map,        Icons.Default.Map)
    /**
     * Pantalla de perfil.
     */
    data object Profile    : BottomBarScreen("profile",    R.string.nav_profile,    Icons.Default.Person)
    /**
     * Pantalla de promociones.
     */
    data object Promotions : BottomBarScreen("promotions", R.string.nav_promotions, Icons.Default.LocalOffer)
}
