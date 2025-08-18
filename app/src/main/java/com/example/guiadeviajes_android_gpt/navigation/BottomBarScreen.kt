package com.example.guiadeviajes_android_gpt.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.guiadeviajes_android_gpt.R

/**
 * Representa los ítems visibles en la BottomNavigationBar.
 * Cada ítem está vinculado a un NavRoutes concreto.
 */
sealed class BottomBarScreen(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    data object Home : BottomBarScreen(
        route = NavRoutes.Home.route,
        labelRes = R.string.nav_home,
        icon = Icons.Default.Home
    )

    data object Map : BottomBarScreen(
        route = NavRoutes.Map.route,
        labelRes = R.string.nav_map,
        icon = Icons.Default.Map
    )

    data object Profile : BottomBarScreen(
        route = NavRoutes.Profile.route,
        labelRes = R.string.nav_profile,
        icon = Icons.Default.Person
    )

    data object Promotions : BottomBarScreen(
        route = NavRoutes.Promotions.route,
        labelRes = R.string.nav_promotions,
        icon = Icons.Default.LocalOffer
    )
}
