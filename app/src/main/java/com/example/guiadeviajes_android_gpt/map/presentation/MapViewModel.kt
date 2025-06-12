package com.example.guiadeviajes_android_gpt.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.SimplePlaceResult
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.response.PlaceSearchResult
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.response.PlacesSearchResponse
import com.example.guiadeviajes_android_gpt.home.data.repository.GooglePlacesRepository
import com.example.guiadeviajes_android_gpt.map.dto.PetInterest
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Representa un interés seleccionable por el usuario.
 */
data class Interest(
    val key: String,
    val label: String,
    val type: String,
    val keyword: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val placesRepo: GooglePlacesRepository
) : ViewModel() {

    // ── TopBar ───────────────────────────────────────────
    val userName: String = "Usuario Demo"
    val userTokens: Int = 42

    // Cámara Compose (se asigna desde MapScreen)
    lateinit var cameraState: CameraPositionState

    // ── Permiso de ubicación ─────────────────────────────
    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()
    fun onLocationPermissionGranted() {
        _hasLocationPermission.value = true
        fetchPlaces()
    }

    // ── Intereses ────────────────────────────────────────
    val allInterests = listOf(
        Interest("museums",     "Museos",             "museum"),
        Interest("restaurants", "Restaurantes",       "restaurant",   keyword = "pet"),
        Interest("landmarks",   "Monumentos",         "tourist_attraction"),
        Interest("parks",       "Parques",            "park"),
        Interest("beaches",     "Playas",             "beach",        keyword = "dog"),
        Interest("hotels",      "Hoteles",            "lodging",      keyword = "pet"),
        Interest("campings",    "Campings",           "campground",   keyword = "pet"),
        Interest("pipican",     "Pipican / Zona Paseo","park"),
        Interest("walking",     "Zonas Paseo",        "park"),
        Interest("vets",        "Veterinarios",       "veterinary_care"),
        Interest("hospitals",   "Hospitales Vet.",    "veterinary_care", keyword = "24h"),
        Interest("dogResorts",  "Residencias Caninas","pet_boarding"),
        Interest("groomers",    "Peluquerías",        "pet_grooming"),
        Interest("petStores",   "Tiendas de Piensos", "pet_store")
    )

    private val _selectedInterests = MutableStateFlow(allInterests.map { it.key }.toSet())
    val selectedInterests: StateFlow<Set<String>> = _selectedInterests.asStateFlow()
    fun toggleInterest(key: String) {
        _selectedInterests.update { curr ->
            curr.toMutableSet().also {
                if (!it.remove(key)) it += key
            }
        }
        fetchPlaces()
    }

    // ── Marcadores ────────────────────────────────────────
    private val _places = MutableStateFlow<List<PetInterest>>(emptyList())
    val places: StateFlow<List<PetInterest>> = _places.asStateFlow()

    /**
     * Hace Nearby Search para cada interés y mapea a PetInterest
     * incluyendo dirección, teléfono y web desde SimplePlaceResult.
     */
    private fun fetchPlaces(
        center: LatLng = LatLng(40.4168, -3.7038),
        radius: Int = 2000
    ) {
        viewModelScope.launch {
            val combined = selectedInterests.value.flatMap { key ->
                val interest = allInterests.first { it.key == key }
                val resp: PlacesSearchResponse = placesRepo.getNearbyRaw(
                    lat     = center.latitude,
                    lng     = center.longitude,
                    radius  = radius,
                    type    = interest.type,
                    keyword = interest.keyword
                )
                resp.results.mapNotNull { place: PlaceSearchResult ->
                    // mapeo de Nearby
                    val lat = place.geometry.location.lat
                    val lng = place.geometry.location.lng
                    // pedimos detalles simplificados (SimplePlaceResult) desde el repositorio
                    val detail: SimplePlaceResult? =
                        placesRepo.getPlaceDetails(place.place_id)
                    detail?.let {
                        PetInterest(
                            id          = place.place_id,
                            name        = place.name,
                            position    = LatLng(lat, lng),
                            category    = interest.label,
                            address     = it.address,
                            phoneNumber = it.phoneNumber,
                            website     = it.website
                        )
                    }
                }
            }
            _places.value = combined
        }
    }

    // ── Detalles seleccionados (para info nativa o futura UI) ───
    private val _selectedDetail = MutableStateFlow<PetInterest?>(null)
    val selectedDetail: StateFlow<PetInterest?> = _selectedDetail.asStateFlow()

    /**
     * Al pinchar un marcador simplemente guardamos el PetInterest
     * para usar su snippet (o más adelante un panel Compose).
     */
    fun selectPlace(place: PetInterest) {
        _selectedDetail.value = place
    }

    /**
     * Geocodifica texto a LatLng y recarga marcadores centrados ahí.
     */
    fun geocodeAndFetch(
        query: String,
        onCenter: (LatLng) -> Unit
    ) {
        viewModelScope.launch {
            placesRepo.geocodeAddress(query)?.let { (lat, lng) ->
                val newCenter = LatLng(lat, lng)
                onCenter(newCenter)
                fetchPlaces(center = newCenter)
            }
        }
    }
}
