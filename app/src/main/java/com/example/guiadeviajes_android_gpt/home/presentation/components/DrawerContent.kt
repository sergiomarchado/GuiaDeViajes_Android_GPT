package com.example.guiadeviajes_android_gpt.home.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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


@Composable
fun DrawerContent(
    navController: NavController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    viewModel: HomeViewModel
) {
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
        NavigationDrawerItem(
            label = { Text("Inicio", color = Color.White) },
            selected = false,
            onClick = { scope.launch { drawerState.close() } },
            icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.White) }
        )
        NavigationDrawerItem(
            label = { Text("Comprar tokens", color = Color.White) },
            selected = false,
            onClick = { scope.launch { drawerState.close() } },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White) }
        )
        NavigationDrawerItem(
            label = { Text("Editar perfil", color = Color.White) },
            selected = false,
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate("profile")
            },
            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) }
        )
        NavigationDrawerItem(
            label = { Text("Cerrar sesión", color = Color.White) },
            selected = false,
            onClick = {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White) }
        )
    }
}
