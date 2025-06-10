package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
/**
 * Reasoning.kt
 *
 * DTO que representa información de razonamiento dentro de la respuesta de ChatGPT.
 * Incluye campos de resumen y esfuerzo para interpretar o depurar la respuesta.
 *
 * @property effort   Cualquier dato que describa el esfuerzo computacional (tipo genérico).
 * @property summary  Resumen o explicación del razonamiento del modelo.
 */
@JsonClass(generateAdapter = true)
data class Reasoning(
    @field:Json(name = "effort")
    val effort: Any,
    @field:Json(name = "summary")
    val summary: Any
)
