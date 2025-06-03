package com.example.guiadeviajes_android_gpt.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.example.guiadeviajes_android_gpt.profile.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // 🔹 Registro de usuario con perfil + email de verificación
    fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        onVerificationEmailSent: () -> Unit,
        onError: (String) -> Unit
    ) {
        _isLoading.value = true
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val uid = user?.uid ?: return@addOnCompleteListener
                    val userRef = firebaseDatabase.getReference("users").child(uid)

                    val profile = UserProfile(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phone = "",
                        tokens = 0
                    )

                    userRef.setValue(profile)
                        .addOnSuccessListener {
                            user.sendEmailVerification()
                                .addOnSuccessListener {
                                    _isLoading.value = false
                                    onVerificationEmailSent()
                                }
                                .addOnFailureListener { e ->
                                    _isLoading.value = false
                                    _errorMessage.value = e.message
                                    onError(e.message ?: "Error al enviar correo de verificación.")
                                }
                        }
                        .addOnFailureListener { e ->
                            _isLoading.value = false
                            _errorMessage.value = e.message
                            onError(e.message ?: "Error al guardar perfil.")
                        }
                } else {
                    _isLoading.value = false
                    val error = task.exception?.message ?: "Error al registrar usuario"
                    _errorMessage.value = error
                    onError(error)
                }
            }
    }

    // 🔹 Login de usuario
    fun loginUser(email: String, password: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    _errorMessage.value = task.exception?.message ?: "Error al iniciar sesión"
                }
            }
    }

    // 🔹 Verifica si el email ya está verificado tras recargar el usuario
    fun reloadAndCheckEmailVerification(onResult: (Boolean) -> Unit) {
        _isLoading.value = true
        firebaseAuth.currentUser?.reload()
            ?.addOnSuccessListener {
                _isLoading.value = false
                onResult(firebaseAuth.currentUser?.isEmailVerified == true)
            }
            ?.addOnFailureListener { e ->
                _isLoading.value = false
                _errorMessage.value = e.message
                onResult(false)
            }
    }

    // 🔹 Reenvía el correo de verificación
    fun resendVerificationEmail(onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        firebaseAuth.currentUser?.sendEmailVerification()
            ?.addOnSuccessListener {
                _isLoading.value = false
                onSuccess()
            }
            ?.addOnFailureListener { e ->
                _isLoading.value = false
                _errorMessage.value = e.message
                onError(e.message ?: "Error al reenviar el correo.")
            }
    }

    // 🔹 Establece un mensaje de error manualmente
    fun showError(message: String) {
        _errorMessage.value = message
    }

    // 🔹 Recuperación de contraseña: envía un correo de restablecimiento
    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _isLoading.value = false
                onSuccess()
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _errorMessage.value = e.message
                onError(e.message ?: "Error al enviar el correo de restablecimiento.")
            }
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
