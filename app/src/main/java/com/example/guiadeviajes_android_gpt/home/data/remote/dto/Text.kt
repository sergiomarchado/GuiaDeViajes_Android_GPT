package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
/**
 * Text.kt
 *
 * DTO que representa un bloque de texto con su formato en la respuesta de ChatGPT.
 *
 * @property format Tipo de formato aplicado al texto (e.g., plain_text, markdown).
 */
@JsonClass(generateAdapter = true)
data class Text(
    @field:Json(name = "format")
    val format: Format
)
