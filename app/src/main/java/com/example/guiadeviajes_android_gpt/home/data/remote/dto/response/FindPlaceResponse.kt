package com.example.guiadeviajes_android_gpt.home.data.remote.dto.response
/**
 * FindPlaceResponse.kt
 *
 * DTO para la respuesta del endpoint "Find Place from Text" de Google Places.
 * Contiene una lista de candidatos con el identificador unique de cada lugar.
 */
import com.squareup.moshi.JsonClass

/**
 * DTO para el endpoint Find Place from Text.
 */
@JsonClass(generateAdapter = true)
data class FindPlaceResponse(
    val candidates: List<Candidate>   // Lista de posibles coincidencias de lugares
) {
    /**
     * Candidate representa un candidato individual de búsqueda.
     *
     * @property place_id Identificador único de Google Place (puede ser null si no hay coincidencia).
     */
    @JsonClass(generateAdapter = true)
    data class Candidate(
        val place_id: String?
    )
}
