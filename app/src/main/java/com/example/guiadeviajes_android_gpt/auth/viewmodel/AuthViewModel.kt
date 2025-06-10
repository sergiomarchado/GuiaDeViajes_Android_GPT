package com.example.guiadeviajes_android_gpt.auth.viewmodel
/**
 * AuthViewModel.kt
 *
 * ViewModel que gestiona la lógica de autenticación de usuarios.
 * Incluye registro, inicio de sesión, verificación de email, recuperación de contraseña y logout.
 * Utiliza FirebaseAuth y FirebaseDatabase inyectados por Hilt.
 */
import androidx.lifecycle.ViewModel
import com.example.guiadeviajes_android_gpt.profile.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * ViewModel anotado con Hilt que expone estados de carga y errores,
 * así como funciones de autenticación con Firebase.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {

    // Estado que indica si hay una operación en curso (para mostrar loading)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado para mensajes de error (null si no hay error)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /**
     * Resetea el mensaje de error a null.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Registra un nuevo usuario con email y contraseña,
     * crea su perfil en Realtime Database y envía un email de verificación.
     *
     * @param email Email del usuario.
     * @param password Contraseña.
     * @param firstName Nombre.
     * @param lastName Apellidos.
     * @param onVerificationEmailSent Callback en éxito al enviar email.
     * @param onError Callback en caso de error con mensaje.
     */
    fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        onVerificationEmailSent: () -> Unit,
        onError: (String) -> Unit
    ) {
        _isLoading.value = true  // Inicia loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Usuario creado correctamente: obtener UID para el perfil
                    val user = firebaseAuth.currentUser
                    val uid = user?.uid ?: return@addOnCompleteListener
                    val userRef = firebaseDatabase.getReference("users").child(uid)

                    // Construir objeto UserProfile y guardarlo en Realtime Database
                    val profile = UserProfile(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phone = "",
                        tokens = 0
                    )

                    userRef.setValue(profile)
                        .addOnSuccessListener {
                            // Enviar email de verificación
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
                    // Error al crear usuario
                    _isLoading.value = false
                    val error = task.exception?.message ?: "Error al registrar usuario"
                    _errorMessage.value = error
                    onError(error)
                }
            }
    }

    /**
     * Inicia sesión con email y contraseña.
     *
     * @param email Email del usuario.
     * @param password Contraseña.
     * @param onSuccess Callback en caso de éxito.
     */
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

    /**
     * Recarga el usuario actual y comprueba si el email está verificado.
     *
     * @param onResult Callback con true si verificado.
     */
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

    /**
     * Reenvía el correo de verificación al usuario actual.
     *
     * @param onSuccess Callback en caso de éxito.
     * @param onError Callback en caso de error con mensaje.
     */
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

    /**
     * Envía un email para restablecer la contraseña.
     *
     * @param email Email destinatario.
     * @param onSuccess Callback en caso de éxito.
     * @param onError Callback en caso de error con mensaje.
     */
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

    /**
     * Indica si hay un usuario actualmente autenticado.
     */
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
