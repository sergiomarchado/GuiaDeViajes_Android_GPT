package com.example.guiadeviajes_android_gpt.home.data.remote.dto

data class GooglePlacesResponse(
    val places: List<PlaceResult>?
)

data class PlaceResult(
    val id: String?,
    val displayName: DisplayName?,
    val formattedAddress: String?,
    val primaryType: String?,
    val rating: Float?,
    val websiteUri: String?,
    val photos: List<Photo>?
)

data class DisplayName(
    val text: String?
)

data class Photo(
    val name: String? // photo_reference para usar en Place Photos
)
