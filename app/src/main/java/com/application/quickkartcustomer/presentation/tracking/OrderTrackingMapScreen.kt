package com.application.quickkartcustomer.presentation.tracking

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.core.content.ContextCompat
import android.content.Intent
import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.quickkartcustomer.ui.components.DeliveryTimeline
import com.application.quickkartcustomer.ui.components.MapMarkerUtils
import com.application.quickkartcustomer.ui.theme.DarkBlue
import com.application.quickkartcustomer.ui.theme.Surface
import com.application.quickkartcustomer.ui.theme.Primary
import com.application.quickkartcustomer.ui.navigation.Screen
import com.application.quickkartcustomer.ui.navigation.NavigationStateManager
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingMapScreen(
    orderId: Int,
    onBackClick: () -> Unit,
    navController: NavController? = null,
    navigationStateManager: NavigationStateManager,
    viewModel: OrderTrackingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val deliveryLocation by viewModel.deliveryLocation.collectAsState()
    val route by viewModel.route.collectAsState()
    val animatedLocation by viewModel.animatedLocation.collectAsState()
    val trackingPhase by viewModel.trackingPhase.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()
    val lastUpdateTime by viewModel.lastUpdateTime.collectAsState()
    val error by viewModel.error.collectAsState()

    // ✅ Get REAL customer location from ViewModel state (loaded from order)
    val customerLocation by viewModel.customerLocation.collectAsState()
    val storeLocation by viewModel.storeLocation.collectAsState()

    // Camera position state - use default location if customerLocation is null
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            customerLocation ?: LatLng(21.6139, 77.2090), // Default location
            14f
        )
    }

    // Location permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startLiveTracking()
        }
    }

    // Check permission and start tracking
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

    // Auto-follow delivery partner
    LaunchedEffect(animatedLocation) {
        animatedLocation?.currentPosition?.let { position ->
            if (deliveryLocation?.assignmentStatus?.outForDelivery == true) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(position, 15f),
                    durationMs = 1000
                )
            }
        }
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopLiveTracking()
        }
    }

    Scaffold(
        topBar = {
            // Green header matching image style
            Surface(
                color = Color(0xFF4CAF50), // Green background like image
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Status bar spacing
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back button
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Status text and ETA
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when {
                                    deliveryLocation?.assignmentStatus?.delivered == true -> "Order Delivered"
                                    deliveryLocation?.assignmentStatus?.outForDelivery == true -> "Order is on the way"
                                    deliveryLocation?.assignmentStatus?.pickedUp == true -> "Order is on the way"
                                    else -> "Order Tracking"
                                },
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                            
                            // ETA (only show when out for delivery)
                            val etaMinutes = route?.etaMinutes ?: 0
                            if (deliveryLocation?.assignmentStatus?.outForDelivery == true && etaMinutes > 0) {
                                Text(
                                    text = "Arriving in $etaMinutes ${if (etaMinutes == 1) "minute" else "minutes"}",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        // Recenter button
                        IconButton(
                            onClick = {
                                val target: LatLng = animatedLocation?.currentPosition 
                                    ?: customerLocation 
                                    ?: LatLng(21.6139, 77.2090)
                                cameraPositionState.move(
                                    CameraUpdateFactory.newLatLngZoom(target, 15f)
                                )
                            }
                        ) {
                            Icon(
                                Icons.Default.MyLocation,
                                contentDescription = "Recenter",
                                tint = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // ETA is now in the header, so remove duplicate banner
            
            // Real-time polling indicator (Step 2.3)
            lastUpdateTime?.let { updateTime ->
                val secondsAgo = (System.currentTimeMillis() - updateTime) / 1000
                if (secondsAgo < 10) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "● Live",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            if (error != null) {
                // Error State
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
            } else if (deliveryLocation == null) {
                // Loading State
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Map with Tracking
                LiveTrackingMap(
                    deliveryLocation = deliveryLocation!!,
                    route = route,
                    animatedLocation = animatedLocation,
                    customerLocation = customerLocation,
                    cameraPositionState = cameraPositionState,
                    storeLocation = storeLocation
                )

                // Delivery Partner Info Card (Matches Image Style)
                deliveryLocation?.let { location ->
                    location.deliveryPartner?.let { partner ->
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F5F5) // Light grey like image
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Yellow avatar (circle with person icon) - matches image
                                    Surface(
                                        modifier = Modifier.size(50.dp),
                                        shape = androidx.compose.foundation.shape.CircleShape,
                                        color = Color(0xFFFFEB3B) // Yellow
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = null,
                                                modifier = Modifier.size(30.dp),
                                                tint = Color.White
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.width(12.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "I'm ${partner.name}, your delivery partner",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color(0xFF212121)
                                        )
                                    }
                                    
                                    // Call Button (green circle)
                                    Surface(
                                        modifier = Modifier.size(40.dp),
                                        shape = androidx.compose.foundation.shape.CircleShape,
                                        color = Color(0xFF4CAF50) // Green
                                    ) {
                                        IconButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                    data = Uri.parse("tel:${partner.phone}")
                                                }
                                                context.startActivity(intent)
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Phone,
                                                contentDescription = "Call",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Green status strip (matches image)
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFFE8F5E9), // Light green background
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = when {
                                            location.assignmentStatus.delivered -> "Order has been delivered"
                                            location.assignmentStatus.outForDelivery -> "I have picked up your order, and I am on the way to your location"
                                            location.assignmentStatus.pickedUp -> "I have picked up your order"
                                            else -> "Preparing your order"
                                        },
                                        modifier = Modifier.padding(12.dp),
                                        fontSize = 12.sp,
                                        color = Color(0xFF2E7D32) // Dark green text
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Timeline Card
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    DeliveryTimeline(
                        status = deliveryLocation!!.assignmentStatus,
                        etaMinutes = route?.etaMinutes,
                        deliveryPartnerName = deliveryLocation!!.deliveryPartner.name
                    )
                }

                // Chat Button - Show when delivery partner is assigned
                deliveryLocation?.deliveryPartner?.let { partner ->
                    if (navController != null) {
                        FloatingActionButton(
                            onClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "deliveryPartnerName",
                                    partner.name
                                )
                                navController.navigate(Screen.Chat.createRoute(orderId))
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            containerColor = Primary,
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Chat with delivery partner",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun LiveTrackingMap(
    deliveryLocation: com.application.quickkartcustomer.domain.model.DeliveryLocation,
    route: com.application.quickkartcustomer.domain.model.DeliveryRoute?,
    animatedLocation: com.application.quickkartcustomer.domain.model.AnimatedLocation?,
    customerLocation: LatLng?,
    cameraPositionState: CameraPositionState,
    storeLocation: LatLng? = null
) {
    val context = LocalContext.current

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapType = MapType.NORMAL
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            compassEnabled = true
        )
    ) {

        // Route polyline (blue line) - Z-Index 1
        route?.polylinePoints?.let { points ->
            Polyline(
                points = points,
                color = Color(0xFF2196F3), // Blue
                width = 12f,
                geodesic = true,
                zIndex = 1f
            )
        }

        // 1. Store/Pickup Marker (Black circle with green building) - Z-Index 2
        storeLocation?.let { location ->
            Marker(
                state = MarkerState(position = location),
                title = "Store",
                snippet = "Order pickup location",
                icon = MapMarkerUtils.createStoreIcon(context),
                zIndex = 2f
            )
        }

        // 2. Customer Destination Marker (Blue pin with yellow bullseye) - Z-Index 3
        customerLocation?.let { location ->
            Marker(
                state = MarkerState(position = location),
                title = "Delivery Address",
                snippet = "Your order will be delivered here",
                icon = MapMarkerUtils.createCustomerHomeIcon(context),
                zIndex = 3f
            )
        }

        // 3. Delivery Partner Marker (Yellow person on green motorcycle) - Z-Index 4
        animatedLocation?.currentPosition?.let { position ->
            if (deliveryLocation.assignmentStatus.outForDelivery) {
                Marker(
                    state = MarkerState(position = position),
                    title = deliveryLocation.deliveryPartner.name,
                    snippet = "On the way",
                    icon = MapMarkerUtils.createDeliveryBikeIcon(
                        context,
                        bearing = animatedLocation.bearing
                    ),
                    anchor = Offset(0.5f, 0.5f), // Center the marker
                    flat = true, // Makes marker rotate with map
                    rotation = animatedLocation.bearing,
                    zIndex = 4f
                )
            }
        }
    }
}