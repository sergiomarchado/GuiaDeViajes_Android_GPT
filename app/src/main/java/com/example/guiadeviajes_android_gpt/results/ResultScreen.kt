package com.example.guiadeviajes_android_gpt.results

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.home.presentation.components.MarkdownWebView
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavController,
    city: String,
    country: String,
    interests: String,
    viewModel: ResultViewModel = hiltViewModel()
) {
    // Nuevo UiState unificado
    val state by viewModel.uiState.collectAsState()
    val isLoading = state.isLoading
    val markdownText = state.markdown
    val errorMessage = state.error

    val saveEvents = viewModel.saveStatus
    val snackbarHostState = remember { SnackbarHostState() }

    // Lanzar búsqueda sólo cuando cambien parámetros (el VM evita relanzar si es la misma)
    LaunchedEffect(city, country, interests) {
        viewModel.searchPlacesAndFormatMarkdown(interests, city, country)
    }

    // Errores → Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearErrorMessage()
        }
    }

    // Eventos de guardado → Snackbar
    LaunchedEffect(Unit) {
        saveEvents.collectLatest { event ->
            val msg = when (event) {
                is ResultViewModel.SaveEvent.Success  -> "Guardado ✅"
                is ResultViewModel.SaveEvent.Error    -> "Error al guardar: ${event.message}"
                ResultViewModel.SaveEvent.NotLoggedIn -> "Debes iniciar sesión para guardar"
            }
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Resultados en $city, $country",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = padding.calculateStartPadding(LayoutDirection.Ltr),
                    end   = padding.calculateEndPadding(LayoutDirection.Ltr),
                    top   = padding.calculateTopPadding(),
                    bottom = 0.dp
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Aviso
            Text(
                text = "⚠️ Recuerda contrastar la admisión de mascotas y servicios con el propio lugar.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Estamos preparando tu itinerario…\nUn momento, por favor.",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            CircularProgressIndicator()
                        }
                    }
                }

                markdownText.isNotBlank() -> {
                    // Contenido principal con CSS via WebView
                    Box(modifier = Modifier.weight(1f)) {
                        MarkdownWebView(markdown = markdownText)
                    }
                    Spacer(Modifier.height(16.dp))

                    // Guardar resultados
                    Button(
                        onClick = { viewModel.saveResults(city, country, interests, markdownText) },
                        enabled = !isLoading && markdownText.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Guardar resultados", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }

                else -> {
                    Text(
                        text = "No se encontraron resultados para tu búsqueda.",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }
        }
    }
}
