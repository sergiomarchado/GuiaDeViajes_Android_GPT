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
        // URL de tu RTDB en Europa
        private const val DATABASE_URL = "https://guiaviajesia-default-rtdb.europe-west1.firebasedatabase.app/"
    }

    private val _markdownResults = MutableStateFlow("")
    val markdownResults: StateFlow<String> = _markdownResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** Emite eventos tras intentar guardar en Firebase */
    private val _saveStatus = MutableSharedFlow<SaveEvent>()
    val saveStatus: SharedFlow<SaveEvent> = _saveStatus

    private var searchJob: Job? = null

    /**
     * Orquesta smartSearch y formatea con ChatGPT.
     */
    fun searchPlacesAndFormatMarkdown(interests: String, city: String, country: String) {
        searchJob?.cancel()
        _markdownResults.value = ""
        _errorMessage.value = null
        _isLoading.value = true

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("HOME_VIEWMODEL", "🔎 smartSearch: '$interests', $city, $country")
                val basicResults = googlePlacesRepository.smartSearch(interests, city, country)
                Log.d("HOME_VIEWMODEL", "✅ smartSearch obtuvo: ${basicResults.size}")

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

                val markdown = if (detailedResults.isEmpty()) {
                    Log.w("HOME_VIEWMODEL", "Sin resultados tras detalle")
                    ""
                } else {
                    travelGuideRepository.formatPlacesWithMarkdown(detailedResults, interests)
                }
                _markdownResults.value = markdown

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

    /** Limpia mensaje de error. */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Guarda los resultados (con metadatos) en Firebase Realtime Database.
     */
    fun saveResults(city: String, country: String, interests: String, markdown: String) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            viewModelScope.launch { _saveStatus.emit(SaveEvent.NotLoggedIn) }
            return
        }

        val db = FirebaseDatabase.getInstance(DATABASE_URL)
        val ref = db.getReference("saved_results/${user.uid}").push()
        val entity = SavedResult(
            id        = ref.key.orEmpty(),
            city      = city,
            country   = country,
            interests = interests,
            markdown  = markdown
        )

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
        searchJob?.cancel()
    }

    /** Eventos posibles tras intentar guardar. */
    sealed class SaveEvent {
        data class Success(val id: String) : SaveEvent()
        data class Error(val message: String?) : SaveEvent()
        data object NotLoggedIn : SaveEvent()
    }
}
