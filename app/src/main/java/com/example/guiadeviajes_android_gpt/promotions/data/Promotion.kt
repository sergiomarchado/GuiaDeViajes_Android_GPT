/**
 * Promotion.kt
 *
 * Modelo de datos que representa un patrocinador o promoción cargada desde Firebase Realtime Database.
 * Este objeto se corresponde con la estructura del nodo `/promotions/{promoId}`:
 *  - imageUrl: URL a la imagen (.png, .jpg, etc.)
 *  - name:     Nombre del patrocinador o título de la promoción
 *  - code:     Código de descuento asociado
 *  - terms:    Texto con condiciones o descripción
 */
package com.example.guiadeviajes_android_gpt.promotions.data

data class Promotion(
    val id: String = "",
    val imageUrl: String = "",
    val name: String = "",
    val code: String = "",
    val terms: String = ""
)