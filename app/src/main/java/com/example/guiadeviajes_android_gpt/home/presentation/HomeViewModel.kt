package com.example.guiadeviajes_android_gpt.home.presentation

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * HomeViewModel.kt
 *
 * ViewModel para HomeScreen, gestiona acciones generales de la pantalla principal,
 * como cerrar sesión del usuario.
 *
 * @constructor Inyecta FirebaseAuth para manejar logout.
 * @param firebaseAuth Instancia de FirebaseAuth para la autenticación.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    /**
     * Cierra la sesión de Firebase.
     * Navegación posterior gestionada por la UI (HomeScreen).
     */
    fun logout() {
        firebaseAuth.signOut()
    }

    /**
     * Metodo placeholder para futuros manejos de errores o limpieza de estados.
     * Actualmente no se utilizan errores en HomeScreen.
     */
    fun clearErrorMessage() {
        // No hay errores gestionados en HomeScreen actualmente.
    }
}