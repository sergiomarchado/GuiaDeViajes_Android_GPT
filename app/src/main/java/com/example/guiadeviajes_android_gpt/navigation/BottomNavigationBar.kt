package com.example.guiadeviajes_android_gpt.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Barra inferior que se extiende bajo la zona de gestos y usa el azul oscuro que le pases.
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<BottomBarScreen>,
    backgroundColor: Color = Color(0xFF011A30),
    contentColor: Color = Color.White
) {
    val currentRoute = navController.currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()            // ocupa todo el ancho
            .navigationBarsPadding(),  // eleva la barra sobre la zona de gestos
        containerColor = backgroundColor,
        contentColor = contentColor,
        tonalElevation = 8.dp
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = stringResource(screen.labelRes)
                    )
                },
                label = {
                    Text(
                        text = stringResource(screen.labelRes),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = contentColor,
                    unselectedIconColor = contentColor.copy(alpha = 0.6f),
                    selectedTextColor = contentColor,
                    unselectedTextColor = contentColor.copy(alpha = 0.6f),
                    indicatorColor = backgroundColor.copy(alpha = 0f)
                )
            )
        }
    }
}