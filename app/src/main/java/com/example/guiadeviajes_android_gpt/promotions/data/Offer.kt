package com.example.guiadeviajes_android_gpt.promotions.data

/**
 * Representa una oferta individual de descuento de un patrocinador.
 *
 * @param id    Identificador de la oferta (clave en Firebase).
 * @param code  Código de descuento.
 * @param terms Texto breve de términos y condiciones.
 */
data class Offer(
    val id: String = "",
    val code: String = "",
    val title: String = "",
    val terms: String = "",
    val validUntil: String = ""
)
