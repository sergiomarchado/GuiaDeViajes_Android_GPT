package com.example.guiadeviajes_android_gpt.home.data.repository
/**
 * TravelGuideRepository.kt
 *
 * Repositorio que formatea una lista de lugares de la API de Google Places
 * y utiliza la API de ChatGPT para convertirla en Markdown con enlaces y formato amigable.
 * Finalmente post-procesa el Markdown para transformar los números de teléfono en enlaces clicables.
 */
import android.util.Log
import com.example.guiadeviajes_android_gpt.home.data.remote.ChatgptApi
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.ChatiRequestDto
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.response.ChatiResponseDto
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.Message
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.SimplePlaceResult
import javax.inject.Inject

class TravelGuideRepository @Inject constructor(
    private val chatGptApi: ChatgptApi
) {

    /**
     * Toma una lista de lugares y un string de intereses, construye un JSON simplificado,
     * envía una petición a ChatGPT para formatear la lista en Markdown y post-procesa
     * los números de teléfono para hacerlos clicables.
     *
     * @param places Lista de SimplePlaceResult obtenida de Google Places.
     * @param interests Cadena con los intereses seleccionados por el usuario.
     * @return Texto en Markdown con formato completo y enlaces tel: para teléfonos.
     */
    suspend fun formatPlacesWithMarkdown(
        places: List<SimplePlaceResult>,
        interests: String
    ): String {
        // 1) Limitar el número de lugares para controlar consumo de tokens
        val MAX_PLACES = 10
        val toSend = places.take(MAX_PLACES)

        // 2) Construir un JSON simplificado con los campos esenciales
        val placesJson = buildString {
            append("[\n")
            toSend.forEachIndexed { idx, p ->
                append("  {\n")
                append("    \"name\": \"${p.name}\"")
                append(",\n    \"address\": \"${p.address.orEmpty()}\"")

                // Incluir phoneNumber si existe
                p.phoneNumber?.takeIf { it.isNotBlank() }?.let {
                    append(",\n    \"phoneNumber\": \"${it}\"")
                }
                // Incluir website si existe
                p.website?.takeIf { it.isNotBlank() }?.let {
                    append(",\n    \"website\": \"${it}\"")
                }
                // Incluir rating si existe
                p.rating?.let {
                    append(",\n    \"rating\": $it")
                }
                append("\n  }")
                if (idx < toSend.lastIndex) append(",")
                append("\n")
            }
            append("]")
        }

        // 3) Definir el mensaje system para guiar a ChatGPT
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

        // 4) Mensaje user con intereses y JSON de lugares
        val userMessage = Message(
            role = "user",
            content = "Intereses: **$interests**\n\nLugares (máx. $MAX_PLACES):\n```json\n$placesJson\n```"
        )

        // 5) Crear request sin streaming
        val requestBody = ChatiRequestDto(
            model    = "gpt-3.5-turbo",
            messages = listOf(systemMessage, userMessage)
        )

        Log.d("TravelGuideRepo", "⏳ Enviando ${toSend.size} lugares a ChatGPT")

        // 6) Enviar a ChatGPT y capturar respuesta
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

        // 7) Post-procesar Markdown para convertir teléfonos en enlaces tel:
        val linkedMarkdown = markdown.replace(
            // Busca líneas que empiecen con "- Teléfono:" seguido de dígitos, espacios o '+'
            Regex("""(?m)^(\s*-\s*Teléfono:\s*)([+\d\s-]+)$""")
        ) { match ->
            val label    = match.groupValues[1]          // Porción "- Teléfono: "
            val rawNum   = match.groupValues[2]          // Porción con el número original
            // Filtrar solo dígitos y '+'
            val digits   = rawNum.filter { it.isDigit() || it == '+' }
            // Construir enlace tel:
            "$label[${rawNum.trim()}](tel:$digits)"
        }

        return linkedMarkdown
    }
}
