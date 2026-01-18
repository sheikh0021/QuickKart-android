package com.application.quickkartdeliverypartner.presentation.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.application.quickkartdeliverypartner.domain.model.DeliveryAssignment
import androidx.compose.ui.platform.LocalContext
import com.application.quickkartdeliverypartner.core.network.RetrofitClient
import com.application.quickkartdeliverypartner.core.util.PreferencesManager
import com.application.quickkartdeliverypartner.data.mapper.DeliveryMapper
import com.application.quickkartdeliverypartner.data.remote.api.DeliveryApi
import com.application.quickkartdeliverypartner.data.repository.DeliveryRepositoryImpl
import com.application.quickkartdeliverypartner.domain.usecase.DeliveryUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryTrackingScreen(
    navController: NavController,
    orderId: Int,
    assignments: List<DeliveryAssignment> = emptyList() // This should be passed from previous screen
) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    RetrofitClient.init(preferencesManager)
    val deliveryApi = RetrofitClient.getAuthenticatedClient().create(DeliveryApi::class.java)
    val deliveryMapper = DeliveryMapper()
    val deliveryRepository = DeliveryRepositoryImpl(deliveryApi, deliveryMapper)
    val deliveryUseCase = DeliveryUseCase(deliveryRepository)
    val viewModel = DeliveryTrackingViewModel(deliveryUseCase)
    val assignment by viewModel.assignment.collectAsState()
    val error by viewModel.error.collectAsState()

    // For now, just use the passed assignments
    val allAssignments = assignments

    val assignmentsToUse = if (assignments.isNotEmpty()) assignments else allAssignments

    LaunchedEffect(orderId, assignmentsToUse) {
        viewModel.loadAssignment(orderId, assignmentsToUse)
    }

    LaunchedEffect(error) {
        error?.let {
            // Show error message
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Delivery Tracking") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            assignment?.let { assignment ->
                // Order details
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Order #${assignment.orderNumber}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Customer: ${assignment.customerName}")
                        Text("Phone: ${assignment.customerPhone}")
                        Text("Address: ${assignment.deliveryAddress}")
                        Text("Status: ${assignment.orderStatus}")
                    }
                }

                // Map placeholder (you'll need to implement actual map integration)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Map Integration Needed\n(Latitude: Current Location)\nDelivery Address: ${assignment.deliveryAddress}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // TODO: Implement Google Maps integration
                    // GoogleMap(
                    //     modifier = Modifier.fillMaxSize(),
                    //     cameraPositionState = rememberCameraPositionState {
                    //         position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 15f)
                    //     }
                    // ) {
                    //     // Add markers, polylines, etc.
                    // }
                }

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = {
                        // Update location (you'll need to get actual GPS coordinates)
                        viewModel.updateLocation(orderId, 0.0, 0.0) // Placeholder coordinates
                    }) {
                        Text("Update Location")
                    }

                    OutlinedButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Text("Back to Assignments")
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Assignment not found")
                }
            }
        }
    }
}