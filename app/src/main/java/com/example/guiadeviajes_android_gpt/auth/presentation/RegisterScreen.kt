package com.example.guiadeviajes_android_gpt.auth.presentation
/**
 * RegisterScreen.kt
 *
 * Pantalla de registro de nuevos usuarios.
 * Incluye campos para nombre, apellidos, email, contraseña y confirmación de contraseña,
 * con animaciones de borde y validación básica de contraseñas.
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
import com.example.guiadeviajes_android_gpt.auth.viewmodel.AuthViewModel
import com.example.guiadeviajes_android_gpt.auth.presentation.components.AnimatedLabelTextField
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // Estados locales para cada campo de texto
    val firstNameState = remember { mutableStateOf("") }
    val lastNameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }

    // Estados de foco para animar bordes con "breathing"
    var firstNameFocused by remember { mutableStateOf(false) }
    var lastNameFocused by remember { mutableStateOf(false) }
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }
    var confirmPasswordFocused by remember { mutableStateOf(false) }

    // Observables del ViewModel: carga y errores
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Snackbar para mostrar mensajes de error o confirmación
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Transición infinita para borde "idle" (sin foco)
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

    // Transición infinita para borde "focused" (con foco)
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

    // Snackbar cuando errorMessage cambie
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearErrorMessage()
        }
    }

    // Estructura principal con Scaffold y fondo oscuro
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
                // Título de la pantalla de registro
                Text(
                    text = "Registro de Usuario",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Campo Nombre con etiqueta animada
                AnimatedLabelTextField(
                    value = firstNameState.value,
                    onValueChange = { firstNameState.value = it },
                    label = "Nombre",
                    isFocused = firstNameFocused,
                    onFocusChanged = { firstNameFocused = it },
                    borderColor = if (firstNameFocused) focusedBorderColor else idleBorderColor,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo Apellidos
                AnimatedLabelTextField(
                    value = lastNameState.value,
                    onValueChange = { lastNameState.value = it },
                    label = "Apellidos",
                    isFocused = lastNameFocused,
                    onFocusChanged = { lastNameFocused = it },
                    borderColor = if (lastNameFocused) focusedBorderColor else idleBorderColor,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(8.dp))


                // Campo Email
                AnimatedLabelTextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = "Email",
                    isFocused = emailFocused,
                    onFocusChanged = { emailFocused = it },
                    borderColor = if (emailFocused) focusedBorderColor else idleBorderColor,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo Contraseña
                AnimatedLabelTextField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    label = "Contraseña",
                    isFocused = passwordFocused,
                    onFocusChanged = { passwordFocused = it },
                    borderColor = if (passwordFocused) focusedBorderColor else idleBorderColor,
                    imeAction = ImeAction.Next,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo Confirmar Contraseña
                AnimatedLabelTextField(
                    value = confirmPasswordState.value,
                    onValueChange = { confirmPasswordState.value = it },
                    label = "Confirmar Contraseña",
                    isFocused = confirmPasswordFocused,
                    onFocusChanged = { confirmPasswordFocused = it },
                    borderColor = if (confirmPasswordFocused) focusedBorderColor else idleBorderColor,
                    imeAction = ImeAction.Done
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de registro con validación de contraseñas
                Button(
                    onClick = {
                        if (passwordState.value == confirmPasswordState.value) {
                            viewModel.registerUser(
                                email = emailState.value.trim(),
                                password = passwordState.value.trim(),
                                firstName = firstNameState.value.trim(),
                                lastName = lastNameState.value.trim(),
                                onVerificationEmailSent = {
                                    // Navegar a pantalla de verificación de email
                                    navController.navigate("email_verification") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onError = { errorMsg ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(errorMsg)
                                    }
                                }
                            )
                        } else {
                            // Mensaje si las contraseñas no coinciden
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Las contraseñas no coinciden.")
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
                    Text("Registrarse")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Enlace para retornar a login
                TextButton(onClick = { navController.navigate("login") }) {
                    Text("¿Ya tienes cuenta? Inicia sesión", color = Color.White)
                }

                // Indicador de progreso durante carga
                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}
