package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OutputTokensDetails(
    @field:Json(name = "reasoning_tokens")
    val reasoningTokens: Int
)
