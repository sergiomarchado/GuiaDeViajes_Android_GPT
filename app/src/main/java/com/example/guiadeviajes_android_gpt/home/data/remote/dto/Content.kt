package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
/**
 * Content.kt
 *
 * DTO que representa un bloque de contenido dentro de una respuesta de ChatGPT.
 * Incluye el texto generado, su tipo y posibles anotaciones para formateo adicional.
 *
 * @property annotations Lista de anotaciones de formato u otras marcas (puede estar vacía).
 * @property text        Texto generado por el modelo.
 * @property type        Tipo de contenido (e.g., "text").
 */
@JsonClass(generateAdapter = true)
data class Content(
    @field:Json(name = "annotations")
    val annotations: List<Any>,
    @field:Json(name = "text")
    val text: String,
    @field:Json(name = "type")
    val type: String
)
