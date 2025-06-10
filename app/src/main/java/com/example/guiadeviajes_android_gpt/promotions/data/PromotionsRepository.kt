package com.example.guiadeviajes_android_gpt.promotions.presentation.data

import com.example.guiadeviajes_android_gpt.promotions.presentation.Promotion
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
 * Observa el nodo `/promotions` y emite en tiempo real la lista completa.
 *
 * Cada promoción en Firebase debe tener la estructura:
 * promotions/{promoId}:
 *  - imageUrl: String (.png/.jpg)
 *  - name: String
 *  - code: String
 *  - terms: String
 */
@Singleton
class PromotionsRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    // Referencia al nodo de promociones
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