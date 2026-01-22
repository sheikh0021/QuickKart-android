package com.application.quickkartdeliverypartner.presentation.delivery


import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.application.quickkartdeliverypartner.ui.navigation.DeliveryScreen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryTrackingMapScreen(
    navController: androidx.navigation.NavController,
    orderId: Int,
    assignment: com.application.quickkartdeliverypartner.domain.model.DeliveryAssignment,
    viewModel: DeliveryTrackingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Location state
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var isUpdating by remember { mutableStateOf(false) }
    val error by viewModel.error.collectAsState()

    // Get delivery coordinates with fallback (from Step 1.3.6)
    var deliveryLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoadingCoordinates by remember { mutableStateOf(false) }

    LaunchedEffect(assignment) {
        // Try to use coordinates from assignment first
        if (assignment.deliveryLatitude != null && assignment.deliveryLongitude != null) {
            deliveryLocation = LatLng(
                assignment.deliveryLatitude,
                assignment.deliveryLongitude
            )
        } else {
            // Fallback to geocoding (from Step 1.3.5)
            isLoadingCoordinates = true
            com.application.quickkartdeliverypartner.core.util.GeocodingUtil.geocodeAddress(
                assignment.deliveryAddress
            ).fold(
                onSuccess = { location ->
                    deliveryLocation = location
                    isLoadingCoordinates = false
                },
                onFailure = { error ->
                    // Show error or use default location
                    deliveryLocation = LatLng(28.6139, 77.2090)
                    isLoadingCoordinates = false
                }
            )
        }
    }

    // Camera position - start at delivery address (use default if coordinates not loaded)
    val defaultLocation = LatLng(28.6139, 77.2090)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            deliveryLocation ?: defaultLocation,
            15f
        )
    }

    // Location permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, can get current location
        }
    }

    // Check permission on launch
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Location - Order #$orderId") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(DeliveryScreen.Chat.createRoute(orderId))
                        }
                    ) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = "Chat with customer",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedLocation?.let { location ->
                        isUpdating = true
                        viewModel.updateLocation(
                            orderId = orderId,
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                        // Reset after update using coroutine scope
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(1000)
                            isUpdating = false
                        }
                    }
                },
                modifier = Modifier.padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Update Location",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Instructions Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Tap on the map to mark your current location, then press the check button to update",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Error message
            error?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Loading coordinates indicator
            if (isLoadingCoordinates) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Map
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true,
                        mapType = MapType.NORMAL
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = true,
                        compassEnabled = true
                    ),
                    onMapClick = { location ->
                        selectedLocation = location
                    }
                ) {
                    // Delivery address marker (red pin) - only show if coordinates loaded
                    deliveryLocation?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "Delivery Address",
                            snippet = assignment.deliveryAddress,
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )
                    }

                    // Selected location marker (blue pin)
                    selectedLocation?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "Your Location",
                            snippet = "Tap check to update",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        )
                    }
                }

                // Location info card (bottom)
                selectedLocation?.let { location ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Selected Location",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Latitude: ${String.format("%.6f", location.latitude)}")
                            Text("Longitude: ${String.format("%.6f", location.longitude)}")
                        }
                    }
                }
            }
        }
    }
}