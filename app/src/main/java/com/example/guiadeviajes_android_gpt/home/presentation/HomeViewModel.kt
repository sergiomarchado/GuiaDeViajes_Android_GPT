package com.example.guiadeviajes_android_gpt.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.ChatiRequestDto
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.ChatiResponseDto
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.Message
import com.example.guiadeviajes_android_gpt.home.data.repository.TravelGuideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TravelGuideRepository
) : ViewModel() {

    // 🔹 Estado que contiene la respuesta final de la API
    private val _travelInfo = MutableStateFlow("")
    val travelInfo: StateFlow<String> = _travelInfo

    // 🔹 Estado de carga para mostrar el progreso en la UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 🔹 Estado para errores que mostramos en un snackbar
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // 🔹 Función principal que llama al repositorio y pide la respuesta
    fun getTravelInformation(userQuery: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 🔹 Creamos el cuerpo de la petición con el prompt del usuario
                val request = ChatiRequestDto(
                    model = "gpt-4o", // Modelo de ChatGPT a usar
                    messages = listOf(
                        // Instrucción de sistema (rol)
                        Message(
                            role = "system",
                            content = "Eres un experto en turismo que recomienda rutas y lugares turísticos según intereses."
                        ),
                        // Mensaje real del usuario (rol user)
                        Message(
                            role = "user",
                            content = userQuery
                        )
                    )
                )

                // 🔹 Llamada al repositorio (red)
                val response: ChatiResponseDto = repository.getTravelInformation(request)

                // 🔹 Obtener el texto final (primera elección devuelta)
                val content = response.choices.firstOrNull()?.message?.content ?: "Sin respuesta"
                _travelInfo.value = content
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 🔹 Limpiar el error cuando se ha mostrado
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
