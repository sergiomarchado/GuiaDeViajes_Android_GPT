package com.example.guiadeviajes_android_gpt.home.data.remote.dto.response
/**
 * GeocodingResponse.kt
 *
 * DTO que representa la respuesta de la API de Geocoding de Google Maps.
 * Contiene una lista de resultados con la geometría y la ubicación (latitud y longitud),
 * además de un código de estado de la petición.
 */
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    val results: List<Result>,  // Lista de resultados con coordenadas
    val status: String          // Estado de la solicitud de geocoding
) {
    /**
     * Result contiene la geometría de un resultado particular.
     *
     * @property geometry Objeto Geometry con detalles de ubicación.
     */
    @JsonClass(generateAdapter = true)
    data class Result(
        val geometry: Geometry
    ) {
        /**
         * Geometry encapsula la ubicación en coordenadas lat/lng.
         *
         * @property location Objeto Location con latitud y longitud.
         */
        @JsonClass(generateAdapter = true)
        data class Geometry(
            val location: Location
        ) {
            /**
             * Location representa un punto geográfico.
             *
             * @property lat Latitud en grados decimales.
             * @property lng Longitud en grados decimales.
             */
            @JsonClass(generateAdapter = true)
            data class Location(
                val lat: Double,
                val lng: Double
            )
        }
    }
}
