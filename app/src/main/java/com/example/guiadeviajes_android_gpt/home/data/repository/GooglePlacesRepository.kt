package com.example.guiadeviajes_android_gpt.home.data.repository

import android.util.Log
import com.example.guiadeviajes_android_gpt.BuildConfig
import com.example.guiadeviajes_android_gpt.home.data.remote.GooglePlacesApi
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.SimplePlaceResult
import javax.inject.Inject

class GooglePlacesRepository @Inject constructor(
    private val api: GooglePlacesApi
) {
    /**
     * Text Search con sesgo opcional de ubicación.
     */
    suspend fun searchPlaces(
        query: String,
        location: String? = null,
        radius: Int? = null
    ): List<SimplePlaceResult> {
        return try {
            val response = api.searchPlaces(
                query   = query,
                apiKey  = BuildConfig.API_KEYG,
                location = location,
                radius   = radius
            )
            if (response.isSuccessful) {
                val results = response.body()?.results.orEmpty()
                Log.d("PLACES_API", "✅ TextSearch lugares: ${results.size}")
                results.map { place ->
                    SimplePlaceResult(
                        placeId     = place.place_id,
                        name        = place.name,
                        address     = place.formatted_address,
                        website     = null,
                        phoneNumber = null,
                        rating      = place.rating
                    )
                }
            } else {
                Log.e("PLACES_API", "❌ Error TextSearch: ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("PLACES_API", "❌ Excepción TextSearch: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Find Place from Text como fallback: obtenemos place_id y lo detallamos.
     */
    private suspend fun findPlaceByText(query: String): List<SimplePlaceResult> {
        return try {
            val resp = api.findPlaceFromText(
                input     = query,
                apiKey    = BuildConfig.API_KEYG
            )
            if (resp.isSuccessful) {
                val candidates = resp.body()?.candidates.orEmpty()
                Log.d("PLACES_API", "🔍 FindPlace candidates: ${candidates.size}")
                candidates.mapNotNull { cand ->
                    cand.place_id?.let { id ->
                        getPlaceDetails(id)
                    }
                }
            } else {
                Log.e("PLACES_API", "❌ Error FindPlace: ${resp.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("PLACES_API", "❌ Excepción FindPlace: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Detalla un placeId.
     */
    suspend fun getPlaceDetails(placeId: String): SimplePlaceResult? {
        return try {
            val response = api.getPlaceDetails(
                placeId = placeId,
                apiKey  = BuildConfig.API_KEYG
            )
            if (response.isSuccessful) {
                response.body()?.result?.let {
                    SimplePlaceResult(
                        placeId     = placeId,
                        name        = it.name,
                        address     = it.formatted_address,
                        website     = it.website,
                        phoneNumber = it.international_phone_number,
                        rating      = it.rating
                    )
                }
            } else {
                Log.e("PLACES_API", "❌ Error Details: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("PLACES_API", "❌ Excepción Details: ${e.message}", e)
            null
        }
    }

    /**
     * Nearby Search genérico.
     */
    suspend fun searchNearby(
        lat: Double,
        lng: Double,
        radius: Int = 20000,
        type: String,
        keyword: String? = null,
        rankByDistance: Boolean = false
    ): List<SimplePlaceResult> {
        return try {
            val location = "$lat,$lng"
            val response = if (rankByDistance) {
                api.nearbySearch(
                    location = location,
                    radius = null,
                    type = type,
                    keyword = keyword,
                    apiKey = BuildConfig.API_KEYG,
                    // note: Retrofit will omit null radius, but Google requires rankby=distance
                    // we could add another param rankby if needed
                )
            } else {
                api.nearbySearch(
                    location = location,
                    radius = radius,
                    type = type,
                    keyword = keyword,
                    apiKey = BuildConfig.API_KEYG
                )
            }
            if (response.isSuccessful) {
                val results = response.body()?.results.orEmpty()
                Log.d("PLACES_API", "✅ Nearby($type) found: ${results.size}")
                results.map { place ->
                    SimplePlaceResult(
                        placeId     = place.place_id,
                        name        = place.name,
                        address     = place.formatted_address,
                        website     = null,
                        phoneNumber = null,
                        rating      = place.rating
                    )
                }
            } else {
                Log.e("PLACES_API", "❌ Error Nearby($type): ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("PLACES_API", "❌ Excepción Nearby($type): ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Geocoding.
     */
    suspend fun geocodeAddress(address: String): Pair<Double, Double>? {
        return try {
            val response = api.geocode(address, BuildConfig.API_KEYG)
            if (response.isSuccessful) {
                response.body()?.results
                    ?.firstOrNull()
                    ?.geometry
                    ?.location
                    ?.let { Pair(it.lat, it.lng) }
                    .also {
                        if (it == null) Log.w("GEOCODING", "No coords para '$address'")
                    }
            } else {
                Log.e("GEOCODING", "❌ Error Geocoding: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("GEOCODING", "❌ Excepción Geocoding: ${e.message}", e)
            null
        }
    }

    /**
     * Flujo completo de búsqueda, integrando fallback con FindPlace y Nearby.
     */
    suspend fun smartSearch(
        interests: String,
        city: String,
        country: String
    ): List<SimplePlaceResult> {
        // 1) Intentar Find Place (fallback ligero)
        val rawQuery = "$interests $city $country"
        var results = findPlaceByText(rawQuery)
        if (results.isNotEmpty()) return results

        // 2) Text Search básico sin codificar, con bias si tenemos coords
        results = searchPlaces(rawQuery)
        if (results.isNotEmpty()) return results

        // 3) Si sigue vacío, sesgo de ubicación
        geocodeAddress("$city, $country")?.let { (lat, lng) ->
            // Nearby según tipo
            when {
                interests.contains("restaurantes", ignoreCase = true) -> {
                    results = searchNearby(lat, lng, type = "restaurant", keyword = "pet")
                }
                interests.contains("playas", ignoreCase = true) -> {
                    results = searchNearby(lat, lng, type = "beach", keyword = "dog")
                }
                interests.contains("parques", ignoreCase = true) -> {
                    results = searchNearby(lat, lng, type = "park", keyword = "dog")
                }
                // otros casos…
            }
            // bias en TextSearch
            if (results.isEmpty()) {
                results = searchPlaces(rawQuery, location = "$lat,$lng", radius = 10000)
            }
        }
        // 4) Último fallback: queries lingüísticas
        if (results.isEmpty()) {
            val fallback = when {
                interests.contains("restaurantes", ignoreCase = true) -> "pet friendly restaurants in $city $country"
                else -> "$interests pet friendly $city $country"
            }
            results = searchPlaces(fallback)
        }
        return results
    }
}
