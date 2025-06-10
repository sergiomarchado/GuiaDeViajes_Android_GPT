package com.example.guiadeviajes_android_gpt.promotions.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
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
 * Pantalla que muestra los patrocinios desde Firebase (/promotions).
 * Se invoca dentro de tu Scaffold global (ContentScaffold).
 * Al tocar una tarjeta abre un AlertDialog con código y términos.
 */

@Composable
fun PromotionsScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: PromotionsViewModel = hiltViewModel()
) {
    // 1) Observa promociones
    val promotions by viewModel.promotions.collectAsState()
    // 2) Estado para el diálogo de detalle
    var dialogPromo by remember { mutableStateOf<Promotion?>(null) }

    Scaffold(
        topBar = {
            HomeTopAppBar(
                userName    = "Sergio",
                userTokens  = promotions.size,
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
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
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "Disfruta de algunos códigos de descuento que disfrutará tu bolsillo y tu mascota 😜🐶",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
            }

            // Lista de promociones
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

        // Diálogo de detalle
        dialogPromo?.let { promo ->
            AlertDialog(
                onDismissRequest = { dialogPromo = null },
                title = {
                    Text(
                        promo.name,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        Text("Código de descuento:", fontWeight = FontWeight.Medium)
                        Text(
                            promo.code,
                            fontSize = 20.sp,
                            color    = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Términos y condiciones:", fontWeight = FontWeight.Medium)
                        Text(promo.terms)
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
