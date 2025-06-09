// File: app/src/main/java/com/example/guiadeviajes_android_gpt/promotions/presentation/PromotionsScreen.kt
package com.example.guiadeviajes_android_gpt.promotions.presentation

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
fun PromotionsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Promociones") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Aquí irán las Promociones 🎁")
        }
    }
}
