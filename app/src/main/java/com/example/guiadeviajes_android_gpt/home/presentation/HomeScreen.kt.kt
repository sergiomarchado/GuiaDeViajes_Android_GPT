package com.example.guiadeviajes_android_gpt.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        topBar = {
            HomeTopAppBar(
                userName = state.userName,
                userTokens = state.userTokens,
                onMenuClick = { scope.launch { drawerState.open() } },
                backgroundColor = Color(0xFF011A30)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                    top = paddingValues.calculateTopPadding(),
                    bottom = 0.dp
                )
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "¡Bienvenid@ a PET EXPLORER 🐾!",
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Completa los campos y selecciona un interés para realizar una búsqueda inteligente con IA.\n(Puedes desplazarte lateralmente)",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Ciudad / País (stateless)
            CityCountryInputs(
                country = state.country,
                onCountryChange = viewModel::onCountryChanged,
                city = state.city,
                onCityChange = viewModel::onCityChanged
            )
            Spacer(Modifier.height(8.dp))

            // Intereses directamente desde el VM
            InterestsSection(
                museumsChecked              = state.museums,
                onMuseumsCheckedChange      = viewModel::setMuseums,
                restaurantsChecked          = state.restaurants,
                onRestaurantsCheckedChange  = viewModel::setRestaurants,
                landmarksChecked            = state.landmarks,
                onLandmarksCheckedChange    = viewModel::setLandmarks,
                parksChecked                = state.parks,
                onParksCheckedChange        = viewModel::setParks,
                beachesChecked              = state.beaches,
                onBeachesCheckedChange      = viewModel::setBeaches,
                hotelsChecked               = state.hotels,
                onHotelsCheckedChange       = viewModel::setHotels,
                campingsChecked             = state.campings,
                onCampingsCheckedChange     = viewModel::setCampings,
                pipicanChecked              = state.pipican,
                onPipicanCheckedChange      = viewModel::setPipican,
                walkingZonesChecked         = state.walkingZones,
                onWalkingZonesCheckedChange = viewModel::setWalkingZones,
                vetsChecked                 = state.vets,
                onVetsCheckedChange         = viewModel::setVets,
                hospitalsChecked            = state.hospitals,
                onHospitalsCheckedChange    = viewModel::setHospitals,
                groomersChecked             = state.groomers,
                onGroomersCheckedChange     = viewModel::setGroomers,
                dogResortsChecked           = state.dogResorts,
                onDogResortsCheckedChange   = viewModel::setDogResorts,
                petStoresChecked            = state.petStores,
                onPetStoresCheckedChange    = viewModel::setPetStores
            )
            Spacer(Modifier.height(16.dp))

            SearchButton(
                isLoading = state.isLoading,
                enabled = state.city.isNotBlank(),
                onSearch = {
                    val ciudadRaw = state.city.trim()
                    val paisRaw = state.country.trim()

                    if (ciudadRaw.isNotBlank()) {
                        val interesesRaw = viewModel.buildInterestsString(state)
                        val ciudadEnc = android.net.Uri.encode(ciudadRaw)
                        val paisEnc = android.net.Uri.encode(paisRaw)
                        val intEnc = android.net.Uri.encode(interesesRaw)

                        navController.navigate(
                            com.example.guiadeviajes_android_gpt.navigation.NavRoutes.Result.build(
                                ciudadEnc, paisEnc, intEnc
                            )
                        ) {
                            popUpTo(com.example.guiadeviajes_android_gpt.navigation.NavRoutes.Home.route) { inclusive = false }
                        }

                        viewModel.resetForm()
                    } else {
                        android.util.Log.w("HOME_SCREEN", "Ciudad vacía: sin búsqueda")
                    }
                }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
