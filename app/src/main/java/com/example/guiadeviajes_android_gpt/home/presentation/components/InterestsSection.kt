package com.example.guiadeviajes_android_gpt.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    campingsChecked: Boolean,
    onCampingsCheckedChange: (Boolean) -> Unit,
    pipicanChecked: Boolean,
    onPipicanCheckedChange: (Boolean) -> Unit,
    walkingZonesChecked: Boolean,
    onWalkingZonesCheckedChange: (Boolean) -> Unit,

    vetsChecked: Boolean,
    onVetsCheckedChange: (Boolean) -> Unit,
    hospitalsChecked: Boolean,
    onHospitalsCheckedChange: (Boolean) -> Unit,
    groomersChecked: Boolean,
    onGroomersCheckedChange: (Boolean) -> Unit,
    dogResortsChecked: Boolean,
    onDogResortsCheckedChange: (Boolean) -> Unit,
    petStoresChecked: Boolean,
    onPetStoresCheckedChange: (Boolean) -> Unit
) {
    // 1) Sitios emblemáticos
    SectionHeader("Explora lo más típico:")
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(
            listOf(
                Triple("Museos", museumsChecked, onMuseumsCheckedChange) to Icons.Default.Museum,
                Triple("Restaurantes", restaurantsChecked, onRestaurantsCheckedChange) to Icons.Default.Restaurant,
                Triple("Monumentos", landmarksChecked, onLandmarksCheckedChange) to Icons.Default.LocationOn
            )
        ) { (item, icon) ->
            InterestCard(label = item.first, isSelected = item.second, onToggle = item.third, icon = icon)
        }
    }

    Spacer(Modifier.height(16.dp))

    // 2) Outdoor + hospedaje
    SectionHeader("Al aire libre y hospedaje:")
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(
            listOf(
                Triple("Parques naturales", parksChecked, onParksCheckedChange) to Icons.Default.Forest,
                Triple("Playas perros", beachesChecked, onBeachesCheckedChange) to Icons.Default.BeachAccess,
                Triple("Hoteles", hotelsChecked, onHotelsCheckedChange) to Icons.Default.Hotel,
                Triple("Campings", campingsChecked, onCampingsCheckedChange) to Icons.Default.Cottage,
                Triple("Pipican", pipicanChecked, onPipicanCheckedChange) to Icons.Default.Pets,
                Triple("Zonas paseo", walkingZonesChecked, onWalkingZonesCheckedChange) to Icons.AutoMirrored.Filled.DirectionsWalk
            )
        ) { (item, icon) ->
            InterestCard(label = item.first, isSelected = item.second, onToggle = item.third, icon = icon)
        }
    }

    Spacer(Modifier.height(16.dp))

    // 3) Servicios mascota
    SectionHeader(
        text = "Servicios para tu mascota:",
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(
            listOf(
                Triple("Veterinarios", vetsChecked, onVetsCheckedChange) to Icons.Default.LocalHospital,
                Triple("Hospitales 24h", hospitalsChecked, onHospitalsCheckedChange) to Icons.Default.Healing,
                Triple("Peluquería canina", groomersChecked, onGroomersCheckedChange) to Icons.Default.ContentCut,
                Triple("Residencia canina", dogResortsChecked, onDogResortsCheckedChange) to Icons.Default.Home,
                Triple("Tiendas de piensos", petStoresChecked, onPetStoresCheckedChange) to Icons.Default.Store
            )
        ) { (item, icon) ->
            val critical = item.first in listOf("Veterinarios", "Hospitales 24h")
            InterestCard(
                label = item.first,
                isSelected = item.second,
                onToggle = item.third,
                icon = icon,
                critical = critical
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String, color: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = color,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun InterestCard(
    label: String,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit,
    icon: ImageVector,
    critical: Boolean = false
) {
    // Paleta M3: diferenciamos cards seleccionadas/normal y casos críticos con errorContainer
    val (container, content) = when {
        critical && isSelected -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        critical && !isSelected -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f) to MaterialTheme.colorScheme.onErrorContainer
        isSelected -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        onClick = { onToggle(!isSelected) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .size(width = 120.dp, height = 100.dp)
            .semantics {
                selected = isSelected
            },
        // Semántica de botón con estado de selección
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = content, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                color = content
            )
        }
    }
}
