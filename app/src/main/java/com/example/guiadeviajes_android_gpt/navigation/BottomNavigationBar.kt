package com.example.guiadeviajes_android_gpt.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Barra de navegación inferior basada en Material3.
 *
 * @param navController Controlador de navegación.
 * @param items Lista de pantallas que aparecerán en la barra.
 * @param backgroundColor Color de fondo de la barra.
 * @param contentColor Color del contenido (iconos y textos).
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<BottomBarScreen>,
    backgroundColor: Color = Color(0xFF011A30),
    contentColor: Color = Color.White
) {
    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = backgroundColor,
        contentColor = contentColor,
        tonalElevation = 8.dp
    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route

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
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
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
