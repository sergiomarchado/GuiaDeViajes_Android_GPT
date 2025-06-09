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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.home.presentation.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import com.example.guiadeviajes_android_gpt.navigation.BottomBarScreen

@Composable
fun DrawerContent(
    navController: NavController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    viewModel: HomeViewModel
) {
    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF011A30),
        drawerContentColor   = Color.White
    ) {
        Text(
            "Menú",
            modifier   = Modifier.padding(16.dp),
            color      = Color.White,
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold
        )

        // Inicio
        NavigationDrawerItem(
            label    = { Text("Inicio", color = Color.White) },
            selected = false,
            onClick  = {
                scope.launch {
                    drawerState.close()
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            },
            icon     = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.White) }
        )

        // Resultados guardados
        NavigationDrawerItem(
            label    = { Text("Resultados de consultas guardadas", color = Color.White) },
            selected = false,
            onClick  = {
                scope.launch {
                    drawerState.close()
                    navController.navigate("saved_list")
                }
            },
            icon     = { Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color.White) }
        )

        // Comprar tokens
        NavigationDrawerItem(
            label    = { Text("Comprar tokens", color = Color.White) },
            selected = false,
            onClick  = {
                scope.launch {
                    drawerState.close()
                    navController.navigate("buy_tokens")
                }
            },
            icon     = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White) }
        )

        // Editar perfil
        NavigationDrawerItem(
            label    = { Text("Editar perfil", color = Color.White) },
            selected = false,
            onClick  = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(BottomBarScreen.Profile.route)
                }
            },
            icon     = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) }
        )

        // Cerrar sesión
        NavigationDrawerItem(
            label    = { Text("Cerrar sesión", color = Color.White) },
            selected = false,
            onClick  = {
                scope.launch {
                    drawerState.close()
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            },
            icon     = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White) }
        )
    }
}
