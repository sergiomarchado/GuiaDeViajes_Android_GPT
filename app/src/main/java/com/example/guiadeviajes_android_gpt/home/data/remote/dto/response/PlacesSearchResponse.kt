package com.example.guiadeviajes_android_gpt.home.data.remote.dto.response
/**
 * PlacesSearchResponse.kt
 *
 * DTO que representa la respuesta completa de la búsqueda de lugares (Text Search)
 * de la API de Google Places.
 *
 * @property results Lista de resultados individuales de búsqueda.
 */
data class PlacesSearchResponse(
    val results: List<PlaceSearchResult>
)

/**
 * Representa un resultado individual de una Text Search.
 *
 * @param place_id            ID único de Google Places para este lugar.
 * @param name                Nombre oficial del lugar.
 * @param formatted_address   Dirección formateada para mostrar.
 * @param rating              Valoración promedio del lugar (0.0-5.0), puede ser null.
 * @param geometry            Contenedor de datos de ubicación (latitud y longitud).
 */
data class PlaceSearchResult(
    val place_id: String,
    val name: String,
    val formatted_address: String,
    val rating: Double?,
    val geometry: Geometry
)

/**
 * Contiene la geometría del lugar.
 *
 * @param location Objeto Location con latitud y longitud.
 */
data class Geometry(
    val location: Location
)

/**
 * Coordenadas geográficas de un punto en el mapa.
 *
 * @param lat Latitud en grados decimales.
 * @param lng Longitud en grados decimales.
 */
data class Location(
    val lat: Double,
    val lng: Double
)
