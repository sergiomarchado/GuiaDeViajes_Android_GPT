package com.example.guiadeviajes_android_gpt.map.util

import androidx.compose.ui.graphics.Color

fun getColorForCategory(category: String): Color {
    return when (category) {
        "Hospitales Vet." -> Color.Red
        "Veterinarios" -> Color(0xFFFF4081) // Rose
        "Museos", "Monumentos" -> Color(0xFF8A2BE2) // Violet
        "Parques", "Pipican / Zona Paseo", "Zonas Paseo" -> Color.Green
        "Playas" -> Color(0xFFFFA500) // Orange
        "Restaurantes" -> Color(0xFF00BFFF) // Azure
        "Hoteles", "Campings" -> Color.Blue
        "Peluquerías", "Tiendas de Piensos" -> Color.Yellow
        else -> Color(0xFF8A2BE2) // Violet por defecto
    }
}