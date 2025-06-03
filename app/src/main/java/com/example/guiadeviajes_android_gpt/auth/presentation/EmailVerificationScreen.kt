package com.example.guiadeviajes_android_gpt.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun EmailVerificationScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Mostrar errores en snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    text = "Verificación de correo",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Hemos enviado un correo de verificación. Por favor, revisa tu bandeja de entrada y haz clic en el enlace de verificación.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.reloadAndCheckEmailVerification { isVerified ->
                                if (isVerified) {
                                    // Si el email está verificado, navega a home
                                    navController.navigate("home") {
                                        popUpTo("email_verification") { inclusive = true }
                                    }
                                } else {
                                    viewModel.showError("El correo aún no está verificado. Revisa tu bandeja de entrada.")
                                }
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text("He verificado mi correo")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.resendVerificationEmail(
                            onSuccess = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Correo de verificación reenviado.")
                                }
                            },
                            onError = { error ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(error)
                                }
                            }
                        )
                    },
                    enabled = !isLoading
                ) {
                    Text("Reenviar correo")
                }

                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
        }
    }
}
