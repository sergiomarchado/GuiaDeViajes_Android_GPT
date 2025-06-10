package com.example.guiadeviajes_android_gpt.home.data.remote.dto

data class PlacesSearchResponse(
    val results: List<PlaceSearchResult>
)

data class PlaceSearchResult(
    val place_id: String,
    val name: String,
    val formatted_address: String,
    val rating: Double?,
    val geometry: Geometry
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)
