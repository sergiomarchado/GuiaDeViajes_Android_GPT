// app/src/main/java/com/example/guiadeviajes_android_gpt/results/SavedResultsListScreen.kt
package com.example.guiadeviajes_android_gpt.results

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.home.presentation.components.HomeTopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SavedResultsListScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: SavedResultsViewModel = hiltViewModel()
) {
    val items        by viewModel.savedResults.collectAsState()
    val deleteEvents = viewModel.deleteStatus
    val snackbarHost = remember { SnackbarHostState() }

    // Escuchar eventos de borrado para mostrar snackbar
    LaunchedEffect(deleteEvents) {
        deleteEvents.collect { evt ->
            val message = when (evt) {
                is SavedResultsViewModel.DeleteEvent.Success  -> "Consulta eliminada"
                is SavedResultsViewModel.DeleteEvent.Error    -> "Error al eliminar: ${evt.message}"
                SavedResultsViewModel.DeleteEvent.NotLoggedIn -> "Debes iniciar sesión"
            }
            snackbarHost.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            HomeTopAppBar(
                userName   = "Mis consultas",
                userTokens = items.size,  // mostramos el número de consultas como "tokens"
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (items.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes consultas guardadas", fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("saved_detail/${item.id}")
                            },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${item.city}, ${item.country}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = item.interests,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { viewModel.deleteResult(item.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar consulta",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
