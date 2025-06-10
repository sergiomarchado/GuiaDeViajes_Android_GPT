package com.example.guiadeviajes_android_gpt.profile.viewmodel
/**
 * ProfileViewModel.kt
 *
 * ViewModel que gestiona la carga y actualización del perfil de usuario en Firebase Realtime Database.
 * Expone estados de usuario, carga y errores mediante StateFlow para ser consumidos en Compose.
 */
import androidx.lifecycle.ViewModel
import com.example.guiadeviajes_android_gpt.profile.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ViewModel() {

    // Estado que almacena el perfil de usuario cargado desde la base de datos
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile

    // Estado para indicar si hay una operación en curso (carga o guardado)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado para errores de carga o guardado de perfil
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        // Al inicializar el ViewModel, cargar inmediatamente el perfil del usuario
        loadUserProfile()
    }

    /**
     * Carga el perfil de usuario único bajo /users/{uid} en Firebase Realtime Database.
     * Actualiza _userProfile o _errorMessage según el resultado.
     */
    private fun loadUserProfile() {
        val uid = firebaseAuth.currentUser?.uid ?: return // Si no hay usuario, no hacer nada
        _isLoading.value = true

        // Referencia al nodo de usuario en Firebase
        val userRef = database.getReference("users").child(uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // Obtener el objeto UserProfile desde los datos JSON
                val profile = snapshot.getValue(UserProfile::class.java)
                profile?.let {
                    _userProfile.value = it // Actualizar StateFlow con datos de perfil
                }
                _isLoading.value = false    // Finaliza estado de carga
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = error.message
                _isLoading.value = false
            }
        })
    }

    /**
     * Actualiza el perfil de usuario en Firebase con los datos proporcionados.
     * Realiza validaciones básicas de campos obligatorios antes de enviar.
     *
     * @param profile Objeto UserProfile con los nuevos datos del usuario.
     */
    fun updateUserProfile(profile: UserProfile) {
        // Validaciones de entrada: nombre y apellidos no pueden estar vacíos
        if (profile.firstName.isBlank()) {
            _errorMessage.value = "El nombre no puede estar vacío."
            return
        }
        if (profile.lastName.isBlank()) {
            _errorMessage.value = "Los apellidos no pueden estar vacíos."
            return
        }

        val uid = firebaseAuth.currentUser?.uid ?: return
        _isLoading.value = true

        // Referencia para escribir los datos actualizados
        val userRef = database.getReference("users").child(uid)
        userRef.setValue(profile)
            .addOnSuccessListener {
                _userProfile.value = profile   // Actualizar estado local tras éxito
                _isLoading.value = false
            }
            .addOnFailureListener {
                _errorMessage.value = it.message
                _isLoading.value = false
            }
    }
}
