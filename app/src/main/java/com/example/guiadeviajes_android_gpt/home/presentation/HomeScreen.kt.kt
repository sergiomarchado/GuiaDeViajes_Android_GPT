package com.example.guiadeviajes_android_gpt.home.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.home.presentation.components.CityCountryInputs
import com.example.guiadeviajes_android_gpt.home.presentation.components.HomeTopAppBar
import com.example.guiadeviajes_android_gpt.home.presentation.components.InterestsSection
import com.example.guiadeviajes_android_gpt.home.presentation.components.SearchButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.DrawerState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    // 1) Datos de usuario (ejemplo)
    val userName   = "Sergio"
    val userTokens = 120

    // 2) Estados para inputs
    val selectedCountryState = remember { mutableStateOf("España") }
    val cityQueryState       = remember { mutableStateOf(TextFieldValue("")) }

    // 3) Estados para intereses
    var museumsChecked      by remember { mutableStateOf(false) }
    var restaurantsChecked  by remember { mutableStateOf(false) }
    var landmarksChecked    by remember { mutableStateOf(false) }
    var parksChecked        by remember { mutableStateOf(false) }
    var beachesChecked      by remember { mutableStateOf(false) }
    var hotelsChecked       by remember { mutableStateOf(false) }
    var campingsChecked     by remember { mutableStateOf(false) }
    var pipicanChecked      by remember { mutableStateOf(false) }
    var walkingZonesChecked by remember { mutableStateOf(false) }
    var vetsChecked         by remember { mutableStateOf(false) }
    var hospitalsChecked    by remember { mutableStateOf(false) }
    var dogResortsChecked   by remember { mutableStateOf(false) }
    var groomersChecked     by remember { mutableStateOf(false) }
    var petStoresChecked    by remember { mutableStateOf(false) }

    Scaffold(
        // Solo protegemos contra la status bar
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        topBar = {
            HomeTopAppBar(
                userName       = userName,
                userTokens     = userTokens,
                onMenuClick    = { scope.launch { drawerState.open() } },
                backgroundColor = Color(0xFF011A30)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                // Aplicamos solo padding start, end y top; bottom = 0
                .padding(
                    start  = paddingValues.calculateStartPadding(layoutDirection),
                    end    = paddingValues.calculateEndPadding(layoutDirection),
                    top    = paddingValues.calculateTopPadding(),
                    bottom = 0.dp
                )
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement   = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text     = "¡Bienvenid@ a PET EXPLORER 🐾!",
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text     = "Completa los campos y selecciona un interés para realizar una búsqueda inteligente con IA.\n(Puedes desplazarte lateralmente)",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Ciudad y país
            CityCountryInputs(
                selectedCountry = selectedCountryState,
                cityQuery       = cityQueryState
            )
            Spacer(Modifier.height(8.dp))

            // Sección de intereses
            InterestsSection(
                museumsChecked             = museumsChecked,
                onMuseumsCheckedChange     = { museumsChecked = it },
                restaurantsChecked         = restaurantsChecked,
                onRestaurantsCheckedChange = { restaurantsChecked = it },
                landmarksChecked           = landmarksChecked,
                onLandmarksCheckedChange   = { landmarksChecked = it },
                parksChecked               = parksChecked,
                onParksCheckedChange       = { parksChecked = it },
                beachesChecked             = beachesChecked,
                onBeachesCheckedChange     = { beachesChecked = it },
                hotelsChecked              = hotelsChecked,
                onHotelsCheckedChange      = { hotelsChecked = it },
                campingsChecked            = campingsChecked,
                onCampingsCheckedChange    = { campingsChecked = it },
                pipicanChecked             = pipicanChecked,
                onPipicanCheckedChange     = { pipicanChecked = it },
                walkingZonesChecked        = walkingZonesChecked,
                onWalkingZonesCheckedChange= { walkingZonesChecked = it },
                vetsChecked                = vetsChecked,
                onVetsCheckedChange        = { vetsChecked = it },
                hospitalsChecked           = hospitalsChecked,
                onHospitalsCheckedChange   = { hospitalsChecked = it },
                groomersChecked            = groomersChecked,
                onGroomersCheckedChange    = { groomersChecked = it },
                dogResortsChecked          = dogResortsChecked,
                onDogResortsCheckedChange  = { dogResortsChecked = it },
                petStoresChecked           = petStoresChecked,
                onPetStoresCheckedChange   = { petStoresChecked = it }
            )
            Spacer(Modifier.height(16.dp))

            // Botón de búsqueda
            SearchButton(
                isLoading = false,
                onSearch  = {
                    val ciudadRaw = cityQueryState.value.text.trim()
                    val paisRaw   = selectedCountryState.value.trim()

                    if (ciudadRaw.isNotBlank()) {
                        // Construir lista de intereses seleccionados
                        val interesesLista = mutableListOf<String>().apply {
                            if (museumsChecked)     add("museos")
                            if (restaurantsChecked) add("restaurantes admite mascotas")
                            if (landmarksChecked)   add("lugares de interes y monumentos")
                            if (parksChecked)       add("parques naturales")
                            if (beachesChecked)     add("playas perros")
                            if (hotelsChecked)      add("hoteles admite mascotas")
                            if (campingsChecked)    add("campings pet friendly")
                            if (pipicanChecked)     add("pipican")
                            if (walkingZonesChecked)add("zonas paseo")
                            if (vetsChecked)        add("veterinarios")
                            if (hospitalsChecked)   add("hospitales veterinarios 24h")
                            if (dogResortsChecked)  add("residencias caninas")
                            if (groomersChecked)    add("peluquerías caninas")
                            if (petStoresChecked)   add("tiendas de piensos")
                        }
                        val interesesRaw = interesesLista
                            .joinToString(", ")
                            .ifBlank { "lugares pet friendly" }

                        // Navegar a resultados
                        val ciudadEnc = Uri.encode(ciudadRaw)
                        val paisEnc   = Uri.encode(paisRaw)
                        val intEnc    = Uri.encode(interesesRaw)
                        navController.navigate("result_screen/$ciudadEnc/$paisEnc/$intEnc") {
                            popUpTo("home") { inclusive = false }
                        }

                        // Limpiar formulario
                        cityQueryState.value       = TextFieldValue("")
                        selectedCountryState.value = "España"
                        museumsChecked             = false
                        restaurantsChecked         = false
                        landmarksChecked           = false
                        parksChecked               = false
                        beachesChecked             = false
                        hotelsChecked              = false
                        campingsChecked            = false
                        pipicanChecked             = false
                        walkingZonesChecked        = false
                        vetsChecked                = false
                        hospitalsChecked           = false
                        dogResortsChecked          = false
                        groomersChecked            = false
                        petStoresChecked           = false
                    } else {
                        Log.w("HOME_SCREEN", "Ciudad vacía: sin búsqueda")
                    }
                }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
