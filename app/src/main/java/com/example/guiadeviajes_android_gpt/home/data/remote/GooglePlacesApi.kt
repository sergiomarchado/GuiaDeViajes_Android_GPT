package com.example.guiadeviajes_android_gpt.home.data.remote
/**
 * GooglePlacesApi.kt
 *
 * Interfaz Retrofit para consumir la API de Google Places y Geocoding.
 * Proporciona endpoints para Text Search, Place Details, Nearby Search,
 * Geocoding y Find Place from Text.
 */
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.response.GeocodingResponse
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.response.PlacesSearchResponse
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.response.PlaceDetailsResponse
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.response.FindPlaceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApi {

    /**
     * Text Search en Google Places.
     *
     * @param query Términos de búsqueda, e.g., "pet friendly restaurants".
     * @param apiKey Clave de API de Google Places.
     * @param location Coordenadas opcionales "lat,lng" para sesgar resultados.
     * @param radius Radio opcional en metros para filtrar proximidad.
     * @return Response con PlacesSearchResponse que contiene lista de lugares.
     */
    @GET("place/textsearch/json")
    suspend fun searchPlaces(
        @Query("query")    query: String,
        @Query("key")      apiKey: String,
        @Query("location") location: String? = null,
        @Query("radius")   radius:   Int?    = null
    ): Response<PlacesSearchResponse>

    /**
     * Obtiene detalles de un lugar específico.
     *
     * @param placeId ID único del lugar.
     * @param apiKey  Clave de API de Google Places.
     * @param fields  Campos a incluir en la respuesta (por defecto: name, address, website, phone, rating).
     * @return Response con PlaceDetailsResponse que incluye información detallada.
     */
    @GET("place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("key")      apiKey:  String,
        @Query("fields")   fields:  String = "name,formatted_address,website,international_phone_number,rating"
    ): Response<PlaceDetailsResponse>

    /**
     * Búsqueda Nearby Search en Google Places.
     *
     * @param location Coordenadas "lat,lng" del centro de búsqueda.
     * @param radius   Radio en metros para la búsqueda.
     * @param type     Tipo de lugar (e.g., "restaurant", "park").
     * @param keyword  Palabra clave adicional para filtrar resultados.
     * @param apiKey   Clave de API de Google Places.
     * @return Response con PlacesSearchResponse que contiene lista de lugares.
     */
    @GET("place/nearbysearch/json")
    suspend fun nearbySearch(
        @Query("location") location: String,      // "lat,lng"
        @Query("radius")   radius:   Int?    = 20000,
        @Query("type")     type:     String,
        @Query("keyword")  keyword:  String? = null,
        @Query("key")      apiKey:   String
    ): Response<PlacesSearchResponse>

    /**
     * Geocoding: convierte una dirección en coordenadas.
     *
     * @param address Dirección completa para geocodificar.
     * @param apiKey   Clave de API de Google Maps Geocoding.
     * @return Response con GeocodingResponse que contiene resultados y coordenadas.
     */
    @GET("geocode/json")
    suspend fun geocode(
        @Query("address") address: String,
        @Query("key")     apiKey:  String
    ): Response<GeocodingResponse>

    /**
     * Find Place From Text: búsqueda de lugar por texto.
     *
     * @param input     Texto de búsqueda (input text query).
     * @param inputType Tipo de entrada (por defecto "textquery").
     * @param fields    Campos a incluir en la respuesta (por defecto "place_id").
     * @param apiKey    Clave de API de Google Places.
     * @return Response con FindPlaceResponse que contiene candidatos.
     */
    @GET("place/findplacefromtext/json")
    suspend fun findPlaceFromText(
        @Query("input")     input:     String,
        @Query("inputtype") inputType: String = "textquery",
        @Query("fields")    fields:    String = "place_id",
        @Query("key")       apiKey:    String
    ): Response<FindPlaceResponse>
}
