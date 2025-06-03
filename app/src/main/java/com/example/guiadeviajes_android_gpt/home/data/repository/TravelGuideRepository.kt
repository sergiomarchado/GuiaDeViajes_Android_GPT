package com.example.guiadeviajes_android_gpt.home.data.repository

import com.example.guiadeviajes_android_gpt.home.data.remote.ChatgptApi
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.ChatiRequestDto
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.ChatiResponseDto
import javax.inject.Inject

class TravelGuideRepository @Inject constructor(
    private val api: ChatgptApi
) {
    suspend fun getTravelInformation(request: ChatiRequestDto): ChatiResponseDto {
        return api.getTravelInformation(request)
    }
}
