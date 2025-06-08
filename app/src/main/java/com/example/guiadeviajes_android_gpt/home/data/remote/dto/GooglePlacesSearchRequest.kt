package com.example.guiadeviajes_android_gpt.home.data.remote.dto
data class GooglePlacesSearchRequest(
    val textQuery: String,
    val maxResultCount: Int = 10,
    val includedTypes: List<String>? = null,
    val languageCode: String = "es",
    val regionCode: String? = "ES",            // 🌍 Región preferida
    val locationBias: String? = null,          // 📍 Prioridad a lugares cercanos
    val strictTypeFiltering: Boolean? = false  // 🎯 Filtro estricto por tipos
)
