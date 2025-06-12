package com.example.guiadeviajes_android_gpt.home.data.remote.dto.response

import com.example.guiadeviajes_android_gpt.home.data.remote.dto.PhotoMetadata

/**
 * PlaceDetailsResponse.kt
 *
 * DTO que representa la respuesta de la API de Google Places para detalles de un lugar específico.
 * Contiene un objeto PlaceDetailsResult con la información detallada.
 *
 * @property result Objeto con los datos detallados del lugar.
 */
data class PlaceDetailsResponse(
    val result: PlaceDetailsResult
)
/**
 * PlaceDetailsResult.kt
 *
 * Modelo de datos con la información detallada de un lugar.
 *
 * @property name                        Nombre oficial del lugar.
 * @property formatted_address           Dirección completa y formateada.
 * @property website                     URL del sitio web del lugar (null si no disponible).
 * @property international_phone_number  Teléfono internacional en formato E.164 (null si no disponible).
 * @property rating                      Valoración promedio (0.0-5.0) del lugar (null si no disponible).
 */
data class PlaceDetailsResult(
    val name: String,
    val formatted_address: String,
    val website: String?,
    val international_phone_number: String?,
    val rating: Double?,
    val photos: List<PhotoMetadata>?
)
