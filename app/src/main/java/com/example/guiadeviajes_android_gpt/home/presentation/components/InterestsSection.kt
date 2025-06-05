package com.example.guiadeviajes_android_gpt.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
    onGroomersCheckedChange: (Boolean) -> Unit
) {
    // 🔹 Bloque superior: intereses generales
    val generalItems = listOf(
        Triple("Museos", museumsChecked, onMuseumsCheckedChange) to Icons.Default.Museum,
        Triple("Restaurantes", restaurantsChecked, onRestaurantsCheckedChange) to Icons.Default.Restaurant,
        Triple("Sitios emblemáticos", landmarksChecked, onLandmarksCheckedChange) to Icons.Default.LocationOn,
        Triple("Parques naturales", parksChecked, onParksCheckedChange) to Icons.Default.Forest,
        Triple("Playas", beachesChecked, onBeachesCheckedChange) to Icons.Default.BeachAccess,
        Triple("Hoteles o Campings", hotelsChecked, onHotelsCheckedChange) to Icons.Default.Hotel
    )

    Text(
        text = "Desplázate lateralmente y marca uno o varios intereses según tus necesidades:",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        modifier = Modifier.padding(bottom = 8.dp)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(generalItems) { (item, icon) ->
            val (label, isChecked, onCheckedChange) = item
            InterestItem(
                label = label,
                isSelected = isChecked,
                onClick = onCheckedChange,
                icon = icon
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 🔹 Bloque inferior: servicios para perros
    val specialItems = listOf(
        Triple("Veterinarios", vetsChecked, onVetsCheckedChange) to Icons.Default.MedicalServices,
        Triple("Peluquerías caninas", groomersChecked, onGroomersCheckedChange) to Icons.Default.ContentCut,
        Triple("Residencias caninas", dogResortsChecked, onDogResortsCheckedChange) to Icons.Default.Pets
    )

    Text(
        text = "Servicios especiales para tu perro (¡importante!):",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
        modifier = Modifier.padding(bottom = 8.dp)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(specialItems) { (item, icon) ->
            val (label, isChecked, onCheckedChange) = item
            val backgroundColor = when {
                label == "Veterinarios" && isChecked -> Color.Red.copy(alpha = 0.6f)
                label == "Veterinarios" && !isChecked -> Color.Red.copy(alpha = 0.15f) // 🔴 Suave de fondo si no está seleccionado
                isChecked -> Color(0xFF90CAF9) // Azul clarito para seleccionados
                else -> Color.Transparent
            }
            InterestItem(
                label = label,
                isSelected = isChecked,
                onClick = onCheckedChange,
                icon = icon,
                highlightColor = backgroundColor
            )
        }
    }
}

@Composable
private fun InterestItem(
    label: String,
    isSelected: Boolean,
    onClick: (Boolean) -> Unit,
    icon: ImageVector,
    highlightColor: Color = Color.Transparent
) {
    val backgroundColor = highlightColor.takeIf { it != Color.Transparent }
        ?: if (isSelected) Color(0xFF90CAF9) else Color.Transparent

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .size(width = 90.dp, height = 90.dp)
            .clickable { onClick(!isSelected) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                textAlign = TextAlign.Center // 🟢 Centrar completamente el texto multi-línea
            )
        }
    }
}
