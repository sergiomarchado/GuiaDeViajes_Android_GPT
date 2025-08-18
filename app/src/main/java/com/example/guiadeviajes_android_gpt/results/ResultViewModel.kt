package com.example.guiadeviajes_android_gpt.results

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
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultUiState(
    val isLoading: Boolean = false,
    val markdown: String = "",
    val error: String? = null,
    // Últimos parámetros usados (para evitar relanzar la misma búsqueda)
    val lastCity: String? = null,
    val lastCountry: String? = null,
    val lastInterests: String? = null
)

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val travelGuideRepository: TravelGuideRepository,
    private val googlePlacesRepository: GooglePlacesRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    companion object {
        private const val TAG = "RESULT_VM"
        private const val SEARCH_TIMEOUT_MS = 25_000L
    }

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState

    // Eventos de guardado
    private val _saveStatus = MutableSharedFlow<SaveEvent>()
    val saveStatus: SharedFlow<SaveEvent> = _saveStatus

    private var searchJob: Job? = null

    fun searchPlacesAndFormatMarkdown(interests: String, city: String, country: String) {
        val normCity = city.trim()
        val normCountry = country.trim()
        val normInterests = interests.trim()

        // Validación básica
        if (normCity.isBlank() || normCountry.isBlank()) {
            _uiState.update { it.copy(error = "Ciudad y país son obligatorios") }
            return
        }

        // Evitar relanzar si es exactamente la misma consulta y ya tenemos resultado
        val s = _uiState.value
        if (s.lastCity?.equals(normCity, ignoreCase = true) == true &&
            s.lastCountry?.equals(normCountry, ignoreCase = true) == true &&
            s.lastInterests?.equals(normInterests, ignoreCase = true) == true &&
            s.markdown.isNotBlank()
        ) {
            Log.d(TAG, "🟡 Misma búsqueda detectada, no relanzo.")
            return
        }

        // Cancelar búsqueda previa y preparar estado
        searchJob?.cancel()
        _uiState.update {
            it.copy(
                isLoading = true,
                markdown = "",
                error = null,
                lastCity = normCity,
                lastCountry = normCountry,
                lastInterests = normInterests
            )
        }

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                withTimeout(SEARCH_TIMEOUT_MS) {
                    Log.d(TAG, "🔎 smartSearch: '$normInterests' | $normCity, $normCountry")

                    // 1) Búsqueda inicial
                    val basicResults = googlePlacesRepository.smartSearch(normInterests, normCity, normCountry)
                    Log.d(TAG, "✅ smartSearch obtuvo: ${basicResults.size}")

                    // 2) Detalles en paralelo (supervisorScope: un fallo no tumba todo)
                    val detailedResults = supervisorScope {
                        basicResults.mapNotNull { place ->
                            place.placeId?.let { id ->
                                async(Dispatchers.IO) {
                                    runCatching { googlePlacesRepository.getPlaceDetails(id) }
                                        .getOrNull()
                                }
                            }
                        }.awaitAll().filterNotNull()
                    }
                    Log.d(TAG, "✅ Total detallados: ${detailedResults.size}")

                    // 3) Formateo Markdown
                    val markdown = if (detailedResults.isEmpty()) {
                        ""
                    } else {
                        travelGuideRepository.formatPlacesWithMarkdown(detailedResults, normInterests)
                    }

                    _uiState.update { it.copy(isLoading = false, markdown = markdown, error = null) }
                }
            } catch (_: TimeoutCancellationException) {
                Log.w(TAG, "⏱️ Timeout de búsqueda")
                _uiState.update {
                    it.copy(isLoading = false, error = "La búsqueda tardó demasiado. Inténtalo de nuevo.")
                }
            } catch (_: CancellationException) {
                Log.d(TAG, "⚠️ Búsqueda cancelada")
                // No tocamos el estado para evitar parpadeos al relanzar
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error inesperado: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Se produjo un error realizando la búsqueda."
                    )
                }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(error = null) }
    }

    fun saveResults(city: String, country: String, interests: String, markdown: String) {
        // Validaciones básicas
        if (markdown.isBlank()) {
            viewModelScope.launch { _saveStatus.emit(SaveEvent.Error("No hay contenido para guardar")) }
            return
        }
        val user = firebaseAuth.currentUser
        if (user == null) {
            viewModelScope.launch { _saveStatus.emit(SaveEvent.NotLoggedIn) }
            return
        }

        val db = FirebaseDatabase.getInstance()
        val ref = db.getReference("saved_results/${user.uid}").push()
        val entity = SavedResult(
            id        = ref.key.orEmpty(),
            city      = city,
            country   = country,
            interests = interests,
            markdown  = markdown,
            // createdAt = System.currentTimeMillis() // 👉 añade el campo en tu data class si te interesa
        )

        ref.setValue(entity)
            .addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        _saveStatus.emit(SaveEvent.Success(entity.id))
                    } else {
                        _saveStatus.emit(SaveEvent.Error(task.exception?.message ?: "Fallo al guardar"))
                    }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

    sealed class SaveEvent {
        data class Success(val id: String) : SaveEvent()
        data class Error(val message: String?) : SaveEvent()
        data object NotLoggedIn : SaveEvent()
    }
}
