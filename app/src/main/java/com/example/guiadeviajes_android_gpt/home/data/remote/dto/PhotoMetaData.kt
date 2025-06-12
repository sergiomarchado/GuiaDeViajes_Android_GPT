package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.Json

data class PhotoMetadata(
    @Json(name = "photo_reference")
    val photo_reference: String    // debe coincidir con el JSON
)