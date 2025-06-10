package com.example.guiadeviajes_android_gpt.home.presentation
/**
 * HomeScreen.kt
 *
 * Pantalla principal donde el usuario selecciona ciudad, país e intereses para generar
 * recomendaciones de viaje pet-friendly mediante IA.
 * Incluye AppBar con drawer, formulario de inputs y botón de búsqueda.
 */
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.TextFieldValue
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
    // Datos de usuario simulados (a sustituir por datos reales desde ViewModel)
    val userName   = "Sergio"
    val userTokens = 120

    // Estado para país seleccionado y campo de ciudad
    val selectedCountryState = remember { mutableStateOf("España") }
    val cityQueryState       = remember { mutableStateOf(TextFieldValue("")) }

    // Variables booleanas para controlar los checkboxes de intereses
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

    // Estructura principal UI
    Scaffold(
        // Protege únicamente la parte superior para no superponer la status bar
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),

        // Barra superior con nombre de usuario, tokens y botón de menú para el Drawer
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
        // Obtenemos la dirección de layout para calcular correctamente los paddings
        val layoutDirection = LocalLayoutDirection.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                // Padding dinámico según insets del Scaffold
                .padding(
                    start  = paddingValues.calculateStartPadding(layoutDirection),
                    end    = paddingValues.calculateEndPadding(layoutDirection),
                    top    = paddingValues.calculateTopPadding(),
                    bottom = 0.dp
                )
                // Margen interno fijo alrededor del contenido
                .padding(horizontal = 32.dp, vertical = 16.dp)
                // Permite desplazarse verticalmente si el contenido excede la pantalla
                .verticalScroll(rememberScrollState()),
            verticalArrangement   = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Título de bienvenida con estilo
            Text(
                text     = "¡Bienvenid@ a PET EXPLORER 🐾!",
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Subtítulo con instrucciones para el usuario
            Text(
                text     = "Completa los campos y selecciona un interés para realizar una búsqueda inteligente con IA.\n(Puedes desplazarte lateralmente)",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Composable para introducir ciudad y seleccionar país
            CityCountryInputs(
                selectedCountry = selectedCountryState,
                cityQuery       = cityQueryState
            )
            Spacer(Modifier.height(8.dp))

            // Sección de intereses (checkboxes)
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

            // Botón principal para iniciar la búsqueda
            SearchButton(
                isLoading = false,  // Actualizar con isLoading de viewModel si aplica
                onSearch  = {
                    // Obtener texto limpio de ciudad y país
                    val ciudadRaw = cityQueryState.value.text.trim()
                    val paisRaw   = selectedCountryState.value.trim()

                    if (ciudadRaw.isNotBlank()) {
                        // Construye la lista de intereses seleccionados para la petición
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
                        // Si no hay ninguno, se usa valor por defecto
                        val interesesRaw = interesesLista
                            .joinToString(", ")
                            .ifBlank { "lugares pet friendly" }

                        // Codifica parámetros en URL para navegar a ResultScreen
                        val ciudadEnc = Uri.encode(ciudadRaw)
                        val paisEnc   = Uri.encode(paisRaw)
                        val intEnc    = Uri.encode(interesesRaw)
                        navController.navigate("result_screen/$ciudadEnc/$paisEnc/$intEnc") {
                            popUpTo("home") { inclusive = false }
                        }

                        // Reinicia el formulario para una nueva búsqueda
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
                        // Log para debugging si el usuario no introduce ciudad
                        Log.w("HOME_SCREEN", "Ciudad vacía: sin búsqueda")
                    }
                }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
