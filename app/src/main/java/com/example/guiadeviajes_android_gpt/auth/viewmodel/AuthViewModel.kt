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

    /**
     * 🔹 Registro de usuario con Firebase Auth + creación de perfil (con nombre y apellidos)
     */
    fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        onSuccess: () -> Unit
    ) {
        _isLoading.value = true
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = firebaseAuth.currentUser?.uid ?: return@addOnCompleteListener
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
                            _isLoading.value = false
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            _isLoading.value = false
                            _errorMessage.value = e.message
                        }
                } else {
                    _isLoading.value = false
                    _errorMessage.value = task.exception?.message ?: "Error al registrar usuario"
                }
            }
    }

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

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
