package com.application.quickkartdeliverypartner.presentation.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

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
                title = { Text("My Delivery Assignments") }
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Order Info
            Text(
                text = "Order #${assignment.orderNumber}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Customer Info
            Text("Customer: ${assignment.customerName}")
            Text("Phone: ${assignment.customerPhone}")
            Text("Address: ${assignment.deliveryAddress}")
            Text("Amount: ₹${assignment.totalAmount}")

            Spacer(modifier = Modifier.height(12.dp))

            // Status buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (assignment.pickedUpAt == null) {
                    Button(
                        onClick = { onStatusUpdate("picked_up") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pick Up")
                    }
                } else if (assignment.deliveredAt == null) {
                    Button(
                        onClick = { onStatusUpdate("delivered") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mark Delivered")
                    }
                } else {
                    // Already delivered
                    Text(
                        text = "✓ Delivered",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}