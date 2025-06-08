package com.example.guiadeviajes_android_gpt.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiadeviajes_android_gpt.home.data.repository.GooglePlacesRepository
import com.example.guiadeviajes_android_gpt.home.data.repository.TravelGuideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val travelGuideRepository: TravelGuideRepository,
    private val googlePlacesRepository: GooglePlacesRepository
) : ViewModel() {

    private val _markdownResults = MutableStateFlow("")
    val markdownResults: StateFlow<String> = _markdownResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var searchJob: Job? = null

    /**
     * @param interests Combined interests string (e.g. "parques naturales, pipican").
     * @param city City name
     * @param country Country name
     */
    fun searchPlacesAndFormatMarkdown(interests: String, city: String, country: String) {
        searchJob?.cancel()
        _markdownResults.value = ""
        _errorMessage.value = null
        _isLoading.value = true

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1) Orquestar la búsqueda inteligente
                Log.d("HOME_VIEWMODEL", "🔎 Lanzando smartSearch para '$interests' en $city, $country")
                val basicResults = googlePlacesRepository
                    .smartSearch(interests = interests, city = city, country = country)
                Log.d("HOME_VIEWMODEL", "✅ smartSearch obtuvo: ${basicResults.size} lugares")

                // 2) Obtener detalles en paralelo (si falta website/phone en resultados básicos)
                val detailedResults = coroutineScope {
                    basicResults.mapNotNull { place ->
                        place.placeId?.let { id ->
                            async(Dispatchers.IO) {
                                googlePlacesRepository.getPlaceDetails(id)
                                    ?.also { Log.d("HOME_VIEWMODEL", "Detalle: ${it.name}") }
                            }
                        }
                    }
                        .awaitAll()
                        .filterNotNull()
                }
                Log.d("HOME_VIEWMODEL", "✅ Total detallados: ${detailedResults.size}")

                // 3) Formatear con ChatGPT
                val markdown = if (detailedResults.isEmpty()) {
                    Log.w("HOME_VIEWMODEL", "Sin resultados tras detalle")
                    ""
                } else {
                    Log.d("HOME_VIEWMODEL", "⏳ Formateando ${detailedResults.size} sitios…")
                    travelGuideRepository.formatPlacesWithMarkdown(detailedResults, interests)
                }
                _markdownResults.value = markdown
                Log.d("HOME_VIEWMODEL", "📄 Markdown length=${markdown.length}")

            } catch (e: CancellationException) {
                Log.d("HOME_VIEWMODEL", "⚠️ Búsqueda cancelada")
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
                Log.e("HOME_VIEWMODEL", "❌ Error inesperado: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Limpia el mensaje de error para poder volver a intentar */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}
