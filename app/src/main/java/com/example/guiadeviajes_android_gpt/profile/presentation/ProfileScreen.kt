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

@Composable
fun ProfileScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // 1) Observables del ViewModel: perfil, estado de carga y posibles errores
    val userProfile  by viewModel.userProfile.collectAsState()
    val isLoading    by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // 2) Estados locales para los campos editables
    var firstName by remember { mutableStateOf(userProfile.firstName) }
    var lastName  by remember { mutableStateOf(userProfile.lastName) }
    var phone     by remember { mutableStateOf(userProfile.phone) }

    // 3) Habilitar botón Guardar solo cuando haya cambios y no se esté cargando
    val isSaveEnabled = firstName.isNotBlank() && lastName.isNotBlank() && !isLoading

    // 4) Sincronizar campos locales cuando userProfile cambie
    LaunchedEffect(userProfile) {
        firstName = userProfile.firstName
        lastName  = userProfile.lastName
        phone     = userProfile.phone
    }

    // 5) Estructura de UI
    Scaffold(
        topBar = {
            HomeTopAppBar(
                userName    = "${userProfile.firstName} ${userProfile.lastName}",
                userTokens  = userProfile.tokens,
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Permite scroll si el contenido excede
                .padding(padding)
                .padding(16.dp)
        ) {
            // Título de la sección de perfil
            Text("Tu perfil", style = MaterialTheme.typography.titleLarge, fontSize = 22.sp)
            Spacer(Modifier.height(24.dp))

            // Campo Nombre
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Campo Apellidos
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Campo Email (solo lectura)
            OutlinedTextField(
                value = userProfile.email,
                onValueChange = {},
                label = { Text("Email") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Campo Teléfono
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            // Botón Guardar cambios
            Button(
                onClick = {
                    // Invocar actualización en ViewModel
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
            // Indicador de carga centralizado
            if (isLoading) {
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            // Mostrar mensaje de error en caso necesario
            errorMessage?.let { msg ->
                Spacer(Modifier.height(16.dp))
                Text(text = msg, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
