package com.example.guiadeviajes_android_gpt.profile.data
/**
 * UserProfile.kt
 *
 * Modelo de datos que representa el perfil de usuario almacenado en Firebase Realtime Database.
 * Incluye información personal básica y tokens disponibles en la aplicación.
 *
 * @property firstName Nombre de pila del usuario.
 * @property lastName  Apellidos del usuario.
 * @property email     Correo electrónico registrado.
 * @property phone     Número de teléfono de contacto.
 * @property tokens    Cantidad de tokens disponibles para realizar búsquedas.
 */
data class UserProfile(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val tokens: Int = 0
)
