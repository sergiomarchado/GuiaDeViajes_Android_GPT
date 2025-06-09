// File: app/src/main/java/com/example/guiadeviajes_android_gpt/map/presentation/MapScreen.kt
package com.example.guiadeviajes_android_gpt.map.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mapa") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Aquí irá el Mapa 🗺️")
        }
    }
}
