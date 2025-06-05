package com.example.guiadeviajes_android_gpt.home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.home.presentation.components.HomeTopAppBar
import com.example.guiadeviajes_android_gpt.home.presentation.components.InterestsSection
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val userName = "Sergio"
    val userTokens = 120

    var selectedCountry by remember { mutableStateOf("España") }
    var cityQuery by remember { mutableStateOf(TextFieldValue("")) }
    var museumsChecked by remember { mutableStateOf(false) }
    var restaurantsChecked by remember { mutableStateOf(false) }
    var landmarksChecked by remember { mutableStateOf(false) }
    var parksChecked by remember { mutableStateOf(false) }
    var beachesChecked by remember { mutableStateOf(false) }
    var hotelsChecked by remember { mutableStateOf(false) }
    var vetsChecked by remember { mutableStateOf(false) }
    var dogResortsChecked by remember { mutableStateOf(false) }
    var groomersChecked by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar Snackbar si hay error
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearErrorMessage()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF011A30),
                drawerContentColor = Color.White
            ) {
                Text(
                    "Menú",
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                NavigationDrawerItem(
                    label = { Text("Inicio", color = Color.White) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.White) }
                )
                NavigationDrawerItem(
                    label = { Text("Comprar tokens", color = Color.White) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White) }
                )
                NavigationDrawerItem(
                    label = { Text("Editar perfil", color = Color.White) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("profile")
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) }
                )
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión", color = Color.White) },
                    selected = false,
                    onClick = {
                        viewModel.logout()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    userName = userName,
                    userTokens = userTokens,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Guía de Viajes Pet-Friendly 🐶",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Completa los campos y selecciona uno o varios intereses desplazándote lateralmente.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = selectedCountry,
                    onValueChange = { selectedCountry = it },
                    label = { Text("País", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cityQuery,
                    onValueChange = { cityQuery = it },
                    label = { Text("Ciudad", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                InterestsSection(
                    museumsChecked = museumsChecked,
                    onMuseumsCheckedChange = { museumsChecked = it },
                    restaurantsChecked = restaurantsChecked,
                    onRestaurantsCheckedChange = { restaurantsChecked = it },
                    landmarksChecked = landmarksChecked,
                    onLandmarksCheckedChange = { landmarksChecked = it },
                    parksChecked = parksChecked,
                    onParksCheckedChange = { parksChecked = it },
                    beachesChecked = beachesChecked,
                    onBeachesCheckedChange = { beachesChecked = it },
                    hotelsChecked = hotelsChecked,
                    onHotelsCheckedChange = { hotelsChecked = it },
                    vetsChecked = vetsChecked,
                    onVetsCheckedChange = { vetsChecked = it },
                    dogResortsChecked = dogResortsChecked,
                    onDogResortsCheckedChange = { dogResortsChecked = it },
                    groomersChecked = groomersChecked,
                    onGroomersCheckedChange = { groomersChecked = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (cityQuery.text.isNotBlank()) {
                            val interests = mutableListOf<String>().apply {
                                if (museumsChecked) add("museos")
                                if (restaurantsChecked) add("restaurantes")
                                if (landmarksChecked) add("sitios emblemáticos")
                                if (parksChecked) add("parques naturales")
                                if (beachesChecked) add("playas")
                                if (hotelsChecked) add("hoteles o campings")
                                if (vetsChecked) add("veterinarios")
                                if (dogResortsChecked) add("residencias caninas")
                                if (groomersChecked) add("peluquerías caninas")
                            }

                            val finalPrompt = buildString {
                                append("Estoy viajando a ${cityQuery.text}, $selectedCountry con mi perro.")
                                if (interests.isNotEmpty()) {
                                    append(" Estoy interesado en: ${interests.joinToString(", ")}.")
                                }
                                append(" Dame recomendaciones de lugares, actividades y rutas turísticas adaptadas para viajar con perro.")
                                append(" Incluye siempre que sea posible: Una descripción y valoración del sitio, un teléfono de contacto ([teléfono](tel:123456789)), una página web si existe, y la dirección como enlace de Google Maps ([dirección](geo:0,0?q=Dirección))).")
                                append(" Confirma si aceptan perros.")
                                append(" Si es un veterinario, peluquería o residencia canina, indica los servicios que ofrece.")
                                append(" Devuélveme la respuesta en formato Markdown con títulos claros, listas y enlaces.")
                            }

                            viewModel.getTravelInformation(finalPrompt)
                            navController.navigate("result_screen")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Buscar")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
