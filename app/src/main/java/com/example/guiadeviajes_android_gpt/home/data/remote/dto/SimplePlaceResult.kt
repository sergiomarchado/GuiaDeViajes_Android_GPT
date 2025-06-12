package com.example.guiadeviajes_android_gpt.home.data.remote.dto

/**
 * SimplePlaceResult.kt
 *
 * Modelo de datos simplificado que representa un lugar obtenido de Google Places.
 * Se utiliza para mostrar la información esencial en la UI y para envío a ChatGPT.
 *
 * @property placeId     ID único del lugar en Google Places.
 * @property name        Nombre del lugar.
 * @property address     Dirección formateada (puede ser null si no está disponible).
 * @property website     URL del sitio web (puede ser null si no está disponible).
 * @property phoneNumber Número de teléfono internacional (puede ser null si no existe).
 * @property rating      Valoración del lugar (puede ser null si no está disponible).
 */
data class SimplePlaceResult(
    val placeId: String?,
    val name: String,
    val address: String?,
    val website: String?,
    val phoneNumber: String?,
    val rating: Double?,
    val photos:      List<String> = emptyList()  // valor por defecto
)