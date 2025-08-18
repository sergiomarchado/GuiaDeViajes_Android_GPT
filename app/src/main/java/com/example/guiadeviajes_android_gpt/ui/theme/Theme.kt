package com.example.guiadeviajes_android_gpt.ui.theme
/**
 * Theme.kt
 *
 * Define los esquemas de color y el tema global de la aplicación.
 * Utiliza Material3, colores dinámicos en Android 12+ y configura barras de estado/navigation edge-to-edge.
 */
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

/**
 * Esquema de colores para modo oscuro personalizado.
 */
private val DarkColorScheme = darkColorScheme(
    primary   = Color(0xFF011A30),
    secondary = PurpleGrey80,
    tertiary  = Pink80,
    background = DarkBlue,
    surfaceVariant = LightBlue,
    secondaryContainer = LightBlue2
)

/**
 * Esquema de colores para modo claro personalizado.
 */
private val LightColorScheme = lightColorScheme(
    primary   = Color(0xFF011A30),
    secondary = PurpleGrey40,
    tertiary  = Pink40,
    surfaceVariant = LightBlueL,
    secondaryContainer = LightBlue2L,
    errorContainer = RedErrorL

)

/**
 * Composable que aplica el tema global de la app.
 *
 * - Selecciona esquema de color dinámico o fijo según versión Android y preferencia de sistema.
 * - Configura edge-to-edge para dibujar contenido tras las barras del sistema.
 * - Ajusta colores e iconos de las barras de estado y navegación.
 * - Envuelve el contenido en MaterialTheme con tipografía definida.
 *
 * @param darkTheme Si es true, fuerza modo oscuro (por defecto detecta el sistema).
 * @param dynamicColor Si es true, utiliza color dinámico en Android 12+.
 * @param content Composable que representa la UI a la que se le aplica el tema.
 */
@Composable
fun GuiaDeViajes_Android_GPTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    // 4) Aplicación del tema Material3 con esquema de color y tipografía personalizada
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
