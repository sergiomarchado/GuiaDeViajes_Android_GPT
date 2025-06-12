package com.example.guiadeviajes_android_gpt
/**
 * MainActivity.kt
 *
 * Punto de entrada principal de la aplicación.
 * Configura el tema, la navegación, el Drawer (solo para Home y Profile de momento)
 * y gestiona el estado de Hilt y ViewModels.
 */

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
import com.example.guiadeviajes_android_gpt.map.presentation.PlacesListScreen
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

/**
 * Actividad principal que inicializa la UI y la navegación.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Permite al contenido de la UI extenderse hasta los bordes del dispositivo
        enableEdgeToEdge()

        setContent {
            // Aplica el tema personalizado de la app
            GuiaDeViajes_Android_GPTTheme {
                // Controlador de navegación de Compose
                val navController = rememberNavController()

                // Configuración del Drawer (solo en Home, Profile, Promotions y Map de momento)
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope: CoroutineScope = rememberCoroutineScope()

                // Observa la ruta actual para decidir si mostrar el Drawer
                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route
                val hasDrawer = currentRoute == BottomBarScreen.Home.route ||
                currentRoute == BottomBarScreen.Profile.route ||
                        currentRoute == BottomBarScreen.Promotions.route ||
                        currentRoute == BottomBarScreen.Map.route

                // Inyección de dependencias: ViewModels con Hilt
                val authVM    : AuthViewModel    = hiltViewModel()
                val homeVM    : HomeViewModel    = hiltViewModel()
                val profileVM : ProfileViewModel = hiltViewModel()

                if (hasDrawer) {
                    // Drawer modal para Home y Profile
                    ModalNavigationDrawer(
                        drawerState   = drawerState,
                        drawerContent = {
                            // Contenido personalizado del Drawer
                            DrawerContent(navController, scope, drawerState, homeVM)
                        }
                    ) {
                        // Scaffold con Drawer
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
                    // Scaffold sin Drawer para otras pantallas
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

/**
 * Composable que define la estructura base de la aplicación: Scaffold + NavHost.
 *
 * @param navController Controlador de navegación de Compose.
 * @param authVM ViewModel de autenticación.
 * @param homeVM ViewModel de la pantalla principal.
 * @param profileVM ViewModel del perfil.
 * @param drawerState Estado del Drawer (abierto/cerrado).
 * @param scope Alcance de corutinas para acciones de UI.
 */
@Composable
private fun ContentScaffold(
    navController: NavHostController,
    authVM: AuthViewModel,
    homeVM: HomeViewModel,
    profileVM: ProfileViewModel,
    drawerState: androidx.compose.material3.DrawerState,
    scope: CoroutineScope
) {
    // Observa la ruta actual para condicionar BottomBar
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        modifier       = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar      = {
            // Muestra la BottomNavigation solo en Home y Profile (de momento)
            if (currentRoute in listOf(
                    BottomBarScreen.Home.route,
                    BottomBarScreen.Profile.route,
                    BottomBarScreen.Promotions.route,
                    BottomBarScreen.Map.route
                )) {
                BottomNavigationBar(
                    navController   = navController,
                    items           = listOf(
                        BottomBarScreen.Home,
                        BottomBarScreen.Map,
                        BottomBarScreen.Profile,
                        BottomBarScreen.Promotions
                    ),
                    backgroundColor = Color(0xFF011A30),  // Color azul oscuro
                    contentColor    = Color.White              // Color de los iconos
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    // Ajusta paddings dinámicos según insets de Scaffold
                    start  = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end    = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = paddingValues.calculateBottomPadding()
                ),
            color = MaterialTheme.colorScheme.background
        ) {
            // Host de navegación con rutas y argumentos
            NavHost(
                navController    = navController,
                startDestination = "splash"
            ) {
                // Pantalla de splash inicial
                composable("splash")             { SplashScreen(navController) }
                // Flujo de autenticación
                composable("login")              { LoginScreen(navController, authVM) }
                composable("register")           { RegisterScreen(navController, authVM) }
                composable("email_verification") { EmailVerificationScreen(navController, authVM) }
                composable("forgot_password")    { ForgotPasswordScreen(navController, authVM) }

                // Home, Map, Profile, Promotions y Map en bottom bar
                composable(BottomBarScreen.Home.route) {
                    HomeScreen(
                        navController = navController,
                        viewModel     = homeVM,
                        drawerState   = drawerState,
                        scope         = scope
                    )
                }
                composable(BottomBarScreen.Map.route) {
                    MapScreen(navController, drawerState, scope)
                }
                composable("places_list") {
                    PlacesListScreen(navController)
                }
                composable(BottomBarScreen.Profile.route) {
                    ProfileScreen(navController, drawerState, scope, profileVM)
                }
                composable(BottomBarScreen.Promotions.route) {
                    PromotionsScreen(
                        navController = navController,
                        drawerState   = drawerState,
                        scope         = scope
                    )
                }

                // Pantalla de resultados tras búsqueda (con parámetros)
                composable(
                    route     = "result_screen/{city}/{country}/{interests}",
                    arguments = listOf(
                        navArgument("city")     { type = NavType.StringType },
                        navArgument("country")  { type = NavType.StringType },
                        navArgument("interests"){ type = NavType.StringType }
                    )
                ) { back ->
                    // Extrae parámetros de la ruta
                    val city      = back.arguments!!.getString("city")!!
                    val country   = back.arguments!!.getString("country")!!
                    val interests = back.arguments!!.getString("interests")!!

                    // ViewModel de resultados
                    val vm: ResultViewModel = hiltViewModel()
                    ResultScreen(navController, city, country, interests, vm)
                }

                // Lista y detalle de resultados guardados
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
