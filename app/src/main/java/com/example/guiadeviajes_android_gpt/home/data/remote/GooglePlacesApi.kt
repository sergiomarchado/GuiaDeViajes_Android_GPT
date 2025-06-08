package com.example.guiadeviajes_android_gpt.home.data.remote

import com.example.guiadeviajes_android_gpt.home.data.remote.dto.GeocodingResponse
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.PlacesSearchResponse
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.PlaceDetailsResponse
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.FindPlaceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApi {

    /**
     * Text Search con sesgo de ubicación opcional.
     * @param query La consulta de texto (Retrofit la codifica internamente).
     * @param apiKey API key de Google.
     * @param location Sesgo de ubicación en formato "lat,lng" (opcional).
     * @param radius Radio en metros para sesgar resultados cerca de location (opcional).
     */
    @GET("place/textsearch/json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("key") apiKey: String,
        @Query("location") location: String? = null,
        @Query("radius") radius: Int? = null
    ): Response<PlacesSearchResponse>

    /**
     * Place Details para obtener datos adicionales.
     */
    @GET("place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("key") apiKey: String,
        @Query("fields") fields: String = "name,formatted_address,website,international_phone_number,rating"
    ): Response<PlaceDetailsResponse>

    /**
     * Nearby Search genérico para cualquier tipo y keyword.
     */
    @GET("place/nearbysearch/json")
    suspend fun nearbySearch(
        @Query("location") location: String,
        @Query("radius") radius: Int? = 20000,
        @Query("type") type: String,
        @Query("keyword") keyword: String? = null,
        @Query("key") apiKey: String
    ): Response<PlacesSearchResponse>

    /**
     * Geocoding para convertir dirección en coordenadas.
     */
    @GET("geocode/json")
    suspend fun geocode(
        @Query("address") address: String,
        @Query("key") apiKey: String
    ): Response<GeocodingResponse>

    /**
     * Find Place from Text como fallback adicional.
     * Retorna sólo place_id que luego se pueden detallar.
     */
    @GET("place/findplacefromtext/json")
    suspend fun findPlaceFromText(
        @Query("input") input: String,
        @Query("inputtype") inputType: String = "textquery",
        @Query("fields") fields: String = "place_id",
        @Query("key") apiKey: String
    ): Response<FindPlaceResponse>
}