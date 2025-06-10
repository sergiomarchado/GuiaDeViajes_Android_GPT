package com.example.guiadeviajes_android_gpt.home.data.remote.dto.response

import com.example.guiadeviajes_android_gpt.home.data.remote.dto.Message
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.Usage
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
/**
 * ChatiResponseDto.kt
 *
 * DTO que representa la respuesta de la API de ChatGPT para el endpoint de "chat/completions".
 * Contiene identificador, tipo de objeto, timestamp de creación, lista de elecciones y uso de tokens.
 *
 * @property id        Identificador único de la respuesta.
 * @property objectX   Tipo de objeto retornado (p.ej., "chat.completion").
 * @property created   Timestamp (en segundos) de creación de la respuesta.
 * @property choices   Lista de opciones generadas por el modelo (List<Choice>).
 * @property usage     Información de uso de tokens (Usage DTO) para la petición.
 */
@JsonClass(generateAdapter = true)
data class ChatiResponseDto(
    // ID de la respuesta de ChatGPT
    @field:Json(name = "id") val id: String,
    // Tipo de objeto (p.ej., "chat.completion")
    @field:Json(name = "object") val objectX: String,
    // Marca de tiempo UNIX en segundos cuando se generó
    @field:Json(name = "created") val created: Long,
    // Opciones (mensajes) devueltos por el modelo
    @field:Json(name = "choices") val choices: List<Choice>,
    // Detalles del uso de tokens para la petición
    @field:Json(name = "usage") val usage: Usage
)

/**
 * Choice.kt
 *
 * DTO que representa una opción individual en la respuesta de ChatGPT.
 *
 * @property index         Índice de la opción en la lista (0 para primera opción).
 * @property message       Mensaje generado (Message DTO) con role y contenido.
 * @property finishReason  Razón por la cual el modelo dejó de generar texto.
 */
@JsonClass(generateAdapter = true)
data class Choice(
    // Orden de la opción en la respuesta
    @field:Json(name = "index") val index: Int,
    // Mensaje resultante con role y content
    @field:Json(name = "message") val message: Message,
    // Código de razón de finalización (e.g., "stop")
    @field:Json(name = "finish_reason") val finishReason: String
)
