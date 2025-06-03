package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatiResponseDto(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "object") val objectX: String,
    @field:Json(name = "created") val created: Long,
    @field:Json(name = "choices") val choices: List<Choice>,
    @field:Json(name = "usage") val usage: Usage
)

@JsonClass(generateAdapter = true)
data class Choice(
    @field:Json(name = "index") val index: Int,
    @field:Json(name = "message") val message: Message,
    @field:Json(name = "finish_reason") val finishReason: String
)
