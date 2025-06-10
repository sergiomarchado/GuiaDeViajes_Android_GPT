package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
/**
 * Message.kt
 *
 * DTO que representa un mensaje en una conversación con ChatGPT.
 * Utilizado tanto para mensajes de sistema como de usuario.
 *
 * @property role    Rol del emisor del mensaje (por ejemplo, "system" o "user").
 * @property content Contenido textual del mensaje.
 */
@JsonClass(generateAdapter = true)
data class Message(
    @field:Json(name = "role")
    val role: String,
    @field:Json(name = "content")
    val content: String
)
