package com.example.guiadeviajes_android_gpt.home.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.guiadeviajes_android_gpt.navigation.BottomBarScreen
import com.example.guiadeviajes_android_gpt.navigation.NavRoutes
import kotlinx.coroutines.launch

/**
 * Contenido del Drawer lateral.
 * Desacoplado del ViewModel: recibe un callback onLogout.
 */
@Composable
fun DrawerContent(
    navController: NavController,
    drawerState: DrawerState,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()

    // Ruta actual para marcar 'selected'
    val currentRoute = navController.currentBackStackEntryAsState()
        .value?.destination?.route

    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF011A30),
        drawerContentColor = Color.White
    ) {
        Text(
            "Menú",
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        // Inicio
        NavigationDrawerItem(
            label = { Text("Inicio", color = Color.White) },
            selected = currentRoute == NavRoutes.Home.route,
            onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.White) }
        )

        // Resultados guardados
        NavigationDrawerItem(
            label = { Text("Resultados de consultas guardadas", color = Color.White) },
            selected = currentRoute == NavRoutes.SavedList.route,
            onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(NavRoutes.SavedList.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color.White) }
        )

        // Comprar tokens (si aún no existe la ruta, la añadimos más tarde)
        NavigationDrawerItem(
            label = { Text("Comprar tokens", color = Color.White) },
            selected = currentRoute == "buy_tokens",
            onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate("buy_tokens") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White) }
        )

        // Editar perfil
        NavigationDrawerItem(
            label = { Text("Editar perfil", color = Color.White) },
            selected = currentRoute == BottomBarScreen.Profile.route,
            onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(BottomBarScreen.Profile.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) }
        )

        // Cerrar sesión
        NavigationDrawerItem(
            label = { Text("Cerrar sesión", color = Color.White) },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onLogout()
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White) }
        )
    }
}
