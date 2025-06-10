package com.example.guiadeviajes_android_gpt.auth.presentation
/**
 * LoginScreen.kt
 *
 * Pantalla de acceso de usuario (login) con animaciones de borde en campos de email y contraseña.
 * Gestiona estados de UI, validación de credenciales y navegación al iniciar sesión.
 */
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.R
import com.example.guiadeviajes_android_gpt.auth.viewmodel.AuthViewModel
import com.example.guiadeviajes_android_gpt.auth.presentation.components.AnimatedLabelTextField

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // Estados locales para email y contraseña
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    // Observables del ViewModel: carga y mensajes de error
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Snackbar para mostrar errores
    val snackbarHostState = remember { SnackbarHostState() }

    // Estados de foco para animar los bordes de los TextFields
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }

    // Animación "idle breathing": borde pulsante en estado normal
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

    // Animación "focused breathing": borde pulsante más rápido en foco
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

    // Selección de color de borde según foco
    val emailBorderColor = if (emailFocused) focusedBorderColor else idleBorderColor
    val passwordBorderColor = if (passwordFocused) focusedBorderColor else idleBorderColor

    // Efecto para mostrar Snackbar al cambiar errorMessage
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearErrorMessage()
        }
    }

    // Estructura principal UI
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF011A30) // Fondo de la app
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

                // Logo de la app
                Image(
                    painter = painterResource(id = R.drawable.icono_fav),
                    contentDescription = "Logo de la app",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )

                // Títulos de bienvenida
                Text(
                    text = "Bienvenid@ a",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "PET EXPLORER",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Campo de email con animación y etiqueta flotante
                AnimatedLabelTextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = "Email",
                    isFocused = emailFocused,
                    onFocusChanged = { emailFocused = it },
                    borderColor = emailBorderColor,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de contraseña con transformación y acción Done
                AnimatedLabelTextField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    label = "Contraseña",
                    isFocused = passwordFocused,
                    onFocusChanged = { passwordFocused = it },
                    borderColor = passwordBorderColor,
                    imeAction = ImeAction.Done,
                    isPassword = true,
                    onDone = {
                        // Invoca login al pulsar Done
                        viewModel.loginUser(
                            emailState.value.trim(),
                            passwordState.value.trim(),
                            onSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de iniciar sesión
                Button(
                    onClick = {
                        viewModel.loginUser(
                            emailState.value.trim(),
                            passwordState.value.trim(),
                            onSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF011A30)
                    )
                ) {
                    Text("Iniciar sesión")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Enlaces a registro y recuperación de contraseña
                TextButton(onClick = { navController.navigate("register") }) {
                    Text("¿No tienes cuenta? Regístrate aquí", color = Color.White)
                }
                TextButton(onClick = { navController.navigate("forgot_password") }) {
                    Text("¿Olvidaste tu contraseña?", color = Color.White)
                }

                // Indicador de carga mientras procesa login
                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}
