package com.example.guiadeviajes_android_gpt.results

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.home.presentation.components.MarkdownWebView
/**
 * ResultScreen.kt
 *
 * Pantalla que muestra los resultados de la búsqueda formateados en Markdown.
 * Se encarga de lanzar la búsqueda al entrar, mostrar loading, errores,
 * renderizar el contenido con MarkdownWebView y permitir guardar resultados.
 *
 * @param navController Controlador de navegación para volver atrás.
 * @param city          Ciudad consultada.
 * @param country       País consultado.
 * @param interests     Cadena con los intereses usados en la búsqueda.
 * @param viewModel     ViewModel que orquesta la búsqueda, formateo y guardado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavController,
    city: String,
    country: String,
    interests: String,
    viewModel: ResultViewModel = hiltViewModel()
) {
    // Contexto para mostrar Toasts
    val context          = LocalContext.current

    // Observables del ViewModel
    val isLoading        by viewModel.isLoading.collectAsState()
    val markdownText     by viewModel.markdownResults.collectAsState()
    val errorMessage     by viewModel.errorMessage.collectAsState()
    val saveEvents       = viewModel.saveStatus
    val snackbarHostState= remember { SnackbarHostState() }

    // Lanzar búsqueda cuando cambien los parámetros de búsqueda
    LaunchedEffect(city, country, interests) {
        Log.d("RESULT_SCREEN", "🔍 Búsqueda para ciudad=$city, país=$country, intereses=$interests")
        viewModel.searchPlacesAndFormatMarkdown(interests, city, country)
    }

    // Mostrar errores en Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearErrorMessage()
        }
    }

    // Procesar eventos de guardado: éxito, error o no autenticado
    LaunchedEffect(saveEvents) {
        saveEvents.collect { event ->
            when (event) {
                is ResultViewModel.SaveEvent.Success -> {
                    Log.d("RESULT_SCREEN", "✅ Guardado OK, id=${event.id}")
                    Toast.makeText(context, "Guardado ✅", Toast.LENGTH_SHORT).show()
                }
                is ResultViewModel.SaveEvent.Error -> {
                    Log.e("RESULT_SCREEN", "❌ Error guardando: ${event.message}")
                    Toast.makeText(context, "Error al guardar ❌ ${event.message}", Toast.LENGTH_SHORT).show()
                }
                ResultViewModel.SaveEvent.NotLoggedIn -> {
                    Log.w("RESULT_SCREEN", "⚠️ Usuario no autenticado")
                    Toast.makeText(context, "Debes iniciar sesión para guardar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Estructura de UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Volver", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        snackbarHost   = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier           = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Encabezado con título y advertencia
            Text(
                text       = "Resultados en $city, $country",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onBackground,
                modifier   = Modifier.padding(bottom = 8.dp)
            )
            // Mensaje de advertencia
            Text(
                text      = "⚠️ Recuerda siempre contrastar la admisión de mascotas y sus servicios con el propio lugar.",
                fontSize  = 12.sp,
                color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier  = Modifier.padding(bottom = 16.dp)
            )

            when {
                // Mostrar indicador de carga
                isLoading -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text      = "Estamos trabajando en ofrecerte el mejor resultado…\nUn momento, por favor.",
                                fontSize  = 16.sp,
                                color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier  = Modifier.padding(bottom = 12.dp)
                            )
                            CircularProgressIndicator()
                        }
                    }
                }

                // Mostrar Markdown cuando esté disponible
                markdownText.isNotBlank() -> {
                    Box(modifier = Modifier.weight(1f)) {
                        MarkdownWebView(markdown = markdownText)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    //Botón para guardar resultados en RDB
                    Button(
                        onClick = {
                            viewModel.saveResults(city, country, interests, markdownText)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(text = "Guardar resultados", fontSize = 16.sp)
                    }
                }

                // Mostrar texto de no resultados
                else -> {
                    Text(
                        text      = "No se encontraron resultados para tu búsqueda.",
                        fontSize  = 16.sp,
                        color     = MaterialTheme.colorScheme.error,
                        modifier  = Modifier.padding(top = 24.dp)
                    )
                }
            }
        }
    }
}
