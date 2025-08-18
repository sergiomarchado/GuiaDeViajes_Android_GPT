package com.example.guiadeviajes_android_gpt.home.presentation

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "Sergio",   // TODO: cargar de repo si procede
    val userTokens: Int = 120,         // TODO: cargar de repo si procede

    val country: String = "España",
    val city: String = "",

    // Intereses
    val museums: Boolean = false,
    val restaurants: Boolean = false,
    val landmarks: Boolean = false,
    val parks: Boolean = false,
    val beaches: Boolean = false,
    val hotels: Boolean = false,
    val campings: Boolean = false,
    val pipican: Boolean = false,
    val walkingZones: Boolean = false,
    val vets: Boolean = false,
    val hospitals: Boolean = false,
    val dogResorts: Boolean = false,
    val groomers: Boolean = false,
    val petStores: Boolean = false,

    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun onCountryChanged(country: String) {
        _uiState.update { it.copy(country = country) }
    }

    fun onCityChanged(city: String) {
        _uiState.update { it.copy(city = city) }
    }

    // Toggles (uno por interés). Si prefieres, puedo pasarlos a un único método con key.
    fun setMuseums(v: Boolean)        = toggle { it.copy(museums = v) }
    fun setRestaurants(v: Boolean)    = toggle { it.copy(restaurants = v) }
    fun setLandmarks(v: Boolean)      = toggle { it.copy(landmarks = v) }
    fun setParks(v: Boolean)          = toggle { it.copy(parks = v) }
    fun setBeaches(v: Boolean)        = toggle { it.copy(beaches = v) }
    fun setHotels(v: Boolean)         = toggle { it.copy(hotels = v) }
    fun setCampings(v: Boolean)       = toggle { it.copy(campings = v) }
    fun setPipican(v: Boolean)        = toggle { it.copy(pipican = v) }
    fun setWalkingZones(v: Boolean)   = toggle { it.copy(walkingZones = v) }
    fun setVets(v: Boolean)           = toggle { it.copy(vets = v) }
    fun setHospitals(v: Boolean)      = toggle { it.copy(hospitals = v) }
    fun setDogResorts(v: Boolean)     = toggle { it.copy(dogResorts = v) }
    fun setGroomers(v: Boolean)       = toggle { it.copy(groomers = v) }
    fun setPetStores(v: Boolean)      = toggle { it.copy(petStores = v) }

    private fun toggle(block: (HomeUiState) -> HomeUiState) {
        _uiState.update(block)
    }

    fun resetForm() {
        _uiState.update {
            it.copy(
                country = "España",
                city = "",
                museums = false,
                restaurants = false,
                landmarks = false,
                parks = false,
                beaches = false,
                hotels = false,
                campings = false,
                pipican = false,
                walkingZones = false,
                vets = false,
                hospitals = false,
                dogResorts = false,
                groomers = false,
                petStores = false,
                isLoading = false,
                error = null
            )
        }
    }

    fun setLoading(v: Boolean) = _uiState.update { it.copy(isLoading = v) }
    fun setError(msg: String?) = _uiState.update { it.copy(error = msg) }

    fun logout() {
        firebaseAuth.signOut()
    }

    /** Construye la cadena de intereses seleccionados. */
    fun buildInterestsString(state: HomeUiState = _uiState.value): String {
        val list = buildList {
            if (state.museums)      add("museos")
            if (state.restaurants)  add("restaurantes admite mascotas")
            if (state.landmarks)    add("lugares de interes y monumentos")
            if (state.parks)        add("parques naturales")
            if (state.beaches)      add("playas perros")
            if (state.hotels)       add("hoteles admite mascotas")
            if (state.campings)     add("campings pet friendly")
            if (state.pipican)      add("pipican")
            if (state.walkingZones) add("zonas paseo")
            if (state.vets)         add("veterinarios")
            if (state.hospitals)    add("hospitales veterinarios 24h")
            if (state.dogResorts)   add("residencias caninas")
            if (state.groomers)     add("peluquerías caninas")
            if (state.petStores)    add("tiendas de piensos")
        }
        return list.joinToString(", ").ifBlank { "lugares pet friendly" }
    }
}
