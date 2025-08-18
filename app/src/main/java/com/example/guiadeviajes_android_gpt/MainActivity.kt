package com.example.guiadeviajes_android_gpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.guiadeviajes_android_gpt.ui.app.AppScaffold
import com.example.guiadeviajes_android_gpt.ui.app.rememberAppState
import com.example.guiadeviajes_android_gpt.ui.theme.GuiaDeViajes_Android_GPTTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
    }
}

@Composable
private fun App() {
    GuiaDeViajes_Android_GPTTheme {
        val appState = rememberAppState()
        AppScaffold(
            navController = appState.navController,
            drawerState = appState.drawerState,
            snackbarHostState = appState.snackbarHostState,
            showFrame = appState.showFrame
        )
    }
}
