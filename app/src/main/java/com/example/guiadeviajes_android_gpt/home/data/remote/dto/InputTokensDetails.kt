package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InputTokensDetails(
    @field:Json(name = "cached_tokens")
    val cachedTokens: Int
)
