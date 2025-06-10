package com.example.guiadeviajes_android_gpt.promotions.data

import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PromotionsRepository.kt
 *
 * Repositorio para leer promociones definidas en Firebase Realtime Database.
 * Observa el nodo `/patrocinadores` y emite en tiempo real la lista completa.
 *
 * Cada promoción en Firebase debe tener la estructura:
 * patrocinadores/{promoId}:
 *  - imageUrl: String (.png/.jpg)
 *  - name: String
 *  - code: String
 *  - terms: String
 */
@Singleton
class PromotionsRepository @Inject constructor() {
    // Instancia de Firebase apuntando a la región europea
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(
        "https://guiaviajesia-default-rtdb.europe-west1.firebasedatabase.app/"
    )

    // Referencia al nodo de patrocinadores
    private val promosRef: DatabaseReference = database.getReference("promotions")

    /**
     * Observa en tiempo real el listado de promociones.
     * Emite la lista actualizada cada vez que cambian los datos.
     *
     * @return Flow que emite List<Promotion>
     */
    fun observePromotions(): Flow<List<Promotion>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { ds ->
                    ds.getValue(Promotion::class.java)?.copy(id = ds.key.orEmpty())
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        promosRef.addValueEventListener(listener)
        awaitClose { promosRef.removeEventListener(listener) }
    }
}