package com.example.guiadeviajes_android_gpt.auth.presentation.components

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
    val labelFraction by animateFloatAsState(
        targetValue = if (isFocused || value.isNotEmpty()) 1f else 0f,
        animationSpec = tween(300)
    )
    val labelSize by animateFloatAsState(
        targetValue = if (isFocused || value.isNotEmpty()) 12f else 16f,
        animationSpec = tween(300)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, shape = MaterialTheme.shapes.small)
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
    ) {
        // 🔹 Label flotante
        Text(
            text = label,
            fontSize = labelSize.sp,
            color = Color.White,
            modifier = Modifier
                .padding(start = 4.dp)
                .offset(y = (-12 * labelFraction).dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .onFocusChanged { onFocusChanged(it.isFocused) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
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
                    onDone?.invoke()
                }
            )
        )
    }
}
