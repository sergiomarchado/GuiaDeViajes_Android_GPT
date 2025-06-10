package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
/**
 * Usage.kt
 *
 * DTO que representa la información de uso de tokens en una respuesta de ChatGPT.
 * Incluye conteos de tokens de entrada y salida, así como detalles opcionales.
 *
 * @property inputTokens Número de tokens consumidos por el prompt (input).
 * @property inputTokensDetails Detalle de los tokens de entrada (si está disponible).
 * @property outputTokens Número de tokens generados en la respuesta (output).
 * @property outputTokensDetails Detalle de los tokens de salida (si está disponible).
 * @property totalTokens Suma total de tokens usados.
 */
@JsonClass(generateAdapter = true)
data class Usage(
    @field:Json(name = "input_tokens")
    val inputTokens: Int? = null,
    @field:Json(name = "input_tokens_details")
    val inputTokensDetails: InputTokensDetails? = null,
    @field:Json(name = "output_tokens")
    val outputTokens: Int? = null,
    @field:Json(name = "output_tokens_details")
    val outputTokensDetails: OutputTokensDetails? = null,
    @field:Json(name = "total_tokens")
    val totalTokens: Int? = null
)
