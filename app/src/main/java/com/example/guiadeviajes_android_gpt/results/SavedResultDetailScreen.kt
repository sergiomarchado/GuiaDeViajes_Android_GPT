// SavedResultDetailScreen.kt
package com.example.guiadeviajes_android_gpt.results

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.SavedResult
import com.example.guiadeviajes_android_gpt.home.presentation.components.MarkdownWebView
/**
 * SavedResultDetailScreen.kt
 *
 * Pantalla de detalle para una consulta guardada por el usuario.
 * Muestra los parámetros de búsqueda y el contenido en Markdown renderizado.
 *
 * @param id            Identificador de la consulta guardada a mostrar.
 * @param navController Controlador de navegación para volver atrás.
 * @param viewModel     ViewModel que expone la lista de consultas guardadas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedResultDetailScreen(
    id: String,
    navController: NavController,
    viewModel: SavedResultsViewModel = hiltViewModel()
) {
    // 1) Obtener lista de consultas guardadas desde el ViewModel
    val savedList = viewModel.savedResults.collectAsState().value
    // 2) Buscar la consulta cuyo id coincida
    val item: SavedResult? = savedList.find { it.id == id }

    // 3) Estructura principal UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de consulta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        }
    ) { padding ->
        // 4) Si la consulta existe, mostrar detalles
        if (item != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Mostrar ciudad y país en la cabecera
                Text(
                    text = "${item.city}, ${item.country}",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(4.dp))
                // Mostrar intereses seleccionados
                Text(
                    text = "Intereses: ${item.interests}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                // Divisor visual antes del contenido markdown
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                // Renderizar el contenido Markdown usando MarkdownWebView
                MarkdownWebView(markdown = item.markdown)
            }
        } else {
            // 5) Mensaje de error si no se encuentra la consulta
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Consulta no encontrada")
            }
        }
    }
}
