// SavedResultsViewModel.kt
package com.example.guiadeviajes_android_gpt.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiadeviajes_android_gpt.home.data.remote.dto.SavedResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedResultsViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    companion object {
        private const val DATABASE_URL =
            "https://guiaviajesia-default-rtdb.europe-west1.firebasedatabase.app/"
    }

    // Live data de las consultas guardadas
    private val _savedResults = MutableStateFlow<List<SavedResult>>(emptyList())
    val savedResults: StateFlow<List<SavedResult>> = _savedResults.asStateFlow()

    // Eventos tras un delete
    private val _deleteStatus = MutableSharedFlow<DeleteEvent>()
    val deleteStatus: SharedFlow<DeleteEvent> = _deleteStatus.asSharedFlow()

    // Refiere al RTDB de Europa
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL)
    private var listener: ValueEventListener? = null

    init {
        observeSavedResults()
    }

    private fun observeSavedResults() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val ref = database.getReference("saved_results/$uid")

        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { ds ->
                    ds.getValue(SavedResult::class.java)
                        ?.copy(id = ds.key.orEmpty())
                }
                _savedResults.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                // Aquí podrías emitir otro Flow si quieres notificar errores de carga
            }
        }
        ref.addValueEventListener(listener!!)
    }

    /**
     * Borra la consulta con el [id] dado y emite un evento en [_deleteStatus].
     */
    fun deleteResult(id: String) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            viewModelScope.launch { _deleteStatus.emit(DeleteEvent.NotLoggedIn) }
            return
        }
        val ref = database.getReference("saved_results/$uid/$id")
        ref.removeValue().addOnCompleteListener { task ->
            viewModelScope.launch {
                if (task.isSuccessful) {
                    _deleteStatus.emit(DeleteEvent.Success)
                } else {
                    _deleteStatus.emit(DeleteEvent.Error(task.exception?.message))
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Limpiamos el listener para evitar fugas de memoria
        val uid = firebaseAuth.currentUser?.uid ?: return
        listener?.let {
            database.getReference("saved_results/$uid")
                .removeEventListener(it)
        }
    }

    /** Resultados posibles de una operación de borrado */
    sealed class DeleteEvent {
        data object Success : DeleteEvent()
        data class Error(val message: String?) : DeleteEvent()
        data object NotLoggedIn : DeleteEvent()
    }
}
