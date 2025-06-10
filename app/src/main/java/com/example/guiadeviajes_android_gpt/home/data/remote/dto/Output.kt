package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
/**
 * Output.kt
 *
 * DTO que representa una sección de salida en la respuesta de ChatGPT.
 * Contiene el contenido generado, identificadores y metadatos de estado.
 *
 * @property content Lista de bloques de contenido (textos) generados.
 * @property id      Identificador único del bloque de salida.
 * @property role    Rol del mensaje (por ejemplo, "assistant").
 * @property status  Estado del procesamiento del bloque (e.g., "success").
 * @property type    Tipo de contenido (e.g., "text").
 */
@JsonClass(generateAdapter = true)
data class Output(
    @field:Json(name = "content")
    val content: List<Content>,
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "role")
    val role: String,
    @field:Json(name = "status")
    val status: String,
    @field:Json(name = "type")
    val type: String
)
