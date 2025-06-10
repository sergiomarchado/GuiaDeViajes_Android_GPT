package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
/**
 * Format.kt
 *
 * DTO que representa el formato de un bloque de contenido en la respuesta de ChatGPT.
 * Define el tipo de formato aplicado, por ejemplo "plain_text" o "markdown".
 *
 * @property type Tipo de formato del contenido.
 */
@JsonClass(generateAdapter = true)
data class Format(
    @field:Json(name = "type")
    val type: String
)
