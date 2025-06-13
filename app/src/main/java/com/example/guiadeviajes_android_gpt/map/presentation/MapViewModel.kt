package com.example.guiadeviajes_android_gpt.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.response.PlaceSearchResult
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.response.PlacesSearchResponse
import com.example.guiadeviajes_android_gpt.home.data.repository.GooglePlacesRepository
import com.example.guiadeviajes_android_gpt.map.core.database.CachedPlace
import com.example.guiadeviajes_android_gpt.map.core.database.CachedPlaceDao
import com.example.guiadeviajes_android_gpt.map.dto.PetInterest
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Interest(
    val key: String,
    val label: String,
    val type: String,
    val keyword: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val placesRepo: GooglePlacesRepository,
    private val cachedPlaceDao: CachedPlaceDao
) : ViewModel() {

    val userName: String = "Usuario Demo"
    val userTokens: Int = 42

    lateinit var cameraState: CameraPositionState

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()
    fun onLocationPermissionGranted() {
        _hasLocationPermission.value = true
    }

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

    private val _selectedInterests = MutableStateFlow(
        setOf("hospitals", "vets", "parks", "walking")
    )
    val selectedInterests: StateFlow<Set<String>> = _selectedInterests.asStateFlow()
    fun toggleInterest(key: String) {
        _selectedInterests.update { curr ->
            curr.toMutableSet().also {
                if (!it.remove(key)) it += key
            }
        }
    }

    private val _places = MutableStateFlow<List<PetInterest>>(emptyList())
    val places: StateFlow<List<PetInterest>> = _places.asStateFlow()

    private val _selectedDetail = MutableStateFlow<PetInterest?>(null)
    val selectedDetail: StateFlow<PetInterest?> = _selectedDetail.asStateFlow()

    fun selectPlace(place: PetInterest) {
        _selectedDetail.value = place
    }

    private val _searchCount = MutableStateFlow(0)
    val searchCount: StateFlow<Int> = _searchCount.asStateFlow()
    private val maxSearchesPerSession = 10

    fun geocodeAndFetch(query: String, onCenter: (LatLng) -> Unit) {
        viewModelScope.launch {
            if (_searchCount.value >= maxSearchesPerSession) return@launch
            placesRepo.geocodeAddress(query)?.let { (lat, lng) ->
                val newCenter = LatLng(lat, lng)
                onCenter(newCenter)
                _searchCount.update { it + 1 }
            }
        }
    }

    fun fetchPlaces(center: LatLng, radius: Int = 1500, maxCategories: Int = 4) {
        viewModelScope.launch {
            if (_searchCount.value >= maxSearchesPerSession) return@launch

            val selectedLimited = selectedInterests.value.take(maxCategories)

            val cachedResults = selectedLimited.flatMap { key ->
                cachedPlaceDao.getPlacesForInterest(key).map {
                    PetInterest(
                        id = it.placeId,
                        name = it.name,
                        position = LatLng(it.lat, it.lng),
                        category = it.category,
                        address = it.address,
                        phoneNumber = it.phoneNumber,
                        website = it.website
                    )
                }
            }

            if (cachedResults.isNotEmpty()) {
                _places.value = cachedResults
                return@launch
            }

            val combined = selectedLimited.flatMap { key ->
                val interest = allInterests.firstOrNull { it.key == key } ?: return@flatMap emptyList()
                val response: PlacesSearchResponse = placesRepo.getNearbyRaw(
                    lat = center.latitude,
                    lng = center.longitude,
                    radius = radius,
                    type = interest.type,
                    keyword = interest.keyword
                )

                // Limitar a 5 lugares por categoría
                response.results.take(5).map { place: PlaceSearchResult ->
                    val lat = place.geometry.location.lat
                    val lng = place.geometry.location.lng
                    val details = placesRepo.getPlaceDetails(place.place_id)

                    PetInterest(
                        id = place.place_id,
                        name = place.name,
                        position = LatLng(lat, lng),
                        category = interest.label,
                        address = details?.address ?: "Dirección desconocida",
                        phoneNumber = details?.phoneNumber,
                        website = details?.website,
                        photos = details?.photos ?: emptyList()
                    )
                }
            }

            _places.value = combined
            _searchCount.update { it + 1 }

            val toCache = combined.map {
                CachedPlace(
                    placeId = it.id,
                    interestKey = allInterests.first { i -> i.label == it.category }.key,
                    name = it.name,
                    lat = it.position.latitude,
                    lng = it.position.longitude,
                    category = it.category,
                    address = it.address,
                    phoneNumber = it.phoneNumber,
                    website = it.website
                )
            }
            cachedPlaceDao.insertAll(toCache)
        }
    }

    fun calculateVisibleRadius(center: LatLng, farCorner: LatLng): Int {
        return SphericalUtil.computeDistanceBetween(center, farCorner).toInt()
    }
}
