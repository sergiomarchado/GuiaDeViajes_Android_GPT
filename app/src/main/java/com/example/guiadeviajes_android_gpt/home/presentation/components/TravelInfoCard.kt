package com.example.guiadeviajes_android_gpt.home.presentation.components

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
    val parser = Parser.builder().build()
    val document = parser.parse(travelInfo)
    val renderer = HtmlRenderer.builder().build()
    val htmlContent = renderer.render(document)

    val textColorInt = MaterialTheme.colorScheme.onBackground.toArgb()

    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { context ->
                TextView(context).apply {
                    text = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)
                    setTextColor(textColorInt)
                    textSize = 16f
                    // 🔗 Habilita que los enlaces sean clicables
                    movementMethod = LinkMovementMethod.getInstance()
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}
