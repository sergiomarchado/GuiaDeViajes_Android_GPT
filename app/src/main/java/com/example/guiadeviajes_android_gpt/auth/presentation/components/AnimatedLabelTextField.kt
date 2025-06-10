package com.example.guiadeviajes_android_gpt.auth.presentation.components
/**
 * AnimatedLabelTextField.kt
 *
 * Composable de campo de texto con etiqueta flotante animada.
 * Cambia tamaño y posición de la etiqueta según el foco y el contenido del campo.
 * Puede usarse para inputs de texto y contraseñas.
 */
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedLabelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isFocused: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    borderColor: Color,
    imeAction: ImeAction,
    isPassword: Boolean = false,
    onDone: (() -> Unit)? = null
) {
    // Animación de la posición de la etiqueta: 0f (label abajo), 1f (label flotando)
    val labelFraction by animateFloatAsState(
        targetValue = if (isFocused || value.isNotEmpty()) 1f else 0f,
        animationSpec = tween(300)
    )
    // Animación del tamaño de la etiqueta: 16sp normal, 12sp flotando
    val labelSize by animateFloatAsState(
        targetValue = if (isFocused || value.isNotEmpty()) 12f else 16f,
        animationSpec = tween(300)
    )

    // Contenedor principal con borde y padding
    Box(
        modifier = Modifier
            .fillMaxWidth()  // Ocupa el ancho disponible
            .border(2.dp, borderColor, shape = MaterialTheme.shapes.small) // Borde
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)  // Espaciado interno
    ) {
        // Etiqueta flotante animada
        Text(
            text = label,
            fontSize = labelSize.sp,   // Tamaño animado
            color = Color.White,
            modifier = Modifier
                .padding(start = 4.dp)   // Separación del borde
                .offset(y = (-12 * labelFraction).dp)  // Desplazamiento vertical animado
        )

        // Campo de texto principal
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth() // Rellena el contenedor
                .background(Color.Transparent)  // Fondo transparente
                .onFocusChanged { onFocusChanged(it.isFocused) }, // Notifica cambios de foco
            colors = TextFieldDefaults.colors(
                // Quitamos indicadores y contenedores por defecto
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                // Personalización de texto e indicador
                cursorColor = Color.White,
                focusedLabelColor = Color.Transparent,
                unfocusedLabelColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Invoca callback al pulsar Done en el teclado
                    onDone?.invoke()
                }
            )
        )
    }
}
