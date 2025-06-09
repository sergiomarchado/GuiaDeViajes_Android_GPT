
package com.example.guiadeviajes_android_gpt.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.guiadeviajes_android_gpt.R

sealed class BottomBarScreen(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    object Home       : BottomBarScreen("home",       R.string.nav_home,       Icons.Default.Home)
    object Map        : BottomBarScreen("map",        R.string.nav_map,        Icons.Default.Map)
    object Profile    : BottomBarScreen("profile",    R.string.nav_profile,    Icons.Default.Person)
    object Promotions : BottomBarScreen("promotions", R.string.nav_promotions, Icons.Default.LocalOffer)
}
