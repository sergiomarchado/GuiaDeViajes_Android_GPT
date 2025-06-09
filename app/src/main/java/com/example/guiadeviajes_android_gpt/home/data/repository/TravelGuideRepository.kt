package com.example.guiadeviajes_android_gpt.home.data.repository

import android.util.Log
import com.example.guiadeviajes_android_gpt.home.data.remote.ChatgptApi
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.ChatiRequestDto
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.ChatiResponseDto
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.Message
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.SimplePlaceResult
import javax.inject.Inject

/**
 * Repositorio que formatea una lista de lugares en Markdown,
 * pidiéndole a ChatGPT un formato conciso con los campos esenciales,
 * y post-procesa para hacer clicables los teléfonos.
 */
class TravelGuideRepository @Inject constructor(
    private val chatGptApi: ChatgptApi
) {

    suspend fun formatPlacesWithMarkdown(
        places: List<SimplePlaceResult>,
        interests: String
    ): String {
        // Limitar a primeros 10 resultados para reducir tokens
        val MAX_PLACES = 10
        val toSend = places.take(MAX_PLACES)

        // Construir JSON con campos esenciales
        val placesJson = buildString {
            append("[\n")
            toSend.forEachIndexed { idx, p ->
                append("  {\n")
                append("    \"name\": \"${p.name}\"")
                append(",\n    \"address\": \"${p.address.orEmpty()}\"")
                p.phoneNumber?.takeIf { it.isNotBlank() }?.let {
                    append(",\n    \"phoneNumber\": \"${it}\"")
                }
                p.website?.takeIf { it.isNotBlank() }?.let {
                    append(",\n    \"website\": \"${it}\"")
                }
                p.rating?.let {
                    append(",\n    \"rating\": $it")
                }
                append("\n  }")
                if (idx < toSend.lastIndex) append(",")
                append("\n")
            }
            append("]")
        }

        // Mensaje system conciso
        val systemMessage = Message(
            role = "system",
            content = """
                Eres un Guía de Viajes experto en recomendar sitios que admiten mascotas.
                Recibirás JSON con name, address, phoneNumber, website, rating.
                Devuélvelo en formato Markdown:
                1. Saludo corto y cálido (<=15 palabras).
                2. Por cada lugar que tengas del JSON deberás incluir:
                   - ### Nombre
                   - Dirección: [dirección](https://maps.google.com/?q=<dirección codificada>)
                   - Teléfono: <número> (si existe)
                   - Web: <sitio> (si existe)
                   - Valoración: <número>
                   - Comentario breve de cómo es el servicio de cara al usuario.
                3. Despedida corta y cálida.
            """.trimIndent()
        )

        // Mensaje user con intereses y JSON
        val userMessage = Message(
            role = "user",
            content = "Intereses: **$interests**\n\nLugares (máx. $MAX_PLACES):\n```json\n$placesJson\n```"
        )

        // Construir request sin 'stream'
        val requestBody = ChatiRequestDto(
            model    = "gpt-3.5-turbo",
            messages = listOf(systemMessage, userMessage)
        )

        Log.d("TravelGuideRepo", "⏳ Enviando ${toSend.size} lugares a ChatGPT")

        val markdown = try {
            val response: ChatiResponseDto = chatGptApi.getTravelInformation(requestBody)
            response.choices
                .firstOrNull()
                ?.message
                ?.content
                .orEmpty()
                .also {
                    Log.d("TravelGuideRepo", "✅ Markdown length=${it.length}")
                    Log.v("TravelGuideRepo", "📄 Markdown:\n$it")
                }
        } catch (e: Exception) {
            Log.e("TravelGuideRepo", "❌ Error ChatGPT: ${e.message}", e)
            throw e
        }

        // Post-procesado: envolver números de teléfono en enlaces tel:
        val linkedMarkdown = markdown.replace(
            // Busca líneas que empiecen con "- Teléfono:" seguido de dígitos, espacios o signos '+'
            Regex("""(?m)^(\s*-\s*Teléfono:\s*)([+\d\s-]+)$""")
        ) { match ->
            val label    = match.groupValues[1]          // "- Teléfono: "
            val rawNum   = match.groupValues[2]          // "123 456 789"
            val digits   = rawNum.filter { it.isDigit() || it == '+' }
            // Ejemplo: "- Teléfono: [123 456 789](tel:123456789)"
            "$label[${rawNum.trim()}](tel:$digits)"
        }

        return linkedMarkdown
    }
}
