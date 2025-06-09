package com.example.guiadeviajes_android_gpt.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// Definición de esquemas de color para modo oscuro y claro
private val DarkColorScheme = darkColorScheme(
    primary   = Color(0xFF011A30),
    secondary = PurpleGrey80,
    tertiary  = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary   = Color(0xFF011A30),
    secondary = PurpleGrey40,
    tertiary  = Pink40
)

@Composable
fun GuiaDeViajes_Android_GPTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // 1) Selección del esquema de color
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    // 2) Edge-to-edge: dejamos que el contenido se dibuje tras las barras
    val view = LocalView.current
    SideEffect {
        WindowCompat.setDecorFitsSystemWindows((view.context as Activity).window, false)
    }

    // 3) Pintado de barras con Accompanist + configuración de iconos claros
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color     = colorScheme.primary,
            darkIcons = false
        )
        systemUiController.setNavigationBarColor(
            color                         = colorScheme.primary,
            darkIcons                     = false,
            navigationBarContrastEnforced = false
        )

        // Aseguramos también vía WindowInsetsControllerCompat
        WindowInsetsControllerCompat(
            (view.context as Activity).window,
            view
        ).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }

    // 4) Aplicamos MaterialTheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
