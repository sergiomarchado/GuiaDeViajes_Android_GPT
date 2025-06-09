package com.example.guiadeviajes_android_gpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.guiadeviajes_android_gpt.auth.presentation.*
import com.example.guiadeviajes_android_gpt.auth.viewmodel.AuthViewModel
import com.example.guiadeviajes_android_gpt.home.presentation.HomeScreen
import com.example.guiadeviajes_android_gpt.home.presentation.HomeViewModel
import com.example.guiadeviajes_android_gpt.home.presentation.components.DrawerContent
import com.example.guiadeviajes_android_gpt.map.presentation.MapScreen
import com.example.guiadeviajes_android_gpt.navigation.BottomBarScreen
import com.example.guiadeviajes_android_gpt.navigation.BottomNavigationBar
import com.example.guiadeviajes_android_gpt.profile.presentation.ProfileScreen
import com.example.guiadeviajes_android_gpt.profile.viewmodel.ProfileViewModel
import com.example.guiadeviajes_android_gpt.promotions.presentation.PromotionsScreen
import com.example.guiadeviajes_android_gpt.results.ResultScreen
import com.example.guiadeviajes_android_gpt.results.ResultViewModel
import com.example.guiadeviajes_android_gpt.results.SavedResultDetailScreen
import com.example.guiadeviajes_android_gpt.results.SavedResultsListScreen
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
                val navController = rememberNavController()

                // Sólo Home/Profile tendrán drawer
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope: CoroutineScope = rememberCoroutineScope()
                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route
                val hasDrawer = currentRoute == BottomBarScreen.Home.route ||
                        currentRoute == BottomBarScreen.Profile.route

                val authVM    : AuthViewModel    = hiltViewModel()
                val homeVM    : HomeViewModel    = hiltViewModel()
                val profileVM : ProfileViewModel = hiltViewModel()

                if (hasDrawer) {
                    ModalNavigationDrawer(
                        drawerState   = drawerState,
                        drawerContent = {
                            DrawerContent(navController, scope, drawerState, homeVM)
                        }
                    ) {
                        ContentScaffold(
                            navController = navController,
                            authVM        = authVM,
                            homeVM        = homeVM,
                            profileVM     = profileVM,
                            drawerState   = drawerState,
                            scope         = scope
                        )
                    }
                } else {
                    ContentScaffold(
                        navController = navController,
                        authVM        = authVM,
                        homeVM        = homeVM,
                        profileVM     = profileVM,
                        drawerState   = drawerState,
                        scope         = scope
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentScaffold(
    navController: NavHostController,
    authVM: AuthViewModel,
    homeVM: HomeViewModel,
    profileVM: ProfileViewModel,
    drawerState: androidx.compose.material3.DrawerState,
    scope: CoroutineScope
) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        modifier       = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar      = {
            if (currentRoute == BottomBarScreen.Home.route ||
                currentRoute == BottomBarScreen.Profile.route) {
                BottomNavigationBar(
                    navController   = navController,
                    items           = listOf(
                        BottomBarScreen.Home,
                        BottomBarScreen.Map,
                        BottomBarScreen.Profile,
                        BottomBarScreen.Promotions
                    ),
                    backgroundColor = Color(0xFF011A30),
                    contentColor    = Color.White
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start  = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end    = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = paddingValues.calculateBottomPadding()
                ),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController    = navController,
                startDestination = "splash"
            ) {
                composable("splash")             { SplashScreen(navController) }
                composable("login")              { LoginScreen(navController, authVM) }
                composable("register")           { RegisterScreen(navController, authVM) }
                composable("email_verification") { EmailVerificationScreen(navController, authVM) }
                composable("forgot_password")    { ForgotPasswordScreen(navController, authVM) }

                composable(BottomBarScreen.Home.route) {
                    HomeScreen(
                        navController = navController,
                        viewModel     = homeVM,
                        drawerState   = drawerState,
                        scope         = scope
                    )
                }
                composable(BottomBarScreen.Map.route)     { MapScreen() }
                composable(BottomBarScreen.Profile.route) {
                    ProfileScreen(navController, drawerState, scope, profileVM)
                }
                composable(BottomBarScreen.Promotions.route) { PromotionsScreen() }

                composable(
                    route     = "result_screen/{city}/{country}/{interests}",
                    arguments = listOf(
                        navArgument("city")     { type = NavType.StringType },
                        navArgument("country")  { type = NavType.StringType },
                        navArgument("interests"){ type = NavType.StringType }
                    )
                ) { back ->
                    val city      = back.arguments!!.getString("city")!!
                    val country   = back.arguments!!.getString("country")!!
                    val interests = back.arguments!!.getString("interests")!!
                    val vm: ResultViewModel = hiltViewModel()
                    ResultScreen(navController, city, country, interests, vm)
                }

                composable("saved_list") {
                    SavedResultsListScreen(navController, drawerState, scope)
                }
                composable(
                    route     = "saved_detail/{id}",
                    arguments = listOf(navArgument("id"){ type = NavType.StringType })
                ) { back ->
                    SavedResultDetailScreen(
                        id            = back.arguments!!.getString("id")!!,
                        navController = navController
                    )
                }
            }
        }
    }
}
