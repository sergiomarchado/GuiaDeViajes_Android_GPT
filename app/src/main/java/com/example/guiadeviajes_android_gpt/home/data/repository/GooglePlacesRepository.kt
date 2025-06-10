package com.example.guiadeviajes_android_gpt.home.data.repository
/**
 * GooglePlacesRepository.kt
 *
 * Repositorio que encapsula la lógica de búsqueda de lugares usando la API de Google Places.
 * Proporciona métodos para Text Search, Find Place, Nearby Search y Geocoding,
 * devolviendo resultados simplificados en SimplePlaceResult.
 */
import android.util.Log
import com.example.guiadeviajes_android_gpt.BuildConfig
import com.example.guiadeviajes_android_gpt.home.data.remote.GooglePlacesApi
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.SimplePlaceResult
import javax.inject.Inject

class GooglePlacesRepository @Inject constructor(
    private val api: GooglePlacesApi
) {

    /**
     * Realiza una búsqueda de texto (Text Search) en Google Places.
     *
     * @param query Términos de búsqueda combinados (intereses, ciudad, país).
     * @param location Coordenadas para sesgar resultados (opcional).
     * @param radius Radio en metros para sesgar resultados (opcional).
     * @return Lista de resultados simplificados.
     */
    private suspend fun searchPlaces(
        query: String,
        location: String? = null,
        radius: Int? = null
    ): List<SimplePlaceResult> {
        return try {
            // Llamada a la API de Text Search
            val response = api.searchPlaces(
                query    = query,
                apiKey   = BuildConfig.API_KEYG,
                location = location,
                radius   = radius
            )
            if (response.isSuccessful) {
                val results = response.body()?.results.orEmpty()
                Log.d("PLACES_API", "✅ TextSearch lugares: ${results.size}")
                // Mapear cada resultado a SimplePlaceResult
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
                // Log de error de respuesta HTTP
                Log.e("PLACES_API", "❌ Error TextSearch: ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            // Captura de excepciones de red o parseo
            Log.e("PLACES_API", "❌ Excepción TextSearch: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Realiza una búsqueda de tipo Find Place From Text.
     * Obtiene candidatos y luego busca detalles de cada uno.
     *
     * @param query Términos de búsqueda.
     * @return Lista de resultados con detalles.
     */
    private suspend fun findPlaceByText(query: String): List<SimplePlaceResult> {
        return try {
            val resp = api.findPlaceFromText(
                input  = query,
                apiKey = BuildConfig.API_KEYG
            )
            if (resp.isSuccessful) {
                val candidates = resp.body()?.candidates.orEmpty()
                Log.d("PLACES_API", "🔍 FindPlace candidates: ${candidates.size}")
                // Mapear candidatos a detalles de lugar
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
     * Obtiene detalles de un lugar dado su placeId.
     *
     * @param placeId ID del lugar en Google Places.
     * @return Resultado simplificado o null en caso de error.
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
     * Realiza una búsqueda Nearby Search sesgada por tipo y palabra clave.
     *
     * @param lat Latitud de la ubicación.
     * @param lng Longitud de la ubicación.
     * @param radius Radio de búsqueda en metros.
     * @param type Tipo de lugar (e.g., "restaurant").
     * @param keyword Palabra clave para filtrar resultados (opcional).
     * @return Lista de resultados simplificados.
     */
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

    /**
     * Convierte una dirección en coordenadas lat/lng usando Geocoding API.
     *
     * @param address Dirección completa.
     * @return Par de coordenadas (lat, lng) o null si no se encontró.
     */
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

    /**
     * Búsqueda inteligente que combina varios métodos para obtener hasta maxResults lugares.
     *
     * 1) Find Place From Text
     * 2) Text Search
     * 3) Nearby Search con sesgo de ubicación según intereses
     * 4) Búsqueda de fallback lingüístico
     * 5) Desduplicar y limitar resultados
     *
     * @param interests Intereses seleccionados (% pet friendly).
     * @param city Nombre de la ciudad.
     * @param country Nombre del país.
     * @param maxResults Límite de resultados a devolver.
     * @return Lista de resultados únicos.
     */
    suspend fun smartSearch(
        interests: String,
        city: String,
        country: String,
        maxResults: Int = 10
    ): List<SimplePlaceResult> {
        val collected = mutableListOf<SimplePlaceResult>()
        val rawQuery = "$interests $city $country"

        // 1) Quick Find Place
        collected += findPlaceByText(rawQuery)

        // 2) Basic Text Search
        collected += searchPlaces(rawQuery)

        // 3) Sesgo de ubicación: si faltan resultados, geocodificar y hacer Nearby
        if (collected.size < maxResults) {
            geocodeAddress("$city, $country")?.let { (lat, lng) ->
                // Seleccionar Nearby según contenido de intereses
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
                // Añadir Text Search con ubicación y radio reducido
                collected += searchPlaces(rawQuery, location = "$lat,$lng", radius = 10_000)
            }
        }

        // 4) Fallback lingüístico si aún faltan resultados
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

        // 5) Desduplicar por placeId y limitar al tamaño máximo
        return collected
            .distinctBy { it.placeId }
            .take(maxResults)
    }
}
