package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
/**
 * ChatiRequestDto.kt
 *
 * DTO que representa el cuerpo de la petición a la API de completions de ChatGPT.
 * Contiene el modelo a usar y la lista de mensajes que forman la conversación.
 *
 * @property model    Identificador del modelo de OpenAI (e.g., "gpt-3.5-turbo").
 * @property messages Lista de mensajes (sistema y usuario) que definen el contexto.
 */
@JsonClass(generateAdapter = true)
data class ChatiRequestDto(
    @field:Json(name = "model")
    val model: String,
    @field:Json(name = "messages")
    val messages: List<Message>
)
