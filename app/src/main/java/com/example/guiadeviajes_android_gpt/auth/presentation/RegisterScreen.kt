package com.example.guiadeviajes_android_gpt.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // 🔹 Estados de los campos
    val firstNameState = remember { mutableStateOf("") }
    val lastNameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }

    // 🔹 Estado de carga y error
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 🔹 Muestra errores globales
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
                    text = "Registro",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 🔹 Nombre
                OutlinedTextField(
                    value = firstNameState.value,
                    onValueChange = { firstNameState.value = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 Apellidos
                OutlinedTextField(
                    value = lastNameState.value,
                    onValueChange = { lastNameState.value = it },
                    label = { Text("Apellidos") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 Email
                OutlinedTextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 Contraseña
                OutlinedTextField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 Confirmar contraseña
                OutlinedTextField(
                    value = confirmPasswordState.value,
                    onValueChange = { confirmPasswordState.value = it },
                    label = { Text("Confirmar Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 🔹 Botón de registro
                Button(
                    onClick = {
                        if (passwordState.value == confirmPasswordState.value) {
                            viewModel.registerUser(
                                email = emailState.value.trim(),
                                password = passwordState.value.trim(),
                                firstName = firstNameState.value.trim(),
                                lastName = lastNameState.value.trim(),
                                onVerificationEmailSent = {
                                    // Navega a la pantalla de verificación de email
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
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Las contraseñas no coinciden.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Registrarse")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 Botón para volver a Login
                TextButton(
                    onClick = { navController.navigate("login") }
                ) {
                    Text("¿Ya tienes cuenta? Inicia sesión")
                }

                // 🔹 Cargando...
                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
        }
    }
}
