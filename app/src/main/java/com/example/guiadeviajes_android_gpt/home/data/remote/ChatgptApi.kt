package com.example.guiadeviajes_android_gpt.home.data.remote
/**
 * ChatgptApi.kt
 *
 * Interfaz Retrofit para consumir el endpoint de Chat Completions de OpenAI.
 * Se utiliza para obtener información de viaje en formato Markdown generada por GPT.
 *
 * BASE_URL: URL base de la API de OpenAI.
 */
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.ChatiRequestDto
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.response.ChatiResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatgptApi {

    /**
     * URL base para todas las peticiones a OpenAI GPT-3.5/Turbo.
     */
    companion object {
        const val BASE_URL = "https://api.openai.com/v1/"
    }

    /**
     * Envía un cuerpo de petición con mensajes al modelo GPT-3.5-turbo
     * y devuelve la respuesta con las opciones generadas.
     *
     * @param body DTO que contiene el modelo, mensajes y parámetros de la petición.
     * @return DTO con la estructura de la respuesta de completions.
     */
    @POST("chat/completions")
    suspend fun getTravelInformation(
        @Body body: ChatiRequestDto
    ): ChatiResponseDto
}
