// GeocodingResponse.kt
package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    val results: List<Result>,
    val status: String
) {
    @JsonClass(generateAdapter = true)
    data class Result(
        val geometry: Geometry
    ) {
        @JsonClass(generateAdapter = true)
        data class Geometry(
            val location: Location
        ) {
            @JsonClass(generateAdapter = true)
            data class Location(
                val lat: Double,
                val lng: Double
            )
        }
    }
}
