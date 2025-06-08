package com.example.guiadeviajes_android_gpt.home.data.remote.dto

/**
 * Representa un lugar simplificado para mostrar en la app.
 */
data class SimplePlaceResult(
    val placeId: String?,
    val name: String,
    val address: String?,
    val website: String?,
    val phoneNumber: String?,
    val rating: Double?
)