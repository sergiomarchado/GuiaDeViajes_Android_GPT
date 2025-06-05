package com.example.guiadeviajes_android_gpt.auth.presentation

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
    val emailState = remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var emailFocused by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 🔹 Animación infinita para borde "idle breathing"
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

    // 🔹 Animación infinita para borde "focused breathing"
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

    val borderColor = if (emailFocused) focusedBorderColor else idleBorderColor

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearErrorMessage()
        }
    }

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
                Text(
                    text = "Recuperar contraseña",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 🔹 Campo de email con animación y label flotante
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

                // 🔹 Botón de enviar correo
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

                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Volver atrás", color = Color.White)
                }

                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}
