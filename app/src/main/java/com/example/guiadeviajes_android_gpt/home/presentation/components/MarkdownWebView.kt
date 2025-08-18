package com.example.guiadeviajes_android_gpt.home.presentation.components

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.os.Build
import android.webkit.*
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

@Composable
fun MarkdownWebView(
    markdown: String,
    modifier: Modifier = Modifier
) {
    // Reusar parser/renderer
    val parser = remember { Parser.builder().build() }
    val renderer = remember { HtmlRenderer.builder().build() }

    // Colores desde el tema (para CSS)
    val container = MaterialTheme.colorScheme.surface
    val onContainer = MaterialTheme.colorScheme.onSurface
    val link = MaterialTheme.colorScheme.primary

    // HTML final recordado (recalcula si cambia markdown o los colores relevantes)
    val htmlContent = remember(markdown, container, onContainer, link) {
        val body = renderer.render(parser.parse(markdown))
        val css = buildCss(
            bg = container.toArgb(),
            fg = onContainer.toArgb(),
            link = link.toArgb()
        )
        """
        <html>
          <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            $css
          </head>
          <body>
            $body
          </body>
        </html>
        """.trimIndent()
    }

    // Estado actualizado de html para el update {}
    val htmlState by rememberUpdatedState(htmlContent)

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            factory = { context ->
                WebView(context).apply {
                    secureSettings()
                    setBackgroundColor(AndroidColor.TRANSPARENT)

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val uri = request?.url ?: return false
                            return openExternal(context.packageManager, uri.toString())
                        }

                        @Deprecated("Deprecated in Java")
                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                            val u = url ?: return false
                            return openExternal(context.packageManager, u)
                        }
                    }

                    // Carga inicial
                    loadDataWithBaseURL(
                        /* baseUrl = */ null,
                        /* data     = */ htmlContent,
                        /* mimeType = */ "text/html",
                        /* encoding = */ "UTF-8",
                        /* historyUrl = */ null
                    )
                }
            },
            update = { webView ->
                // Sólo recargar si cambió el HTML
                webView.evaluateJavascript(
                    "(document.documentElement.outerHTML || '')"
                ) { current ->
                    val changed = current == null || !current.contains(htmlState.substring(0, minOf(100, htmlState.length)))
                    if (changed) {
                        webView.loadDataWithBaseURL(null, htmlState, "text/html", "UTF-8", null)
                    }
                }
            }
        )
    }
}

private fun buildCss(bg: Int, fg: Int, link: Int): String {
    fun Int.css(): String = "#%06X".format(0xFFFFFF and this)
    val bgCss = bg.css()
    val fgCss = fg.css()
    val linkCss = link.css()

    return """
        <style>
            html, body {
                background: $bgCss;
                color: $fgCss;
                line-height: 1.6;
                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu,
                             Cantarell, "Helvetica Neue", sans-serif;
                padding: 16px;
            }
            h1, h2, h3 {
                color: $linkCss;
                margin-top: 20px;
                margin-bottom: 10px;
                font-weight: 600;
            }
            h1 { font-size: 22px; }
            h2 { font-size: 18px; }
            h3 { font-size: 16px; }
            p { margin: 12px 0; font-size: 16px; }
            ul, ol { margin: 12px 0 12px 22px; }
            li { margin: 6px 0; }
            a { color: $linkCss; text-decoration: none; border-bottom: 1px dotted $linkCss; }
            a:hover { opacity: .9; }
            code, pre {
                background: ${shade(bg, 0.08).css()};
                color: ${fgCss};
                border-radius: 6px;
                padding: 4px 6px;
            }
            pre { padding: 10px; overflow-x: auto; }
            blockquote {
                border-left: 4px solid ${shade(fg, 0.4).css()};
                background: ${shade(bg, 0.06).css()};
                padding: 12px 14px; margin: 12px 0;
                font-style: italic;
            }
        </style>
    """.trimIndent()
}

private fun shade(color: Int, alpha: Double): Int {
    val r = (color shr 16) and 0xFF
    val g = (color shr 8) and 0xFF
    val b = color and 0xFF
    // mezcla con negro para un sombreado sencillo
    val nr = (r * (1 - alpha)).toInt()
    val ng = (g * (1 - alpha)).toInt()
    val nb = (b * (1 - alpha)).toInt()
    return (nr shl 16) or (ng shl 8) or nb
}

@SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
private fun WebView.secureSettings() {
    with(settings) {
        javaScriptEnabled = false
        domStorageEnabled = false
        setSupportZoom(false)
        builtInZoomControls = false
        displayZoomControls = false

        allowFileAccess = false
        allowContentAccess = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }
    }
    isVerticalScrollBarEnabled = true
    isHorizontalScrollBarEnabled = false
}

private fun openExternal(pm: android.content.pm.PackageManager, url: String): Boolean {
    val uri = url.toUri()
    val scheme = uri.scheme?.lowercase()
    val handled = scheme in setOf("http", "https", "mailto", "tel", "geo")
    if (!handled) return false

    return try {
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        // Si hay actividad que pueda manejarlo -> lanza
        if (intent.resolveActivity(pm) != null) {
            return true
        }
        false
    } catch (_: ActivityNotFoundException) {
        false
    } catch (_: Exception) {
        false
    }
}
