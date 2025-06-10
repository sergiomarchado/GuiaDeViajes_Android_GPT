package com.example.guiadeviajes_android_gpt.home.data.remote.dto.response
/**
 * GooglePlacesResponse.kt
 *
 * DTO que representa una respuesta genérica de Google Places en otros endpoints.
 * Incluye lista de PlaceResult con información básica para mostrar.
 *
 * @property places Lista de resultados de búsqueda o null si no hay datos.
 */
data class GooglePlacesResponse(
    val places: List<PlaceResult>?
)
/**
 * PlaceResult.kt
 *
 * Modelo simplificado de un lugar retornado por ciertos endpoints de Google Places.
 *
 * @property id                Identificador único del lugar.
 * @property displayName       Nombre para mostrar (puede incluir datos de localización).
 * @property formattedAddress  Dirección formateada (puede ser null).
 * @property primaryType       Tipo principal de lugar (e.g., "restaurant").
 * @property rating            Valoración media (0.0-5.0) o null.
 * @property websiteUri        URL del sitio web (puede ser null).
 * @property photos            Lista de fotos con photo_reference para Place Photos.
 */
data class PlaceResult(
    val id: String?,
    val displayName: DisplayName?,
    val formattedAddress: String?,
    val primaryType: String?,
    val rating: Float?,
    val websiteUri: String?,
    val photos: List<Photo>?
)
/**
 * DisplayName.kt
 *
 * DTO que encapsula el nombre a mostrar de un lugar.
 *
 * @property text Texto principal del nombre a mostrar.
 */
data class DisplayName(
    val text: String?
)
/**
 * Photo.kt
 *
 * DTO que representa una referencia a una foto de un lugar.
 *
 * @property name PhotoReference que se usa para descargar la imagen.
 */
data class Photo(
    val name: String? // photo_reference para usar en Place Photos
)
