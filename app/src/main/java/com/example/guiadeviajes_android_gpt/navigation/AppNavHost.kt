package com.example.guiadeviajes_android_gpt.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.DrawerState
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.guiadeviajes_android_gpt.auth.presentation.*
import com.example.guiadeviajes_android_gpt.auth.viewmodel.AuthViewModel
import com.example.guiadeviajes_android_gpt.home.presentation.HomeScreen
import com.example.guiadeviajes_android_gpt.home.presentation.HomeViewModel
import com.example.guiadeviajes_android_gpt.map.presentation.MapScreen
import com.example.guiadeviajes_android_gpt.map.presentation.PlacesListScreen
import com.example.guiadeviajes_android_gpt.profile.presentation.ProfileScreen
import com.example.guiadeviajes_android_gpt.profile.viewmodel.ProfileViewModel
import com.example.guiadeviajes_android_gpt.promotions.presentation.PromotionsScreen
import com.example.guiadeviajes_android_gpt.results.ResultScreen
import com.example.guiadeviajes_android_gpt.results.ResultViewModel
import com.example.guiadeviajes_android_gpt.results.SavedResultDetailScreen
import com.example.guiadeviajes_android_gpt.results.SavedResultsListScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    drawerState: DrawerState,
    startDestination: String = NavRoutes.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        // Splash
        composable(NavRoutes.Splash.route) { SplashScreen(navController) }

        // Auth
        composable(NavRoutes.Login.route) {
            val vm: AuthViewModel = hiltViewModel()
            LoginScreen(navController, vm)
        }
        composable(NavRoutes.Register.route) {
            val vm: AuthViewModel = hiltViewModel()
            RegisterScreen(navController, vm)
        }
        composable(NavRoutes.EmailVerification.route) {
            val vm: AuthViewModel = hiltViewModel()
            EmailVerificationScreen(navController, vm)
        }
        composable(NavRoutes.ForgotPassword.route) {
            val vm: AuthViewModel = hiltViewModel()
            ForgotPasswordScreen(navController, vm)
        }

        // Frame (BottomBar + Drawer)
        composable(NavRoutes.Home.route) {
            val vm: HomeViewModel = hiltViewModel()
            val scope = rememberCoroutineScope()
            HomeScreen(navController, vm, drawerState, scope)
        }
        composable(NavRoutes.Map.route) {
            val scope = rememberCoroutineScope()
            MapScreen(navController, drawerState, scope)
        }
        composable(NavRoutes.PlacesList.route) {
            PlacesListScreen(navController)
        }
        composable(NavRoutes.Profile.route) {
            val vm: ProfileViewModel = hiltViewModel()
            val scope = rememberCoroutineScope()
            ProfileScreen(navController, drawerState, scope, vm)
        }
        composable(NavRoutes.Promotions.route) {
            val scope = rememberCoroutineScope()
            PromotionsScreen(navController, drawerState, scope)
        }

        // Resultados
        composable(
            route = NavRoutes.Result.route,
            arguments = listOf(
                navArgument("city") { type = NavType.StringType },
                navArgument("country") { type = NavType.StringType },
                navArgument("interests") { type = NavType.StringType }
            )
        ) { back ->
            val vm: ResultViewModel = hiltViewModel()
            ResultScreen(
                navController = navController,
                city = back.arguments!!.getString("city")!!,
                country = back.arguments!!.getString("country")!!,
                interests = back.arguments!!.getString("interests")!!,
                viewModel = vm
            )
        }

        // Guardados
        composable(NavRoutes.SavedList.route) {
            val scope = rememberCoroutineScope()
            SavedResultsListScreen(navController, drawerState, scope)
        }
        composable(
            route = NavRoutes.SavedDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { back ->
            SavedResultDetailScreen(
                id = back.arguments!!.getString("id")!!,
                navController = navController
            )
        }
    }
}
