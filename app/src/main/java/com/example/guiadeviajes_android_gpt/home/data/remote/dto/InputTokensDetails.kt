package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
/**
 * InputTokensDetails.kt
 *
 * DTO que representa detalles de los tokens de entrada usados en la petición a ChatGPT.
 * Incluye el número de tokens en caché para optimizar uso de tokens.
 *
 * @property cachedTokens Número de tokens reutilizados de caché en la petición.
 */
@JsonClass(generateAdapter = true)
data class InputTokensDetails(
    @field:Json(name = "cached_tokens")
    val cachedTokens: Int  // Tokens que no se recuentan porque provienen de caché
)
