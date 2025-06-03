package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Reasoning(
    @field:Json(name = "effort")
    val effort: Any,
    @field:Json(name = "summary")
    val summary: Any
)
