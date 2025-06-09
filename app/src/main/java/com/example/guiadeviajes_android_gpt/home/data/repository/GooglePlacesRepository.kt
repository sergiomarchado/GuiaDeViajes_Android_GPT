package com.example.guiadeviajes_android_gpt.home.data.repository

import android.util.Log
import com.example.guiadeviajes_android_gpt.BuildConfig
import com.example.guiadeviajes_android_gpt.home.data.remote.GooglePlacesApi
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.SimplePlaceResult
import javax.inject.Inject

class GooglePlacesRepository @Inject constructor(
    private val api: GooglePlacesApi
) {

    private suspend fun searchPlaces(
        query: String,
        location: String? = null,
        radius: Int? = null
    ): List<SimplePlaceResult> {
        return try {
            val response = api.searchPlaces(
                query    = query,
                apiKey   = BuildConfig.API_KEYG,
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

    private suspend fun findPlaceByText(query: String): List<SimplePlaceResult> {
        return try {
            val resp = api.findPlaceFromText(
                input  = query,
                apiKey = BuildConfig.API_KEYG
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

    private suspend fun searchNearby(
        lat: Double,
        lng: Double,
        radius: Int = 20_000,
        type: String,
        keyword: String? = null
    ): List<SimplePlaceResult> {
        return try {
            val location = "$lat,$lng"
            val response = api.nearbySearch(
                location = location,
                radius   = radius,
                type     = type,
                keyword  = keyword,
                apiKey   = BuildConfig.API_KEYG
            )
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

    private suspend fun geocodeAddress(address: String): Pair<Double, Double>? {
        return try {
            val response = api.geocode(address, BuildConfig.API_KEYG)
            if (response.isSuccessful) {
                response.body()?.results
                    ?.firstOrNull()
                    ?.geometry
                    ?.location
                    ?.let { Pair(it.lat, it.lng) }
                    .also { if (it == null) Log.w("GEOCODING", "No coords para '$address'") }
            } else {
                Log.e("GEOCODING", "❌ Error Geocoding: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("GEOCODING", "❌ Excepción Geocoding: ${e.message}", e)
            null
        }
    }

    suspend fun smartSearch(
        interests: String,
        city: String,
        country: String,
        maxResults: Int = 10
    ): List<SimplePlaceResult> {
        val collected = mutableListOf<SimplePlaceResult>()
        val rawQuery = "$interests $city $country"

        // 1) Find Place rápido
        collected += findPlaceByText(rawQuery)

        // 2) Text Search básico
        collected += searchPlaces(rawQuery)

        // 3) Sesgo de ubicación + Nearby según interés
        if (collected.size < maxResults) {
            geocodeAddress("$city, $country")?.let { (lat, lng) ->
                when {
                    interests.contains("restaurantes", ignoreCase = true) ->
                        collected += searchNearby(lat, lng, type = "restaurant", keyword = "pet")
                    interests.contains("playas", ignoreCase = true) ->
                        collected += searchNearby(lat, lng, type = "beach", keyword = "dog")
                    interests.contains("parques", ignoreCase = true) ->
                        collected += searchNearby(lat, lng, type = "park", keyword = "dog")
                    interests.contains("hoteles", ignoreCase = true) ->
                        collected += searchNearby(lat, lng, type = "lodging", keyword = "pet")
                    interests.contains("campings", ignoreCase = true) ->
                        collected += searchNearby(lat, lng, type = "campground", keyword = "pet")
                    interests.contains("veterinarios", ignoreCase = true) ->
                        collected += searchNearby(lat, lng, type = "veterinary_care")
                    interests.contains("hospitales veterinarios", ignoreCase = true) ->
                        collected += searchNearby(lat, lng, type = "veterinary_care", keyword = "24h")
                    interests.contains("peluquerías", ignoreCase = true) ->
                        collected += searchNearby(lat, lng, type = "pet_store", keyword = "groomer")
                    interests.contains("residencias", ignoreCase = true) ->
                        collected += searchNearby(lat, lng, type = "pet_boarding")
                    interests.contains("tiendas de piensos", ignoreCase = true) ->
                        collected += searchNearby(lat, lng, type = "pet_store", keyword = "food")
                }
                // y bias en TextSearch
                collected += searchPlaces(rawQuery, location = "$lat,$lng", radius = 10_000)
            }
        }

        // 4) Último fallback lingüístico
        if (collected.size < maxResults) {
            val fallback = when {
                interests.contains("campings", ignoreCase = true) ->
                    "pet friendly campgrounds in $city $country"
                interests.contains("tiendas de piensos", ignoreCase = true) ->
                    "pet food stores in $city $country"
                else ->
                    "$interests pet friendly $city $country"
            }
            collected += searchPlaces(fallback)
        }

        // 5) Desduplicar y limitar
        return collected
            .distinctBy { it.placeId }
            .take(maxResults)
    }
}
