package com.example.guiadeviajes_android_gpt.auth.presentation
/**
 * SplashScreen.kt
 *
 * Pantalla de bienvenida (splash) que se muestra al iniciar la aplicación.
 * Muestra el logo durante 2 segundos y redirige al usuario a la pantalla de Login o Home
 * en función de su estado de autenticación.
 */
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.R
import com.example.guiadeviajes_android_gpt.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // Efecto que controla la duración del splash y la navegación condicional
    LaunchedEffect(Unit) {
        // Espera 2 segundos antes de continuar
        delay(2000)
        // Redirige según estado de autenticación
        if (viewModel.isUserLoggedIn()) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            // Usuario no autenticado: navega a Login
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // Fondo mejorado para fundirse con la imagen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF011A30)) // Color de fondo
    ) {
        // Imagen del logo a pantalla completa
        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = "Splash Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

