package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Usage(
    @field:Json(name = "input_tokens")
    val inputTokens: Int? = null,
    @field:Json(name = "input_tokens_details")
    val inputTokensDetails: InputTokensDetails? = null,
    @field:Json(name = "output_tokens")
    val outputTokens: Int? = null,
    @field:Json(name = "output_tokens_details")
    val outputTokensDetails: OutputTokensDetails? = null,
    @field:Json(name = "total_tokens")
    val totalTokens: Int? = null
)
