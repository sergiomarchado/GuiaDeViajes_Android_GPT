package com.example.guiadeviajes_android_gpt.home.presentation.components
/**
 * MarkdownWebView.kt
 *
 * Composable que muestra contenido en formato Markdown dentro de un WebView.
 * El Markdown se convierte a HTML con estilos CSS modernos y se carga en un WebView
 * dentro de un Card con bordes redondeados y sombra. Los enlaces se abren externamente.
 */
import android.content.Intent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import androidx.core.net.toUri

@Composable
fun MarkdownWebView(
    markdown: String
) {
    // 1) Convertir Markdown a HTML con estilos CSS (función helper)
    val htmlContent = convertMarkdownToHtml(markdown)

    // 2) Mostrar el contenido HTML dentro de un Card para estilo de tarjeta
    Card(
        // Bordes redondeados y elevación para sombra
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        // 3) Usamos AndroidView para incrustar un WebView nativo
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    // Desactivar JavaScript por seguridad
                    settings.javaScriptEnabled = false

                    // Manejar clics en enlaces: abrir navegador externo
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url.toString()
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(intent)
                            return true   // No cargar internamente
                        }
                    }

                    // Cargar el HTML generado en el WebView
                    loadDataWithBaseURL(
                        /* baseUrl = */ null,
                        /* data     = */ htmlContent,
                        /* mimeType = */ "text/html",
                        /* encoding = */ "UTF-8",
                        /* historyUrl = */ null
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )
    }
}

/**
 * Convierte una cadena Markdown a un documento HTML completo
 * incluyendo una hoja de estilos CSS para darle mejor formato.
 *
 * @param markdown Texto en formato Markdown.
 * @return Cadena de HTML lista para cargar en WebView.
 */
private fun convertMarkdownToHtml(markdown: String): String {
    // 1) Parsear Markdown a AST usando CommonMark
    val parser = Parser.builder().build()
    val document = parser.parse(markdown)

    // 2) Renderizar AST a HTML básico
    val renderer = HtmlRenderer.builder().build()
    val htmlBody = renderer.render(document)

    // 3) Definir estilos CSS: tipografías, colores, márgenes, listados, bloques de código y enlaces
    val css = """
        <style>
            /* -------------------------------------------
               Reset de márgenes y paddings básicos
            ------------------------------------------- */
            *, *::before, *::after {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            
            /* -------------------------------------------
               Estilos del <body>
            ------------------------------------------- */
            body {
                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Helvetica Neue", sans-serif;
                background-color: #F5F5F5;          /* Gris muy claro */
                color: #333333;                     /* Texto oscuro suave */
                line-height: 1.6;
                padding: 16px;
            }

            /* -------------------------------------------
               Encabezados (h1, h2, h3, h4) 
            ------------------------------------------- */
            h1, h2, h3 {
                color: #1E88E5;                     /* Azul suave para encabezados */
                margin-top: 24px;
                margin-bottom: 12px;
                font-weight: 600;
            }
            h1 { font-size: 24px; }
            h2 { font-size: 20px; }
            h3 { font-size: 18px; }

            /* -------------------------------------------
               Párrafos y texto normal
            ------------------------------------------- */
            p {
                margin-bottom: 12px;
                font-size: 16px;
            }

            /* -------------------------------------------
               Listas (ul, ol) con “card-like” containers
            ------------------------------------------- */
            ul, ol {
                margin: 16px 0;
                padding-left: 24px;
            }
            li {
                margin-bottom: 8px;
                font-size: 16px;
            }

            /* -------------------------------------------
               Bloques de código, si hubiese
            ------------------------------------------- */
            pre, code {
                background-color: #ECEFF1;         /* Fondo gris suave */
                padding: 8px;
                border-radius: 4px;
                overflow-x: auto;
                font-family: monospace;
                margin-bottom: 12px;
            }

            /* -------------------------------------------
               Enlaces (<a>)
            ------------------------------------------- */
            a {
                color: #1976D2;                     /* Azul vivo */
                text-decoration: none;
                border-bottom: 1px dotted #1976D2; /* Línea punteada bajo enlace */
            }
            a:hover {
                color: #0D47A1;                     /* Azul más oscuro al pasar ratón */
            }
            a:visited {
                color: #512DA8;                     /* Púrpura suave para enlaces visitados */
            }

            /* -------------------------------------------
               Citar texto (<blockquote>)
            ------------------------------------------- */
            blockquote {
                border-left: 4px solid #B0BEC5;     /* Línea gris al lado */
                background-color: #ECEFF1;         /* Fondo gris suave */
                padding: 12px 16px;
                margin: 16px 0;
                font-style: italic;
            }
        </style>
    """.trimIndent()

    // 4) Construir el documento HTML completo con meta viewport
    return """
        <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                $css
            </head>
            <body>
                $htmlBody
            </body>
        </html>
    """.trimIndent()
}
