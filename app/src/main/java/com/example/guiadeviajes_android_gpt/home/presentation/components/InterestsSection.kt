package com.example.guiadeviajes_android_gpt.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Sección de intereses organizada en tres filas:
 * 1. Sitios emblemáticos
 * 2. Experiencias al aire libre y alojamiento
 * 3. Servicios para mascotas
 */
@Composable
fun InterestsSection(
    museumsChecked: Boolean,
    onMuseumsCheckedChange: (Boolean) -> Unit,
    restaurantsChecked: Boolean,
    onRestaurantsCheckedChange: (Boolean) -> Unit,
    landmarksChecked: Boolean,
    onLandmarksCheckedChange: (Boolean) -> Unit,
    parksChecked: Boolean,
    onParksCheckedChange: (Boolean) -> Unit,
    beachesChecked: Boolean,
    onBeachesCheckedChange: (Boolean) -> Unit,
    hotelsChecked: Boolean,
    onHotelsCheckedChange: (Boolean) -> Unit,
    vetsChecked: Boolean,
    onVetsCheckedChange: (Boolean) -> Unit,
    dogResortsChecked: Boolean,
    onDogResortsCheckedChange: (Boolean) -> Unit,
    groomersChecked: Boolean,
    onGroomersCheckedChange: (Boolean) -> Unit,
    pipicanChecked: Boolean,
    onPipicanCheckedChange: (Boolean) -> Unit,
    walkingZonesChecked: Boolean,
    onWalkingZonesCheckedChange: (Boolean) -> Unit
) {
    // 1) Sitios emblemáticos
    val emblematicItems = listOf(
        Triple("Museos", museumsChecked, onMuseumsCheckedChange) to Icons.Default.Museum,
        Triple("Restaurantes", restaurantsChecked, onRestaurantsCheckedChange) to Icons.Default.Restaurant,
        Triple("Emblemáticos", landmarksChecked, onLandmarksCheckedChange) to Icons.Default.LocationOn
    )
    Text(
        text = "Explora lo más típico:",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(emblematicItems) { (item, icon) ->
            val (label, isChecked, onCheckedChange) = item
            InterestItem(label, isChecked, onCheckedChange, icon)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 2) Experiencias outdoor y alojamiento
    val outdoorItems = listOf(
        Triple("Parques naturales", parksChecked, onParksCheckedChange) to Icons.Default.Forest,
        Triple("Playas perros", beachesChecked, onBeachesCheckedChange) to Icons.Default.BeachAccess,
        Triple("Hoteles/Campings", hotelsChecked, onHotelsCheckedChange) to Icons.Default.Hotel,
        Triple("Pipican", pipicanChecked, onPipicanCheckedChange) to Icons.Default.Pets,
        Triple("Zonas paseo", walkingZonesChecked, onWalkingZonesCheckedChange) to Icons.AutoMirrored.Filled.DirectionsWalk
    )
    Text(
        text = "Al aire libre y hospedaje:",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(outdoorItems) { (item, icon) ->
            val (label, isChecked, onCheckedChange) = item
            InterestItem(label, isChecked, onCheckedChange, icon)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 3) Servicios para mascotas
    val serviceItems = listOf(
        Triple("Veterinarios", vetsChecked, onVetsCheckedChange) to Icons.Default.LocalHospital,
        Triple("Peluquería canina", groomersChecked, onGroomersCheckedChange) to Icons.Default.ContentCut,
        Triple("Residencia canina", dogResortsChecked, onDogResortsCheckedChange) to Icons.Default.Home
    )
    Text(
        text = "Servicios para tu mascota:",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(serviceItems) { (item, icon) ->
            val (label, isChecked, onCheckedChange) = item
            InterestItem(label, isChecked, onCheckedChange, icon)
        }
    }
}

@Composable
private fun InterestItem(
    label: String,
    isSelected: Boolean,
    onClick: (Boolean) -> Unit,
    icon: ImageVector
) {
    // Destacar "Veterinarios" en tonos de rojo
    val bg = when (label) {
        "Veterinarios" -> if (isSelected) Color.Red.copy(alpha = 0.6f) else Color.Red.copy(alpha = 0.15f)
        else -> if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent
    }
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .size(100.dp)
            .clickable { onClick(!isSelected) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
