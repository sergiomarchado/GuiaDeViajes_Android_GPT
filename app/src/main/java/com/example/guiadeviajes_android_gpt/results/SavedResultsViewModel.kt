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
/**
 * SavedResultsViewModel.kt
 *
 * ViewModel encargado de observar y gestionar los resultados guardados por el usuario
 * en Firebase Realtime Database. Expone flujos para la lista de resultados y para eventos
 * tras la eliminación de un elemento.
 *
 * @constructor Inyecta FirebaseAuth para obtener al usuario actual.
 * @property firebaseAuth Instancia de FirebaseAuth para autenticación del usuario.
 */
@HiltViewModel
class SavedResultsViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    companion object {
        private const val DATABASE_URL =
            "https://guiaviajesia-default-rtdb.europe-west1.firebasedatabase.app/"
    }

    // Flujo que contiene la lista actual de resultados guardados
    private val _savedResults = MutableStateFlow<List<SavedResult>>(emptyList())
    val savedResults: StateFlow<List<SavedResult>> = _savedResults.asStateFlow()

    // Flujo que emite eventos tras intentar eliminar un resultado
    private val _deleteStatus = MutableSharedFlow<DeleteEvent>()
    val deleteStatus: SharedFlow<DeleteEvent> = _deleteStatus.asSharedFlow()

    // Instancia de FirebaseDatabase y listener para observar cambios
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL)
    private var listener: ValueEventListener? = null

    init {
        // Comenzar a observar resultados al inicializar el ViewModel
        observeSavedResults()
    }

    /**
     * Registra un ValueEventListener en la ruta /saved_results/{uid}
     * para actualizar _savedResults cada vez que cambien los datos.
     */
    private fun observeSavedResults() {
        val uid = firebaseAuth.currentUser?.uid ?: return // Si no hay usuario, no hacer nada
        val ref = database.getReference("saved_results/$uid")

        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Mapear cada child a SavedResult, asignando la clave como id
                val list = snapshot.children.mapNotNull { ds ->
                    ds.getValue(SavedResult::class.java)
                        ?.copy(id = ds.key.orEmpty())
                }
                _savedResults.value = list // Emitir nueva lista
            }

            override fun onCancelled(error: DatabaseError) {
                // Podríamos emitir un error en otro Flow si fuera necesario
            }
        }
        ref.addValueEventListener(listener!!)
    }

    /**
     * Elimina el resultado guardado con el id especificado.
     * Emite un event en deleteStatus según el resultado de la operación.
     *
     * @param id Identificador del SavedResult a eliminar.
     */
    fun deleteResult(id: String) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            // Emitir evento de no autenticado
            viewModelScope.launch { _deleteStatus.emit(DeleteEvent.NotLoggedIn) }
            return
        }
        // Referencia al nodo específico de este resultado
        val ref = database.getReference("saved_results/$uid/$id")
        ref.removeValue().addOnCompleteListener { task ->
            viewModelScope.launch {
                if (task.isSuccessful) {
                    _deleteStatus.emit(DeleteEvent.Success)   // Eliminación exitosa
                } else {
                    _deleteStatus.emit(DeleteEvent.Error(task.exception?.message))
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Remover listener para evitar fugas de memoria
        val uid = firebaseAuth.currentUser?.uid ?: return
        listener?.let {
            database.getReference("saved_results/$uid")
                .removeEventListener(it)
        }
    }

    /**
     * Evento resultante de una operación de eliminación.
     */
    sealed class DeleteEvent {
        // Eliminación completada
        data object Success : DeleteEvent()
        // Error al eliminar
        data class Error(val message: String?) : DeleteEvent()
        // Usuario no autenticado
        data object NotLoggedIn : DeleteEvent()
    }
}
