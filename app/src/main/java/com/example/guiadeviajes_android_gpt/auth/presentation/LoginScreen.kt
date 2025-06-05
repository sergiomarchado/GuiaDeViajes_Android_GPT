package com.example.guiadeviajes_android_gpt.auth.presentation

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
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // 🔹 Estados de foco
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }

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

    val emailBorderColor = if (emailFocused) focusedBorderColor else idleBorderColor
    val passwordBorderColor = if (passwordFocused) focusedBorderColor else idleBorderColor

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
                Image(
                    painter = painterResource(id = R.drawable.icono_fav),
                    contentDescription = "Logo de la app",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )

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

                TextButton(onClick = { navController.navigate("register") }) {
                    Text("¿No tienes cuenta? Regístrate aquí", color = Color.White)
                }
                TextButton(onClick = { navController.navigate("forgot_password") }) {
                    Text("¿Olvidaste tu contraseña?", color = Color.White)
                }

                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}
