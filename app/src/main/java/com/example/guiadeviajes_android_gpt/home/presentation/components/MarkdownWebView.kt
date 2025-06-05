package com.example.guiadeviajes_android_gpt.home.presentation.components

import android.content.Intent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import androidx.core.net.toUri

@Composable
fun MarkdownWebView(
    markdown: String
) {
    val htmlContent = convertMarkdownToHtml(markdown)

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = false
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url.toString()
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                        return true
                    }
                }
                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    )
}

private fun convertMarkdownToHtml(markdown: String): String {
    val parser = Parser.builder().build()
    val document = parser.parse(markdown)
    val renderer = HtmlRenderer.builder().build()
    val htmlContent = renderer.render(document)

    val css = """
        <style>
            body {
                font-family: sans-serif;
                padding: 16px;
                background-color: #ffffff;
                color: #333333;
                line-height: 1.6;
            }
            h1, h2, h3 {
                color: #00796B;
                margin-top: 16px;
                margin-bottom: 8px;
            }
            p {
                margin: 8px 0;
            }
            ul, ol {
                margin: 8px 0;
                padding-left: 16px;
            }
            li {
                margin-bottom: 4px;
            }
            a {
                color: #1E88E5;
                text-decoration: underline;
            }
            a:visited {
                color: #5E35B1;
            }
            strong {
                font-weight: bold;
            }
        </style>
    """.trimIndent()

    return "<html><head>$css</head><body>$htmlContent</body></html>"
}
