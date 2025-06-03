package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatiRequestDto(
    @field:Json(name = "model")
    val model: String,
    @field:Json(name = "messages")
    val messages: List<Message>
)
