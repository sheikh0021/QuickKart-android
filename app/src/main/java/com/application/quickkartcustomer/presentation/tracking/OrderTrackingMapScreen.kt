package com.application.quickkartcustomer.presentation.tracking

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.quickkartcustomer.ui.theme.DarkBlue
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingMapScreen(
    orderId: Int,
    onBackClick: () -> Unit,
    viewModel: OrderTrackingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val deliveryLocation by viewModel.deliveryLocation.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()
    val error by viewModel.error.collectAsState()

    // Location permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startLiveTracking()
        }
    }

    // Check location permission and start tracking
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.startLiveTracking()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Cleanup tracking when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopLiveTracking()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = deliveryLocation?.let { location ->
                            when {
                                location.assignmentStatus.delivered -> "Order Delivered"
                                location.assignmentStatus.outForDelivery -> "Live Tracking"
                                else -> "Order Tracking"
                            }
                        } ?: "Order Tracking",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshLocation() }) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: $error",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refreshLocation() }) {
                        Text("Retry")
                    }
                }
            } else {
                deliveryLocation?.let { location ->
                    DeliveryMapContent(location)
                } ?: run {
                    // Loading state
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun DeliveryMapContent(deliveryLocation: com.application.quickkartcustomer.domain.model.DeliveryLocation) {
    val assignmentStatus = deliveryLocation.assignmentStatus

    when {
        // PHASE 1: ORDER BEING PACKED
        !assignmentStatus.pickedUp && !assignmentStatus.outForDelivery && !assignmentStatus.delivered -> {
            PackingPhaseMap(deliveryLocation)
        }

        // PHASE 2: OUT FOR DELIVERY (Live Tracking)
        assignmentStatus.outForDelivery && !assignmentStatus.delivered -> {
            DeliveryPhaseMap(deliveryLocation)
        }

        // PHASE 3: ORDER DELIVERED
        assignmentStatus.delivered -> {
            DeliveredPhaseMap(deliveryLocation)
        }
    }
}

@Composable
private fun PackingPhaseMap(deliveryLocation: com.application.quickkartcustomer.domain.model.DeliveryLocation) {
    // Default customer location (should be passed from order data)
    val customerLocation = LatLng(28.6139, 77.2090) // Replace with actual customer address coordinates
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(customerLocation, 15f)
    }

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
                myLocationButtonEnabled = true
            )
        ) {
            // Only show customer delivery address during packing
            Marker(
                state = MarkerState(position = customerLocation),
                title = "Delivery Address",
                snippet = "Your order will be delivered here",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            )
        }

        // Packing progress overlay
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Order is being prepared",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Our team is packing your items carefully",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFF9800)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Estimated preparation time: 15-20 minutes",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun DeliveryPhaseMap(deliveryLocation: com.application.quickkartcustomer.domain.model.DeliveryLocation) {
    val deliveryPartnerLocation = deliveryLocation.location
    val customerLocation = LatLng(28.6139, 77.2090) // Replace with actual customer address coordinates
    val cameraPositionState = rememberCameraPositionState()

    // Update camera to follow delivery partner
    LaunchedEffect(deliveryPartnerLocation) {
        val targetLocation = deliveryPartnerLocation?.let {
            LatLng(it.latitude, it.longitude)
        } ?: customerLocation

        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(targetLocation, 15f),
            1000
        )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapType = MapType.NORMAL
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = true
        )
    ) {
        // Customer delivery address marker
        Marker(
            state = MarkerState(position = customerLocation),
            title = "Delivery Address",
            snippet = "Your order will be delivered here",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        )

        // Delivery partner location marker
        deliveryPartnerLocation?.let { location ->
            Marker(
                state = MarkerState(
                    position = LatLng(location.latitude, location.longitude)
                ),
                title = "Delivery Partner",
                snippet = "${deliveryLocation.deliveryPartner.name} is on the way",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
        }
    }

    // Live tracking overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Delivery Partner En Route",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Partner: ${deliveryLocation.deliveryPartner.name}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Phone: ${deliveryLocation.deliveryPartner.phone}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    androidx.compose.material3.Icon(
                        androidx.compose.material.icons.Icons.Default.DirectionsBike,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DeliveredPhaseMap(deliveryLocation: com.application.quickkartcustomer.domain.model.DeliveryLocation) {
    val customerLocation = LatLng(28.6139, 77.2090) // Replace with actual customer address coordinates
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(customerLocation, 15f)
    }

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
                myLocationButtonEnabled = true
            )
        ) {
            // Customer delivery address with green marker for successful delivery
            Marker(
                state = MarkerState(position = customerLocation),
                title = "Order Delivered Successfully!",
                snippet = "Your order has been delivered to this address",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
        }

        // Delivery completion overlay
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Order Delivered Successfully!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Thank you for choosing QuickKart",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Delivered by: ${deliveryLocation.deliveryPartner.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}