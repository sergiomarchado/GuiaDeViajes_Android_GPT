package com.example.guiadeviajes_android_gpt.results
/**
 * ResultViewModel.kt
 *
 * ViewModel que orquesta la búsqueda inteligente de lugares y el formateado de resultados.
 * Combina repositorios de Google Places y ChatGPT, expone estados de carga, errores,
 * resultados en Markdown y eventos de guardado en Firebase.
 */
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.SavedResult
import com.example.guiadeviajes_android_gpt.home.data.repository.GooglePlacesRepository
import com.example.guiadeviajes_android_gpt.home.data.repository.TravelGuideRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val travelGuideRepository: TravelGuideRepository,
    private val googlePlacesRepository: GooglePlacesRepository,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    companion object {
        // URL de RTDB
        private const val DATABASE_URL = "https://guiaviajesia-default-rtdb.europe-west1.firebasedatabase.app/"
    }

    // Markdown generado por ChatGPT con los lugares recomendados
    private val _markdownResults = MutableStateFlow("")
    val markdownResults: StateFlow<String> = _markdownResults

    // Estado de carga para mostrar ProgressIndicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado de error para mostrar mensajes de fallo
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Eventos de guardado en Firebase: éxito, error o no autenticado
    private val _saveStatus = MutableSharedFlow<SaveEvent>()
    val saveStatus: SharedFlow<SaveEvent> = _saveStatus

    // Job de la búsqueda para poder cancelarla si se inicia otra
    private var searchJob: Job? = null

    /**
     * Inicia la búsqueda inteligente y formateado:
     * 1) smartSearch en Google Places
     * 2) Detallar resultados con getPlaceDetails
     * 3) Formatear Markdown mediante ChatGPT
     * Expone markdownResults o errorMessage según corresponda.
     */
    fun searchPlacesAndFormatMarkdown(interests: String, city: String, country: String) {
        // Cancelar búsqueda previa si existe
        searchJob?.cancel()
        _markdownResults.value = ""
        _errorMessage.value = null
        _isLoading.value = true

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("HOME_VIEWMODEL", "🔎 smartSearch: '$interests', $city, $country")
                // 1) Búsqueda inteligente inicial
                val basicResults = googlePlacesRepository.smartSearch(interests, city, country)
                Log.d("HOME_VIEWMODEL", "✅ smartSearch obtuvo: ${basicResults.size}")

                // 2) Detallar cada lugar en paralelo
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

                // 3) Formatear en Markdown o vacío si no hay resultados
                val markdown = if (detailedResults.isEmpty()) {
                    Log.w("HOME_VIEWMODEL", "Sin resultados tras detalle")
                    ""
                } else {
                    travelGuideRepository.formatPlacesWithMarkdown(detailedResults, interests)
                }
                _markdownResults.value = markdown

            } catch (e: CancellationException) {
                // Se ignoran cancelaciones de job
                Log.d("HOME_VIEWMODEL", "⚠️ Búsqueda cancelada")
            } catch (e: Exception) {
                // Captura de otros errores y propagación al UI
                _errorMessage.value = e.message ?: "Error desconocido"
                Log.e("HOME_VIEWMODEL", "❌ Error inesperado: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpia el mensaje de error actual.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Guarda los resultados generados en Firebase Realtime Database bajo /saved_results/{uid}.
     * Emite SaveEvent según éxito, error o no autenticación.
     */
    fun saveResults(city: String, country: String, interests: String, markdown: String) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            viewModelScope.launch { _saveStatus.emit(SaveEvent.NotLoggedIn) }
            return
        }

        // Referencia push para nuevos resultados
        val db = FirebaseDatabase.getInstance(DATABASE_URL)
        val ref = db.getReference("saved_results/${user.uid}").push()
        val entity = SavedResult(
            id        = ref.key.orEmpty(),
            city      = city,
            country   = country,
            interests = interests,
            markdown  = markdown
        )

        // Escritura asíncrona en la base de datos
        ref.setValue(entity)
            .addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        _saveStatus.emit(SaveEvent.Success(entity.id))
                    } else {
                        _saveStatus.emit(SaveEvent.Error(task.exception?.message))
                    }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        // Cancelar búsqueda si el ViewModel se destruye
        searchJob?.cancel()
    }

    /**
     * Eventos posibles tras intentar guardar en Firebase.
     */
    sealed class SaveEvent {
        // Guardado exitoso
        data class Success(val id: String) : SaveEvent()
        // Error al guardar
        data class Error(val message: String?) : SaveEvent()
        // Usuario no autenticado
        data object NotLoggedIn : SaveEvent()
    }
}
