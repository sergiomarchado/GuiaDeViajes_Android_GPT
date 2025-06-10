package com.example.guiadeviajes_android_gpt.home.presentation.components
/**
 * CityCountryInputs.kt
 *
 * Composable que muestra dos campos de texto para seleccionar el país
 * y especificar la ciudad en la pantalla Home.
 */
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun CityCountryInputs(
    selectedCountry: MutableState<String>,
    cityQuery: MutableState<TextFieldValue>
) {
    // Contenedor vertical que ocupa el ancho
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 1) Campo de texto para el país
        OutlinedTextField(
            value = selectedCountry.value,
            onValueChange = {
                // Actualiza el estado del país cuando el usuario edita
                selectedCountry.value = it
            },
            label = {
                Text(
                    "País",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                // Icono de localización para indicar selección de ubicación
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            colors = OutlinedTextFieldDefaults.colors(
                // Borde primario al enfocar
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                // Borde tenue cuando no está enfocado
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2) Campo de texto para la ciudad
        OutlinedTextField(
            value = cityQuery.value,
            onValueChange = {
                // Actualiza el estado de la ciudad a medida que el usuario escribe
                cityQuery.value = it
            },
            label = {
                Text(
                    "Ciudad",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                // Mismo icono
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
