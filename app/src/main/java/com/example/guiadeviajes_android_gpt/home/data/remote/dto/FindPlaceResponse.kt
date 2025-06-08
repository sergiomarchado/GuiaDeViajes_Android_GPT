package com.example.guiadeviajes_android_gpt.home.data.remote.dto

import com.squareup.moshi.JsonClass

/**
 * DTO para el endpoint Find Place from Text de Google Places API.
 * Mapea la respuesta con los place_id de los candidatos.
 */
@JsonClass(generateAdapter = true)
data class FindPlaceResponse(
    val candidates: List<Candidate>
) {
    @JsonClass(generateAdapter = true)
    data class Candidate(
        val place_id: String?
    )
}