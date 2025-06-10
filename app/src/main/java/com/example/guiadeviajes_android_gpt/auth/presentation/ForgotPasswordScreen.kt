package com.example.guiadeviajes_android_gpt.auth.presentation
/**
 * ForgotPasswordScreen.kt
 *
 * Pantalla para recuperar la contraseña del usuario mediante el envío de un correo de restablecimiento.
 * Incluye animaciones de borde para el campo de correo y gestión de estados de carga y errores.
 */
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.auth.presentation.components.AnimatedLabelTextField
import com.example.guiadeviajes_android_gpt.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // Estado local del texto de email
    val emailState = remember { mutableStateOf("") }
    // Observables del ViewModel: estado de carga y posibles errores
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Estado de foco para animar el borde del campo
    var emailFocused by remember { mutableStateOf(false) }

    // Snackbar para mostrar mensajes al usuario
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Animación "idle breathing" (borde pulsante) cuando no tiene foco
    val idleTransition = rememberInfiniteTransition()
    val idleAlpha by idleTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val idleBorderColor = Color(0xFFB0BEC5).copy(alpha = idleAlpha)

    // Animación "focused breathing" cuando el campo está enfocado
    val focusedTransition = rememberInfiniteTransition()
    val focusedAlpha by focusedTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val focusedBorderColor = Color.Cyan.copy(alpha = focusedAlpha)

    // Selección de color de borde según estado de foco
    val borderColor = if (emailFocused) focusedBorderColor else idleBorderColor

    // Mostrar errores en Snackbar cuando cambie errorMessage
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearErrorMessage()
        }
    }

    // Estructura principal UI
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF011A30)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título de la pantalla
                Text(
                    text = "Recuperar contraseña",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Campo de email con etiqueta animada y borde dinámico
                AnimatedLabelTextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = "Email",
                    isFocused = emailFocused,
                    onFocusChanged = { emailFocused = it },
                    borderColor = borderColor,
                    imeAction = ImeAction.Done
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para enviar el correo de recuperación
                Button(
                    onClick = {
                        if (emailState.value.trim().isNotEmpty()) {
                            viewModel.sendPasswordResetEmail(
                                email = emailState.value.trim(),
                                onSuccess = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Correo de recuperación enviado. Revisa tu bandeja de entrada.")
                                        navController.popBackStack()
                                    }
                                },
                                onError = { error ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(error)
                                    }
                                }
                            )
                        } else {
                            // Mensaje si el email está vacío
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Por favor, introduce un email.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF011A30)
                    )
                ) {
                    Text("Enviar correo de recuperación")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón de volver atrás
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Volver atrás", color = Color.White)
                }

                // Indicador de carga mientras se realiza la petición
                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}
