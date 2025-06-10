package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
/**
 * OutputTokensDetails.kt
 *
 * DTO que representa detalles de los tokens de salida generados por ChatGPT.
 * Contiene el número de tokens usados para el razonamiento interno del modelo.
 *
 * @property reasoningTokens Número de tokens consumidos durante el proceso de razonamiento.
 */
@JsonClass(generateAdapter = true)
data class OutputTokensDetails(
    @field:Json(name = "reasoning_tokens")
    val reasoningTokens: Int
)
