package com.example.guiadeviajes_android_gpt.promotions.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.home.presentation.components.HomeTopAppBar
import com.example.guiadeviajes_android_gpt.promotions.data.Promotion
import com.example.guiadeviajes_android_gpt.promotions.components.RemoteImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * PromotionsScreen.kt
 *
 * Muestra los patrocinios desde Firebase (/promotions) con múltiples ofertas.
 * - HomeTopAppBar con botón de Drawer.
 * - Lista de promociones.
 * - Al tocar, abre un AlertDialog con todas las ofertas (code + terms).
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionsScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: PromotionsViewModel = hiltViewModel()
) {
    // 1) Observa promociones
    val promotions by viewModel.promotions.collectAsState()

    // 2) Promo seleccionada para el diálogo
    var dialogPromo by remember { mutableStateOf<Promotion?>(null) }

    Scaffold(
        topBar = {
            HomeTopAppBar(
                userName    = "Promociones",
                userTokens  = promotions.size,
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con título y descripción
            item {
                Text(
                    text = "Promociones",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Disfruta de algunos códigos de descuento que disfrutará tu bolsillo y tu mascota 😜🐶",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
            }

            // Si no hay promociones
            if (promotions.isEmpty()) {
                item {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            "No hay promociones disponibles.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                // Lista de promociones
                items(promotions, key = { it.id }) { promo ->
                    Card(
                        shape     = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier  = Modifier
                            .fillMaxWidth()
                            .clickable { dialogPromo = promo }
                    ) {
                        Row(
                            Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RemoteImage(
                                url                = promo.imageUrl,
                                contentDescription = promo.name,
                                modifier           = Modifier
                                    .size(64.dp)
                                    .clip(MaterialTheme.shapes.small)
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(promo.name, fontSize = 18.sp)
                        }
                    }
                }
            }
        }

        // Diálogo con todas las ofertas de la promo, scrollable y con título/terms/validUntil
        dialogPromo?.let { promo ->
            AlertDialog(
                onDismissRequest = { dialogPromo = null },
                title = {
                    Text(
                        text      = promo.name,
                        fontWeight = FontWeight.Bold,
                        fontSize  = 20.sp
                    )
                },
                text = {
                    // Caja con scroll y altura máxima
                    Box(
                        Modifier
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column {
                            promo.offers.forEach { offer ->
                                // Título de la oferta
                                Text(
                                    text      = offer.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize  = 16.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                // Código
                                Text(
                                    text       = "Código: ${offer.code}",
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.height(4.dp))
                                // Terms
                                Text(
                                    text  = offer.terms,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.height(4.dp))
                                // Fecha de validez
                                Text(
                                    text  = "Válido hasta ${offer.validUntil}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(Modifier.height(8.dp))
                                HorizontalDivider()
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { dialogPromo = null }) {
                        Text("Cerrar")
                    }
                }
            )
        }

    }
}
