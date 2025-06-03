package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
    @field:Json(name = "role")
    val role: String,
    @field:Json(name = "content")
    val content: String
)
