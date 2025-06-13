package com.example.guiadeviajes_android_gpt.map.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.guiadeviajes_android_gpt.BuildConfig
import com.example.guiadeviajes_android_gpt.R
import com.example.guiadeviajes_android_gpt.home.presentation.components.HomeTopAppBar
import com.example.guiadeviajes_android_gpt.map.util.getColorForCategory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var locationQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var initialChecked by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val hasPerm = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val permLauncher = rememberLauncherForActivityResult(RequestPermission()) {
        if (it) viewModel.onLocationPermissionGranted()
    }

    LaunchedEffect(hasPerm) {
        if (!initialChecked) {
            initialChecked = true
            if (hasPerm) viewModel.onLocationPermissionGranted()
            else permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val permissionGranted by viewModel.hasLocationPermission.collectAsState()
    val searchCount by viewModel.searchCount.collectAsState()
    val maxSearches = remember { 10 }

    val mapStyleJson = remember {
        context.resources.openRawResource(R.raw.dark_map_style)
            .bufferedReader().use { it.readText() }
    }

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.4168, -3.7038), 13f)
    }

    viewModel.cameraState = cameraState

    val interests = viewModel.allInterests
    val selectedKeys by viewModel.selectedInterests.collectAsState()
    val places by viewModel.places.collectAsState()
    val detail by viewModel.selectedDetail.collectAsState()

    val sheetState = rememberBottomSheetScaffoldState()
    val sheetScope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            detail?.let { d ->
                Column(Modifier.padding(16.dp)) {
                    IconButton(onClick = { sheetScope.launch { sheetState.bottomSheetState.hide() } }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Cerrar")
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(d.name, fontSize = 20.sp, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(4.dp))
                    d.address?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                    d.phoneNumber?.let {
                        Spacer(Modifier.height(4.dp))
                        Text("\uD83D\uDCDE $it", style = MaterialTheme.typography.bodyMedium)
                    }
                    d.website?.let {
                        Spacer(Modifier.height(4.dp))
                        Text("\uD83C\uDF10 $it", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Fotos", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    LazyRow {
                        items(d.photos) { ref ->
                            val url = "https://maps.googleapis.com/maps/api/place/photo" +
                                    "?maxwidth=400&photoreference=$ref&key=${BuildConfig.API_KEYG}"
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp).padding(end = 8.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val uri = "geo:${d.position.latitude},${d.position.longitude}?q=${Uri.encode(d.name)}".toUri()
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri).apply {
                                setPackage("com.google.android.apps.maps")
                            })
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Abrir en Google Maps")
                    }
                }
            }
        },
        topBar = {
            HomeTopAppBar(
                userName = viewModel.userName,
                userTokens = viewModel.userTokens,
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            Modifier.fillMaxSize().padding(paddingValues)
        ) {
            var lastQuery by remember { mutableStateOf("") }

            OutlinedTextField(
                value = locationQuery,
                onValueChange = { locationQuery = it },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                label = { Text("Buscar ubicación") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (locationQuery.isNotBlank() && locationQuery != lastQuery) {
                                if (searchCount < maxSearches) {
                                    isSearching = true
                                    lastQuery = locationQuery
                                    viewModel.geocodeAndFetch(locationQuery) { center ->
                                        scope.launch {
                                            cameraState.animate(CameraUpdateFactory.newLatLngZoom(center, 13f))
                                            val radius = 1000
                                            viewModel.fetchPlaces(center = center, radius = radius)
                                        }
                                        isSearching = false
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Límite de búsquedas alcanzado.")
                                    }
                                }
                            }
                        },
                        enabled = !isSearching
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                }
            )

            if (isSearching) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            Text(
                text = "Búsquedas realizadas: $searchCount / $maxSearches",
                modifier = Modifier.padding(start = 12.dp, bottom = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            LazyRow(Modifier.padding(8.dp)) {
                items(interests) { interest ->
                    val chipColor = getColorForCategory(interest.label)
                    FilterChip(
                        selected = interest.key in selectedKeys,
                        onClick = { viewModel.toggleInterest(interest.key) },
                        label = { Text(interest.label) },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = chipColor.copy(alpha = 0.2f),
                            labelColor = chipColor,
                            selectedContainerColor = chipColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Box(Modifier.weight(1f)) {
                if (permissionGranted) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraState,
                        properties = MapProperties(
                            isMyLocationEnabled = true,
                            mapStyleOptions = MapStyleOptions(mapStyleJson),
                            mapType = MapType.NORMAL
                        ),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            myLocationButtonEnabled = true,
                            zoomGesturesEnabled = true,
                            scrollGesturesEnabled = true,
                            tiltGesturesEnabled = true
                        )
                    ) {
                        places.forEach { item ->
                            Marker(
                                state = MarkerState(item.position),
                                title = item.name,
                                snippet = buildString {
                                    item.address?.let { appendLine(it) }
                                    item.phoneNumber?.let { appendLine("Tel: $it") }
                                    item.website?.let { appendLine("Web: $it") }
                                }.trim().ifBlank { null },
                                icon = BitmapDescriptorFactory.defaultMarker(
                                    when (item.category) {
                                        "Hospitales Vet." -> BitmapDescriptorFactory.HUE_RED
                                        "Veterinarios" -> BitmapDescriptorFactory.HUE_ROSE
                                        "Museos", "Monumentos" -> BitmapDescriptorFactory.HUE_VIOLET
                                        "Parques", "Pipican / Zona Paseo", "Zonas Paseo" -> BitmapDescriptorFactory.HUE_GREEN
                                        "Playas" -> BitmapDescriptorFactory.HUE_ORANGE
                                        "Restaurantes" -> BitmapDescriptorFactory.HUE_AZURE
                                        "Hoteles", "Campings" -> BitmapDescriptorFactory.HUE_BLUE
                                        "Peluquerías", "Tiendas de Piensos" -> BitmapDescriptorFactory.HUE_YELLOW
                                        else -> BitmapDescriptorFactory.HUE_VIOLET
                                    }
                                ),
                                onClick = {
                                    viewModel.selectPlace(item)
                                    scope.launch { sheetState.bottomSheetState.expand() }
                                    true
                                }
                            )
                        }
                    }

                    if (searchCount < maxSearches) {
                        Button(
                            onClick = {
                                val bounds = cameraState.projection?.visibleRegion?.latLngBounds
                                if (bounds != null) {
                                    val center = bounds.center
                                    val farCorner = bounds.northeast
                                    val radius = viewModel.calculateVisibleRadius(center, farCorner)
                                    if (radius <= 3000) {
                                        viewModel.fetchPlaces(center = center, radius = radius)
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Área demasiado grande. Reduce el zoom para buscar."
                                            )
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        ) {
                            Text("Buscar en esta zona")
                        }
                    } else {
                        Text(
                            "Límite de búsquedas alcanzado.",
                            color = Color.Red,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        )
                    }
                } else {
                    Text("Permiso de ubicación necesario", Modifier.padding(16.dp))
                }
            }
        }
    }
}