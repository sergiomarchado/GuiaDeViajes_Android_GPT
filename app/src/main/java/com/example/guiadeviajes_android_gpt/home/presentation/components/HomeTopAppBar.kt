package com.example.guiadeviajes_android_gpt.home.presentation.components
/**
 * HomeTopAppBar.kt
 *
 * Composable que define la barra superior de la pantalla principal.
 * Muestra el logo de la app, nombre de usuario con tokens y un icono de menú para abrir el Drawer.
 */
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guiadeviajes_android_gpt.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    userName: String,
    userTokens: Int,
    onMenuClick: () -> Unit,
    backgroundColor: Color = Color(0xFF011A30)
) {
    // TopAppBar de Material3 con colores personalizados
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor             = backgroundColor,
            navigationIconContentColor = Color.White,
            titleContentColor          = Color.White,
            actionIconContentColor     = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()   // Ocupa el ancho disponible
            .height(100.dp),  // Altura fija para adaptarse al diseño
        title = {
            // Usamos un Box para posicionar los elementos dentro de la AppBar
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // 1) Logotipo de la app en el lado izquierdo
                Image(
                    painter           = painterResource(id = R.drawable.icono_fav),
                    contentDescription = null,
                    modifier          = Modifier
                        .size(58.dp)
                        .align(Alignment.CenterStart)
                )
                // 2) Nombre de usuario con icono de perfil centrado en la AppBar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        imageVector         = Icons.Default.Person,
                        contentDescription  = null,
                        tint                = Color.White,
                        modifier            = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text     = "$userName ($userTokens tokens)",
                        color    = Color.White,
                        fontSize = 14.sp
                    )
                }
                // 3) Botón de menú en el lado derecho para abrir el Drawer
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector        = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint               = Color.White,
                        modifier           = Modifier.size(40.dp)
                    )
                }
            }
        }
    )
}