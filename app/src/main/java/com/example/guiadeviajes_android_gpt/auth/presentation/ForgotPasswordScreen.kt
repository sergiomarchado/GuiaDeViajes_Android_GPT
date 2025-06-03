package com.example.guiadeviajes_android_gpt.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
                    text = "Recuperar contraseña",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (emailState.value.trim().isNotEmpty()) {
                            viewModel.sendPasswordResetEmail(
                                email = emailState.value.trim(),
                                onSuccess = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Correo de recuperación enviado. Revisa tu bandeja de entrada.")
                                        navController.popBackStack() // Volver atrás tras éxito
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
                    enabled = !isLoading
                ) {
                    Text("Enviar correo de recuperación")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Text("Volver atrás")
                }

                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
        }
    }
}
