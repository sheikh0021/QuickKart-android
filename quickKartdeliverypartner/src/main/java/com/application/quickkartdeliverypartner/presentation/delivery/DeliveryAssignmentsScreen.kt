package com.application.quickkartdeliverypartner.presentation.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
fun DeliveryAssignmentsScreen() {
    val context = LocalContext.current
    val viewModel = remember { DeliveryAssignmentsViewModel(context) }
    val assignments by viewModel.assignments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val updateStatus by viewModel.updateStatus.collectAsState()

    LaunchedEffect(error) {
        error?.let {
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Delivery Assignments",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (assignments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No delivery assignments yet.\nCheck back later!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn {
                    items(assignments) { assignment ->
                        DeliveryAssignmentCard(
                            assignment = assignment,
                            onStatusUpdate = { status ->
                                viewModel.updateDeliveryStatus(assignment.id, status)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Status update message
            updateStatus?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Error message
            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Error: $it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryAssignmentCard(
    assignment: com.application.quickkartdeliverypartner.domain.model.DeliveryAssignment,
    onStatusUpdate: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
          Column(modifier = Modifier.weight(1f)) {
              Text(
                  text = "Order #${assignment.orderNumber}",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                  text = "₹${assignment.totalAmount}",
                  style = MaterialTheme.typography.titleSmall,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Bold
              )
          }
                StatusIndicator(assignment = assignment)
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Customer Info
            Text("Customer: ${assignment.customerName}",
                style = MaterialTheme.typography.bodyMedium)
            Text("Phone: ${assignment.customerPhone}",
                style = MaterialTheme.typography.bodyMedium)
            Text("Address: ${assignment.deliveryAddress}",
                style = MaterialTheme.typography.bodyMedium)
            Text("Amount: ₹${assignment.totalAmount}",
                style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))
            when {
                assignment.pickedUpAt == null -> {
                    Button(
                        onClick = {onStatusUpdate("picked_up")},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Pick Up Order")
                    }
                }
                assignment.orderStatus == "picked_up" -> {
                    Button(
                        onClick = {onStatusUpdate("out_for_delivery")},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF9B023)
                        )
                    ) {
                        Text("Out For Delivery")
                    }
                }
                assignment.deliveredAt == null -> {
                    Button(
                        onClick = {onStatusUpdate("delivered")},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text("Mark as Delivered")
                    }
                }
                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✓ Delivered",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(assignment: DeliveryAssignment){
    val (statusText, statusColor) = when {
        assignment.deliveredAt != null -> "Delivered" to Color(0xFF4CAF50)
        assignment.orderStatus == "out_for_delivery" -> "Out for Delivery" to Color(0xFFF9B023)
        assignment.pickedUpAt != null -> "Picked Up" to Color(0xFF2196F3)
        else -> "Assigned" to Color(0xFFFF9800)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = statusColor.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = statusText,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}