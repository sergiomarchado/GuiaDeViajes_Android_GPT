package com.example.guiadeviajes_android_gpt.profile.presentation

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
import com.example.guiadeviajes_android_gpt.navigation.BottomNavigationBar
import com.example.guiadeviajes_android_gpt.profile.viewmodel.ProfileViewModel
import com.example.guiadeviajes_android_gpt.home.presentation.components.HomeTopAppBar
import com.example.guiadeviajes_android_gpt.navigation.BottomBarScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // Estado del perfil
    val userProfile  by viewModel.userProfile.collectAsState()
    val isLoading    by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Campos editables
    var firstName by remember { mutableStateOf(userProfile.firstName) }
    var lastName  by remember { mutableStateOf(userProfile.lastName) }
    var phone     by remember { mutableStateOf(userProfile.phone) }

    // Control para habilitar el botón Guardar
    val isSaveEnabled = firstName.isNotBlank() && lastName.isNotBlank() && !isLoading

    LaunchedEffect(userProfile) {
        firstName = userProfile.firstName
        lastName  = userProfile.lastName
        phone     = userProfile.phone
    }

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
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Tu perfil", style = MaterialTheme.typography.titleLarge, fontSize = 22.sp)
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = userProfile.email,
                onValueChange = {},
                label = { Text("Email") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

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
