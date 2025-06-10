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
 * BottomNavigationBar.kt
 *
 * Composable que renderiza la barra de navegación inferior en la app.
 * Soporta gestos de navegación y utiliza un color de fondo y contenido personalizados.
 *
 * @param navController  Controlador de navegación para cambiar rutas.
 * @param items          Lista de BottomBarScreen que define rutas, etiquetas e iconos.
 * @param backgroundColor Color de fondo de la barra (por defecto azul oscuro corporativo).
 * @param contentColor    Color de los iconos y texto (por defecto blanco).
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<BottomBarScreen>,
    backgroundColor: Color = Color(0xFF011A30),
    contentColor: Color = Color.White
) {
    // Obtener la ruta actual del BackStack para marcar el item seleccionado
    val currentRoute = navController.currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    // NavigationBar de Material3 con elevación y padding para la zona de gestos
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()            // Ocupa el ancho de la pantalla
            .navigationBarsPadding(),  // Eleva la barra sobre la barra de gestos
        containerColor = backgroundColor,
        contentColor = contentColor,
        tonalElevation = 8.dp         // Sombra suave para destacar la barra
    ) {
        // Iterar por cada pantalla definida en los items
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
                            // Configuración para preservar el estado de la pila
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