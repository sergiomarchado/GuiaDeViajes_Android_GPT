package com.example.guiadeviajes_android_gpt.ui.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.guiadeviajes_android_gpt.home.presentation.HomeViewModel
import com.example.guiadeviajes_android_gpt.home.presentation.components.DrawerContent
import com.example.guiadeviajes_android_gpt.navigation.AppNavHost
import com.example.guiadeviajes_android_gpt.navigation.BottomBarScreen
import com.example.guiadeviajes_android_gpt.navigation.BottomNavigationBar

/**
 * Contenedor principal (Scaffold + Drawer + BottomBar).
 * No depende de AppState para que puedas migrarlo por partes.
 */
@Composable
fun AppScaffold(
    navController: NavHostController,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    showFrame: Boolean
) {
    val content: @Composable () -> Unit = {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showFrame) {
                    BottomNavigationBar(
                        navController = navController,
                        items = listOf(
                            BottomBarScreen.Home,
                            BottomBarScreen.Map,
                            BottomBarScreen.Profile,
                            BottomBarScreen.Promotions
                        ),
                        backgroundColor = Color(0xFF011A30),
                        contentColor = Color.White
                    )
                }
            }
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavHost(
                    navController = navController,
                    drawerState = drawerState
                )
            }
        }
    }

    if (showFrame) {
        // El Drawer usa el VM de Home si tu DrawerContent lo necesita
        val homeVM: HomeViewModel = hiltViewModel()
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    navController = navController,
                    drawerState = drawerState,
                    onLogout = homeVM::logout
                )
            },
            content = content
        )
    } else {
        content()
    }
}
