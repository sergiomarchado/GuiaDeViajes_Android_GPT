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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.guiadeviajes_android_gpt.auth.presentation.*
import com.example.guiadeviajes_android_gpt.auth.viewmodel.AuthViewModel
import com.example.guiadeviajes_android_gpt.home.presentation.HomeScreen
import com.example.guiadeviajes_android_gpt.home.presentation.HomeViewModel
import com.example.guiadeviajes_android_gpt.home.presentation.ResultScreen
import com.example.guiadeviajes_android_gpt.home.presentation.ResultViewModel
import com.example.guiadeviajes_android_gpt.profile.presentation.ProfileScreen
import com.example.guiadeviajes_android_gpt.profile.viewmodel.ProfileViewModel
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

                    // ViewModels para las pantallas
                    val authViewModel: AuthViewModel      = hiltViewModel()
                    val homeViewModel: HomeViewModel      = hiltViewModel()
                    val profileViewModel: ProfileViewModel = hiltViewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(navController)
                        }
                        composable("login") {
                            LoginScreen(navController, authViewModel)
                        }
                        composable("register") {
                            RegisterScreen(navController, authViewModel)
                        }
                        composable("email_verification") {
                            EmailVerificationScreen(navController, authViewModel)
                        }
                        composable("forgot_password") {
                            ForgotPasswordScreen(navController, authViewModel)
                        }
                        composable("home") {
                            HomeScreen(navController, homeViewModel)
                        }
                        composable("profile") {
                            ProfileScreen(navController, profileViewModel)
                        }
                        composable(
                            route = "result_screen/{city}/{country}/{interests}",
                            arguments = listOf(
                                navArgument("city") { type = NavType.StringType },
                                navArgument("country") { type = NavType.StringType },
                                navArgument("interests") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val city      = backStackEntry.arguments?.getString("city") ?: ""
                            val country   = backStackEntry.arguments?.getString("country") ?: ""
                            val interests = backStackEntry.arguments?.getString("interests") ?: ""

                            // Aquí inyectamos ResultViewModel, no HomeViewModel
                            val resultViewModel: ResultViewModel = hiltViewModel()
                            ResultScreen(
                                navController = navController,
                                city          = city,
                                country       = country,
                                interests     = interests,
                                viewModel     = resultViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
