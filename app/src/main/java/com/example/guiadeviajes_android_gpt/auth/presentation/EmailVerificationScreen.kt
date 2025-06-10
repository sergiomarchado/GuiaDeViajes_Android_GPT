package com.example.guiadeviajes_android_gpt.auth.presentation
/**
 * EmailVerificationScreen.kt
 *
 * Pantalla de verificación de correo electrónico.
 * Permite al usuario verificar su email mediante un botón para refrescar estado y otro para reenviar el correo.
 * Muestra además estados de carga y mensajes de error en un Snackbar.
 */
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun EmailVerificationScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // Observables del ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Efecto que muestra un Snackbar cuando cambia errorMessage
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearErrorMessage()
        }
    }

    // Estructura principal de UI
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF011A30) // Fondo oscuro
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
                    text = "Verificación de correo",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Texto informativo
                Text(
                    text = "Hemos enviado un correo de verificación. Por favor, revisa tu bandeja de entrada y haz clic en el enlace de verificación.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para comprobar estado de verificación
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.reloadAndCheckEmailVerification { isVerified ->
                                if (isVerified) {
                                    // Navega a Home y limpia backStack de verificación
                                    navController.navigate("home") {
                                        popUpTo("email_verification") { inclusive = true }
                                    }
                                } else {
                                    viewModel.showError("El correo aún no está verificado. Revisa tu bandeja de entrada.")
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF011A30)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("He verificado mi correo")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón para reenviar correo de verificación
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
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF011A30)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reenviar correo")
                }

                // Indicador de carga centrado debajo de botones
                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}
