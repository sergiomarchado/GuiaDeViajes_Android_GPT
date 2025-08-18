package com.example.guiadeviajes_android_gpt.home.presentation.components

import android.annotation.SuppressLint
import android.graphics.text.LineBreaker
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

@SuppressLint("ObsoleteSdkInt")
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TravelInfoCard(
    travelInfo: String,
    modifier: Modifier = Modifier
) {
    // Reusar parser/renderer y el HTML resultante entre recomposiciones
    val parser = remember { Parser.builder().build() }
    val renderer = remember { HtmlRenderer.builder().build() }
    val htmlContent = remember(travelInfo) {
        val doc = parser.parse(travelInfo)
        renderer.render(doc)
    }

    val containerColor = MaterialTheme.colorScheme.surface
    val textColorInt = MaterialTheme.colorScheme.onSurface.toArgb()
    val linkColorInt = MaterialTheme.colorScheme.primary.toArgb()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AndroidView(
            modifier = Modifier.padding(16.dp),
            factory = { context ->
                TextView(context).apply {
                    // Render HTML (usamos modo legacy por compatibilidad)
                    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        @Suppress("DEPRECATION")
                        Html.fromHtml(htmlContent)
                    }

                    // Colores y legibilidad
                    setTextColor(textColorInt)
                    setLinkTextColor(linkColorInt)
                    textSize = 16f
                    setLineSpacing(0f, 1.2f) // 20% de interlineado

                    // Enlaces clicables
                    movementMethod = LinkMovementMethod.getInstance()

                    // Mejoras de lectura
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        breakStrategy = LineBreaker.BREAK_STRATEGY_BALANCED
                    }
                }
            },
            update = { tv ->
                // Si cambia el tema o el contenido, actualizamos
                tv.setTextColor(textColorInt)
                tv.setLinkTextColor(linkColorInt)
                val spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    @Suppress("DEPRECATION")
                    Html.fromHtml(htmlContent)
                }
                if (tv.text != spanned) tv.text = spanned
            }
        )
    }
}
