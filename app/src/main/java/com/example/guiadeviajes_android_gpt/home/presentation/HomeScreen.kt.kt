package com.example.guiadeviajes_android_gpt.home.presentation

import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 🔹 Datos de ejemplo. Reemplázalos con datos reales.
    val userName = "Sergio"
    val userTokens = 120

    var selectedCountry by remember { mutableStateOf("España") }
    var cityQuery by remember { mutableStateOf(TextFieldValue("")) }
    var museumsChecked by remember { mutableStateOf(false) }
    var restaurantsChecked by remember { mutableStateOf(false) }
    var landmarksChecked by remember { mutableStateOf(false) }
    var parksChecked by remember { mutableStateOf(false) }
    var beachesChecked by remember { mutableStateOf(false) }
    var hotelsChecked by remember { mutableStateOf(false) }

    val travelInfo by viewModel.travelInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Long)
            viewModel.clearErrorMessage()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Menú",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Comprar tokens") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        // TODO: Navegar a la compra de tokens
                    },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Editar perfil") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("profile") // 👈 Aquí navega al perfil
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        viewModel.logout()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Default.Logout, contentDescription = null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { /* Lo dejamos vacío */ },
                    navigationIcon = {
                        Column(
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("¡Hola, $userName!", style = MaterialTheme.typography.bodySmall)
                            Text("$userTokens tokens", style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                // 🔹 Ahora el título está aquí, bien separado
                Text(
                    text = "Guía de Viajes Pet-Friendly 🐶",
                    style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Completa los campos para obtener recomendaciones personalizadas adaptadas a tu viaje con perro.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 🔹 Campos de texto
                OutlinedTextField(
                    value = selectedCountry,
                    onValueChange = { selectedCountry = it },
                    label = { Text("País") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = Color(0xFFCCCCCC),
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cityQuery,
                    onValueChange = { cityQuery = it },
                    label = { Text("Ciudad") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = Color(0xFFCCCCCC),
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                InterestsSection(
                    museumsChecked = museumsChecked,
                    onMuseumsCheckedChange = { museumsChecked = it },
                    restaurantsChecked = restaurantsChecked,
                    onRestaurantsCheckedChange = { restaurantsChecked = it },
                    landmarksChecked = landmarksChecked,
                    onLandmarksCheckedChange = { landmarksChecked = it },
                    parksChecked = parksChecked,
                    onParksCheckedChange = { parksChecked = it },
                    beachesChecked = beachesChecked,
                    onBeachesCheckedChange = { beachesChecked = it },
                    hotelsChecked = hotelsChecked,
                    onHotelsCheckedChange = { hotelsChecked = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (cityQuery.text.isNotBlank()) {
                            val interests = mutableListOf<String>()
                            if (museumsChecked) interests.add("museos")
                            if (restaurantsChecked) interests.add("restaurantes")
                            if (landmarksChecked) interests.add("sitios emblemáticos")
                            if (parksChecked) interests.add("parques naturales")
                            if (beachesChecked) interests.add("playas")
                            if (hotelsChecked) interests.add("hoteles o campings")

                            val finalPrompt = buildString {
                                append("Estoy viajando a ${cityQuery.text}, $selectedCountry con mi perro.")
                                if (interests.isNotEmpty()) {
                                    append(" Estoy interesado en: ${interests.joinToString(", ")}.")
                                }
                                append(" Dame recomendaciones de lugares, actividades y rutas turísticas adaptadas para viajar con perro.")
                                append(" Incluye si es posible un teléfono de contacto o página web de los sitios mencionados, y confirma si aceptan perros.")
                                append(" Devuélveme la respuesta en formato Markdown con títulos claros, listas y enlaces.")
                            }

                            viewModel.getTravelInformation(finalPrompt)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Buscar")
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else if (travelInfo.isNotBlank()) {
                    val htmlContent by remember(travelInfo) {
                        derivedStateOf {
                            val parser = Parser.builder().build()
                            val document = parser.parse(travelInfo)
                            val renderer = HtmlRenderer.builder().build()
                            renderer.render(document)
                        }
                    }

                    val textColorInt = MaterialTheme.colorScheme.onSurface.toArgb()

                    Card(
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AndroidView(
                            factory = { context ->
                                TextView(context).apply {
                                    text = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)
                                    setTextColor(textColorInt)
                                    textSize = 16f
                                }
                            },
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

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
    onHotelsCheckedChange: (Boolean) -> Unit
) {
    Column {
        InterestCheckbox("Museos", museumsChecked, onMuseumsCheckedChange, Icons.Default.Museum)
        InterestCheckbox("Restaurantes", restaurantsChecked, onRestaurantsCheckedChange, Icons.Default.Restaurant)
        InterestCheckbox("Sitios emblemáticos", landmarksChecked, onLandmarksCheckedChange, Icons.Default.LocationOn)
        InterestCheckbox("Parques naturales", parksChecked, onParksCheckedChange, Icons.Default.Forest)
        InterestCheckbox("Playas", beachesChecked, onBeachesCheckedChange, Icons.Default.BeachAccess)
        InterestCheckbox("Hoteles o Campings", hotelsChecked, onHotelsCheckedChange, Icons.Default.LocationOn)
    }
}


@Composable
fun InterestCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = Color.Gray
            )
        )
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
