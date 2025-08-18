package com.example.guiadeviajes_android_gpt.profile.presentation

/**
 * ProfileScreen.kt
 *
 * Pantalla donde el usuario puede ver y editar su perfil.
 * Se muestran campos de nombre, apellidos, email (solo lectura) y teléfono,
 * junto con un botón para guardar cambios.
 */

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.guiadeviajes_android_gpt.profile.viewmodel.ProfileViewModel
import com.example.guiadeviajes_android_gpt.home.presentation.components.HomeTopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // 1) Observables del ViewModel
    val userProfile  by viewModel.userProfile.collectAsState()
    val isLoading    by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // 2) Estados locales para edición (simple y directo)
    var firstName by remember(userProfile) { mutableStateOf(userProfile.firstName) }
    var lastName  by remember(userProfile) { mutableStateOf(userProfile.lastName) }
    var phone     by remember(userProfile) { mutableStateOf(userProfile.phone) }

    // 3) Validación básica
    val isSaveEnabled = firstName.isNotBlank() && lastName.isNotBlank() && !isLoading

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        topBar = {
            HomeTopAppBar(
                userName    = "${userProfile.firstName} ${userProfile.lastName}",
                userTokens  = userProfile.tokens,
                onMenuClick = { scope.launch { drawerState.open() } },
                backgroundColor = Color(0xFF011A30) // mismo color que Home
                // scrollBehavior = null // opcional, si más adelante quieres colapsable
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val layoutDirection = LocalLayoutDirection.current

        Column(
            Modifier
                .fillMaxSize()
                .padding(
                    start = padding.calculateStartPadding(layoutDirection),
                    end   = padding.calculateEndPadding(layoutDirection),
                    top   = padding.calculateTopPadding(),
                    bottom = 0.dp
                )
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Título
            Text("Tu perfil", style = MaterialTheme.typography.titleLarge, fontSize = 22.sp)
            Spacer(Modifier.height(24.dp))

            // Nombre
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Apellidos
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Email (solo lectura)
            OutlinedTextField(
                value = userProfile.email,
                onValueChange = {},
                label = { Text("Email") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Teléfono
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            // Guardar
            Button(
                onClick = {
                    viewModel.updateUserProfile(
                        userProfile.copy(
                            firstName = firstName,
                            lastName  = lastName,
                            phone     = phone
                        )
                    )
                },
                enabled = isSaveEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Guardar", fontSize = 16.sp)
            }

            if (isLoading) {
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            errorMessage?.let { msg ->
                Spacer(Modifier.height(16.dp))
                Text(text = msg, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
