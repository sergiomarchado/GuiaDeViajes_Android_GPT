package com.example.guiadeviajes_android_gpt.promotions.data

/**
 * Representa un patrocinador con múltiples ofertas.
 *
 * @param id       Identificador de la promoción (clave en Firebase).
 * @param name     Nombre del patrocinador.
 * @param imageUrl URL de su logo o imagen.
 * @param offers   Lista de ofertas disponibles.
 */
data class Promotion(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val offers: List<Offer> = emptyList()
)
