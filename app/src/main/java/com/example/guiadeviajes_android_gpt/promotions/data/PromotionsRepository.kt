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
 * Repositorio para leer promociones con múltiples ofertas desde
 * Firebase Realtime Database. Observa el nodo `/promotions` y emite
 * en tiempo real la lista de Promotion, cada una con su lista
 * de Offer.
 *
 * Estructura esperada en Firebase:
 * promotions/{promoId}:
 *   - name:       String
 *   - imageUrl:   String
 *   - offers:
 *       {offerId}:
 *         - code:       String
 *         - title:      String
 *         - terms:      String
 *         - validUntil: String
 */
@Singleton
class PromotionsRepository @Inject constructor() {
    // Instancia apuntando a la URL de tu RTDB en Europa
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(
        "https://guiaviajesia-default-rtdb.europe-west1.firebasedatabase.app/"
    )

    // Referencia al nodo principal
    private val promotionsRef: DatabaseReference =
        database.getReference("promotions")

    /**
     * Observa en tiempo real el listado de promociones, mapeando
     * cada snapshot a Promotion + List<Offer>.
     */
    fun observePromotions(): Flow<List<Promotion>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { promoSnap ->
                    val id       = promoSnap.key ?: return@mapNotNull null
                    val name     = promoSnap.child("name").getValue(String::class.java)
                        ?: return@mapNotNull null
                    val imageUrl = promoSnap.child("imageUrl")
                        .getValue(String::class.java).orEmpty()

                    // Mapeamos el sub-nodo "offers"
                    val offers = promoSnap.child("offers").children.mapNotNull { offerSnap ->
                        val offerId    = offerSnap.key ?: return@mapNotNull null
                        val code       = offerSnap.child("code")
                            .getValue(String::class.java)
                            ?: return@mapNotNull null
                        val title      = offerSnap.child("title")
                            .getValue(String::class.java)
                            .orEmpty()
                        val terms      = offerSnap.child("terms")
                            .getValue(String::class.java)
                            .orEmpty()
                        val validUntil = offerSnap.child("validUntil")
                            .getValue(String::class.java)
                            .orEmpty()
                        Offer(
                            id         = offerId,
                            code       = code,
                            title      = title,
                            terms      = terms,
                            validUntil = validUntil
                        )
                    }

                    Promotion(
                        id       = id,
                        name     = name,
                        imageUrl = imageUrl,
                        offers   = offers
                    )
                }
                trySend(list).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        promotionsRef.addValueEventListener(listener)
        awaitClose { promotionsRef.removeEventListener(listener) }
    }
}
