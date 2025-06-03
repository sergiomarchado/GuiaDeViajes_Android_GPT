package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Content(
    @field:Json(name = "annotations")
    val annotations: List<Any>,
    @field:Json(name = "text")
    val text: String,
    @field:Json(name = "type")
    val type: String
)
