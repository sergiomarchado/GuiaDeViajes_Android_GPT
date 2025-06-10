package com.example.guiadeviajes_android_gpt.home.presentation.components
/**
 * SearchButton.kt
 *
 * Composable que renderiza un botón para iniciar la búsqueda inteligente.
 * Muestra un indicador de progreso si la búsqueda está en curso.
 */
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchButton(
    isLoading: Boolean,
    onSearch: () -> Unit
) {
    // Botón de acción principal con estilos del tema
    Button(
        onClick = onSearch,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (isLoading) {
            // Muestra un spinner dentro del botón mientras carga
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        } else {
            // Texto del botón cuando no carga
            Text("Búsqueda Inteligente")
        }
    }
}
