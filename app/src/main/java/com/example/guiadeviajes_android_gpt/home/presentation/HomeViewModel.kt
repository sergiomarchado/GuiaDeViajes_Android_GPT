package com.example.guiadeviajes_android_gpt.home.presentation

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel para HomeScreen, gestiona acciones de la barra de navegación lateral.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    /**
     * Cierra la sesión de Firebase y limpia cualquier dato de usuario en curso.
     */
    fun logout() {
        firebaseAuth.signOut()
    }

    /**
     * Placeholder para posibles futuras acciones (por ejemplo, fetch de perfil).
     */
    fun clearErrorMessage() {
        // No hay errores gestionados en HomeScreen actualmente.
    }
}