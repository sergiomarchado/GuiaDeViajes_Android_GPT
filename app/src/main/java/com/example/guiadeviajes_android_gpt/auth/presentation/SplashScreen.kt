package com.example.guiadeviajes_android_gpt.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.R
import com.example.guiadeviajes_android_gpt.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pantalla de Splash que se muestra al abrir la app.
 * Muestra el logo a pantalla completa durante 2 segundos y luego
 * redirige a la pantalla correspondiente según si hay usuario logueado o no.
 */
@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(2000)
        if (viewModel.isUserLoggedIn()) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // Fondo mejorado para fundirse con la imagen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF011A30)) // Fondo más oscuro y profundo
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = "Splash Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

