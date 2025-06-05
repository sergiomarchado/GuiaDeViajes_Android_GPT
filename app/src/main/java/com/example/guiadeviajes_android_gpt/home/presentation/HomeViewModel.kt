package com.example.guiadeviajes_android_gpt.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.ChatiRequestDto
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.ChatiResponseDto
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.Message
import com.example.guiadeviajes_android_gpt.home.data.repository.TravelGuideRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TravelGuideRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _travelInfo = MutableStateFlow("")
    val travelInfo: StateFlow<String> = _travelInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun getTravelInformation(userQuery: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = ChatiRequestDto(
                    model = "gpt-4o",
                    messages = listOf(
                        Message(
                            role = "system",
                            content = "Eres un experto en turismo que recomienda rutas y lugares turísticos según intereses."
                        ),
                        Message(
                            role = "user",
                            content = userQuery
                        )
                    )
                )
                val response: ChatiResponseDto = repository.getTravelInformation(request)
                val content = response.choices.firstOrNull()?.message?.content ?: "Sin respuesta"
                Log.d("API_RESPONSE", content)
                _travelInfo.value = content
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearTravelInfo() {
        _travelInfo.value = ""
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
