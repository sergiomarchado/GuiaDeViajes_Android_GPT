package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
