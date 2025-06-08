package com.example.guiadeviajes_android_gpt.home.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.home.presentation.components.CityCountryInputs
import com.example.guiadeviajes_android_gpt.home.presentation.components.DrawerContent
import com.example.guiadeviajes_android_gpt.home.presentation.components.HomeTopAppBar
import com.example.guiadeviajes_android_gpt.home.presentation.components.InterestsSection
import com.example.guiadeviajes_android_gpt.home.presentation.components.SearchButton
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Datos de usuario simulados
    val userName   = "Sergio"
    val userTokens = 120

    // Estado “crudo” para país y ciudad
    val selectedCountryState = remember { mutableStateOf("España") }
    val cityQueryState       = remember { mutableStateOf(TextFieldValue("")) }

    // Estados para cada checkbox de interés
    var museumsChecked     by remember { mutableStateOf(false) }
    var restaurantsChecked by remember { mutableStateOf(false) }
    var landmarksChecked   by remember { mutableStateOf(false) }
    var parksChecked       by remember { mutableStateOf(false) }
    var beachesChecked     by remember { mutableStateOf(false) }
    var hotelsChecked      by remember { mutableStateOf(false) }
    var pipicanChecked     by remember { mutableStateOf(false) }
    var walkingZonesChecked by remember { mutableStateOf(false) }
    var vetsChecked        by remember { mutableStateOf(false) }
    var dogResortsChecked  by remember { mutableStateOf(false) }
    var groomersChecked    by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navController = navController,
                viewModel     = viewModel,
                scope         = scope,
                drawerState   = drawerState
            )
        }
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    userName    = userName,
                    userTokens  = userTokens,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
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
                    text     = "¡Bienvenid@ a PET EXPLORER 🐾!",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text     = "Completa los campos y selecciona uno o varios intereses desplazándote lateralmente.",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Ciudad y País
                CityCountryInputs(
                    selectedCountry = selectedCountryState,
                    cityQuery       = cityQueryState
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Intereses
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
                    pipicanChecked = pipicanChecked,
                    onPipicanCheckedChange = { pipicanChecked = it },
                    walkingZonesChecked = walkingZonesChecked,
                    onWalkingZonesCheckedChange = { walkingZonesChecked = it },
                    vetsChecked = vetsChecked,
                    onVetsCheckedChange = { vetsChecked = it },
                    dogResortsChecked = dogResortsChecked,
                    onDogResortsCheckedChange = { dogResortsChecked = it },
                    groomersChecked = groomersChecked,
                    onGroomersCheckedChange = { groomersChecked = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Botón de Búsqueda
                SearchButton(
                    isLoading = false,
                    onSearch  = {
                        val ciudadRaw = cityQueryState.value.text.trim()
                        val paisRaw   = selectedCountryState.value.trim()

                        if (ciudadRaw.isNotBlank()) {
                            // Preparamos intereses
                            val interesesLista = mutableListOf<String>().apply {
                                if (museumsChecked)     add("museos")
                                if (restaurantsChecked) add("restaurantes admite mascotas")
                                if (landmarksChecked)   add("lugares de interes y monumentos")
                                if (parksChecked)       add("parques naturales")
                                if (beachesChecked)     add("playas perros")
                                if (hotelsChecked)      add("hoteles admite mascotas")
                                if (pipicanChecked)     add("pipican")
                                if (walkingZonesChecked) add("zonas paseo")
                                if (vetsChecked)        add("veterinarios y hospitales veterinarios 24h")
                                if (dogResortsChecked)  add("residencias caninas")
                                if (groomersChecked)    add("peluquerías caninas")
                            }
                            val interesesRaw = interesesLista.joinToString(", ")
                                .ifBlank { "lugares pet friendly" }

                            // Codificamos y navegamos
                            val ciudadEnc = Uri.encode(ciudadRaw)
                            val paisEnc   = Uri.encode(paisRaw)
                            val intEnc    = Uri.encode(interesesRaw)
                            navController.navigate("result_screen/$ciudadEnc/$paisEnc/$intEnc") {
                                popUpTo("home") { inclusive = false }
                            }

                            // Limpiamos formulario
                            cityQueryState.value = TextFieldValue("")
                            selectedCountryState.value = "España"
                            museumsChecked = false
                            restaurantsChecked = false
                            landmarksChecked = false
                            parksChecked = false
                            beachesChecked = false
                            hotelsChecked = false
                            pipicanChecked = false
                            walkingZonesChecked = false
                            vetsChecked = false
                            dogResortsChecked = false
                            groomersChecked = false
                        } else {
                            Log.w("HOME_SCREEN", "Ciudad vacía: sin búsqueda")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
