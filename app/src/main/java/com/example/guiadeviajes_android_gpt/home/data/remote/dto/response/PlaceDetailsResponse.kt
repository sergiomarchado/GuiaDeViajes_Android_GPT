package com.example.guiadeviajes_android_gpt.home.data.remote.dto

data class PlaceDetailsResponse(
    val result: PlaceDetailsResult
)

data class PlaceDetailsResult(
    val name: String,
    val formatted_address: String,
    val website: String?,
    val international_phone_number: String?,
    val rating: Double?
)
