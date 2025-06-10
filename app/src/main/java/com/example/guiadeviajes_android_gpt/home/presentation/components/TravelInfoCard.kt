package com.example.guiadeviajes_android_gpt.home.presentation.components
/**
 * TravelInfoCard.kt
 *
 * Composable que muestra información de viaje formateada en Markdown.
 * Convierte el Markdown a HTML, lo renderiza en un TextView dentro de un Card,
 * habilitando enlaces clicables.
 */
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

@Composable
fun TravelInfoCard(
    travelInfo: String
) {
    // 1) Parsear Markdown a AST
    val parser = Parser.builder().build()
    val document = parser.parse(travelInfo)

    // 2) Renderizar AST a HTML
    val renderer = HtmlRenderer.builder().build()
    val htmlContent = renderer.render(document)

    // Obtener color de texto desde el tema Material3
    val textColorInt = MaterialTheme.colorScheme.onBackground.toArgb()

    // Card que envuelve el contenido HTML
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        // AndroidView para renderizar HTML usando TextView
        AndroidView(
            factory = { context ->
                TextView(context).apply {
                    // Asignar HTML al TextView
                    text = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)
                    // Color de texto acorde al tema y tamaño
                    setTextColor(textColorInt)
                    textSize = 16f

                    // Habilitar enlaces clicables en el TextView
                    movementMethod = LinkMovementMethod.getInstance()
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}
