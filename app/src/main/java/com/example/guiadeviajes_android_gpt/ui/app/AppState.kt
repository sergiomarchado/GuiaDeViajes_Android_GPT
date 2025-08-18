package com.example.guiadeviajes_android_gpt.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.guiadeviajes_android_gpt.navigation.BottomBarScreen

@Stable
class AppState(
    val navController: NavHostController,
    val drawerState: DrawerState,
    val snackbarHostState: SnackbarHostState
) {
    // Destinos que muestran Drawer + BottomBar (tus 4 pestañas)
    private val frameRoutes = setOf(
        BottomBarScreen.Home.route,
        BottomBarScreen.Map.route,
        BottomBarScreen.Profile.route,
        BottomBarScreen.Promotions.route
    )

    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val showFrame: Boolean
        @Composable get() = currentDestination?.route in frameRoutes
}

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): AppState = remember(navController, drawerState, snackbarHostState) {
    AppState(navController, drawerState, snackbarHostState)
}
