package com.example.guiadeviajes_android_gpt.home.data.remote.dto
/**
 * SavedResult.kt
 *
 * Modelo de datos que representa un resultado de búsqueda guardado por el usuario.
 * Incluye identificador único para la base de datos, parámetros de búsqueda y
 * el contenido generado en Markdown.
 *
 * @property id         Identificador único del resultado (por ejemplo, clave de Firebase).
 * @property city       Ciudad usada en la búsqueda.
 * @property country    País usado en la búsqueda.
 * @property interests  Cadena con los intereses seleccionados.
 * @property markdown   Contenido en Markdown de la respuesta generada.
 */
data class SavedResult(
    val id:         String = "",
    val city:       String = "",
    val country:    String = "",
    val interests:  String = "",
    val markdown:   String = ""
)