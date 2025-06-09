// File: app/src/main/java/com/example/guiadeviajes_android_gpt/ui/navigation/Screen.kt
package com.example.guiadeviajes_android_gpt.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Home    : Screen("home",    Icons.Default.Home,       "Inicio")
    object Map     : Screen("map",     Icons.Default.Map,        "Mapa")
    object Profile : Screen("profile", Icons.Default.Person,     "Perfil")
    object Promo   : Screen("promo",   Icons.Default.LocalOffer, "Promos")
}
