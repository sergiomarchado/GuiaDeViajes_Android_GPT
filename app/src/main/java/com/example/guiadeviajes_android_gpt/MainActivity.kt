package com.example.guiadeviajes_android_gpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.guiadeviajes_android_gpt.results.ResultScreen
import com.example.guiadeviajes_android_gpt.results.ResultViewModel
import com.example.guiadeviajes_android_gpt.results.SavedResultDetailScreen
import com.example.guiadeviajes_android_gpt.results.SavedResultsListScreen
import com.example.guiadeviajes_android_gpt.home.presentation.components.DrawerContent
import com.example.guiadeviajes_android_gpt.profile.presentation.ProfileScreen
import com.example.guiadeviajes_android_gpt.profile.viewmodel.ProfileViewModel
import com.example.guiadeviajes_android_gpt.ui.theme.GuiaDeViajes_Android_GPTTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GuiaDeViajes_Android_GPTTheme {
                // Estado del Drawer y scope
                val drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)
                val scope: CoroutineScope     = rememberCoroutineScope()
                val navController            = rememberNavController()

                // ViewModels
                val authViewModel: AuthViewModel       = hiltViewModel()
                val homeViewModel: HomeViewModel       = hiltViewModel()
                val profileViewModel: ProfileViewModel = hiltViewModel()

                ModalNavigationDrawer(
                    drawerState   = drawerState,
                    drawerContent = {
                        DrawerContent(
                            navController = navController,
                            scope         = scope,
                            drawerState   = drawerState,
                            viewModel     = homeViewModel
                        )
                    }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color    = MaterialTheme.colorScheme.background
                    ) {
                        NavHost(
                            navController    = navController,
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
                                // Ya no pasa NavController, sino drawerState y scope
                                ProfileScreen(
                                    drawerState      = drawerState,
                                    scope            = scope,
                                    viewModel        = profileViewModel
                                )
                            }
                            composable(
                                route      = "result_screen/{city}/{country}/{interests}",
                                arguments  = listOf(
                                    navArgument("city")     { type = NavType.StringType },
                                    navArgument("country")  { type = NavType.StringType },
                                    navArgument("interests"){ type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val city      = backStackEntry.arguments?.getString("city")     ?: ""
                                val country   = backStackEntry.arguments?.getString("country")  ?: ""
                                val interests = backStackEntry.arguments?.getString("interests")?: ""
                                val resultVm: ResultViewModel = hiltViewModel()
                                ResultScreen(
                                    navController = navController,
                                    city          = city,
                                    country       = country,
                                    interests     = interests,
                                    viewModel     = resultVm
                                )
                            }
                            composable("saved_list") {
                                SavedResultsListScreen(
                                    navController = navController,
                                    drawerState   = drawerState,
                                    scope         = scope
                                )
                            }
                            composable(
                                route     = "saved_detail/{id}",
                                arguments = listOf(navArgument("id"){ type = NavType.StringType })
                            ) { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("id") ?: ""
                                SavedResultDetailScreen(
                                    id            = id,
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
