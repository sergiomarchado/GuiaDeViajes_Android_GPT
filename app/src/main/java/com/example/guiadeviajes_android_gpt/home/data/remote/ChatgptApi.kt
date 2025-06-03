package com.example.guiadeviajes_android_gpt.home.data.remote

import com.example.guiadeviajes_android_gpt.home.data.remote.dto.ChatiRequestDto
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.ChatiResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatgptApi {

    companion object {
        const val BASE_URL = "https://api.openai.com/v1/"
    }

    @POST("chat/completions")
    suspend fun getTravelInformation(
        @Body body: ChatiRequestDto
    ): ChatiResponseDto
}
