package com.example.guiadeviajes_android_gpt.promotions.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun RemoteImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholderSize: Int = 64
) {
    var img by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(url) {
        val bmp = withContext(Dispatchers.IO) {
            try {
                URL(url).openStream().use { stream ->
                    BitmapFactory.decodeStream(stream)
                }?.asImageBitmap()
            } catch (_: Exception) {
                null
            }
        }
        img = bmp
    }

    if (img != null) {
        Image(
            bitmap = img!!,
            contentDescription = contentDescription,
            modifier = modifier
        )
    } else {
        // Mientras carga, un spinner del tamaño que quieras
        Box(modifier = Modifier.size(placeholderSize.dp)) {
            CircularProgressIndicator()
        }
    }
}