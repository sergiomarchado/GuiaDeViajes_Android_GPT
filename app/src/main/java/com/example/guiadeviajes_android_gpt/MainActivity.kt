package com.example.guiadeviajes_android_gpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.guiadeviajes_android_gpt.auth.presentation.LoginScreen
import com.example.guiadeviajes_android_gpt.auth.presentation.RegisterScreen
import com.example.guiadeviajes_android_gpt.auth.viewmodel.AuthViewModel
import com.example.guiadeviajes_android_gpt.home.presentation.HomeScreen
import com.example.guiadeviajes_android_gpt.profile.presentation.ProfileScreen
import com.example.guiadeviajes_android_gpt.ui.theme.GuiaDeViajes_Android_GPTTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GuiaDeViajes_Android_GPTTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = hiltViewModel()

                    // Decide la pantalla inicial: home si hay usuario logueado, login si no.
                    val startDestination = if (authViewModel.isUserLoggedIn()) "home" else "login"

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable("login") {
                            LoginScreen(navController = navController)
                        }
                        composable("register") {
                            RegisterScreen(navController = navController)
                        }
                        composable("home") {
                            HomeScreen(navController = navController)
                        }
                        composable("profile") {
                            ProfileScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
