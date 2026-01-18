package com.application.quickkartdeliverypartner.presentation.delivery

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.application.quickkartdeliverypartner.ui.navigation.DeliveryScreen
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.application.quickkartdeliverypartner.domain.model.DeliveryAssignment
import com.application.quickkartdeliverypartner.core.network.RetrofitClient
import com.application.quickkartdeliverypartner.core.util.PreferencesManager
import com.application.quickkartdeliverypartner.data.mapper.DeliveryMapper
import com.application.quickkartdeliverypartner.data.remote.api.DeliveryApi
import com.application.quickkartdeliverypartner.data.repository.DeliveryRepositoryImpl
import com.application.quickkartdeliverypartner.domain.usecase.DeliveryUseCase


@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun DeliveryDashboardScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    RetrofitClient.init(preferencesManager)
    val deliveryApi = RetrofitClient.getAuthenticatedClient().create(DeliveryApi::class.java)
    val deliveryMapper = DeliveryMapper()
    val deliveryRepository = DeliveryRepositoryImpl(deliveryApi, deliveryMapper)
    val deliveryUseCase = DeliveryUseCase(deliveryRepository)
    val viewModel = DeliveryDashboardViewModel(deliveryUseCase)
    val assignments by viewModel.assignments.collectAsState()
    val earnings by viewModel.earnings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(error) {
        error?.let {
            // Show error message (you can implement a snackbar here)
            viewModel.clearError()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Delivery Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Earnings Card
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Earnings Overview",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "₹${earnings.today}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Column {
                        Text(
                            text = "This Week",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "₹${earnings.week}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Column {
                        Text(
                            text = "This Month",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "₹${earnings.month}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }

        // Active Assignments
        Text(
            text = "Active Assignments (${assignments.size})",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(assignments) { assignment ->
                    DeliveryAssignmentCardClickable(assignment) {
                        navController.navigate(DeliveryScreen.Assignments.route)
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryAssignmentCardClickable(
    assignment: DeliveryAssignment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Order #${assignment.orderNumber}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Customer: ${assignment.customerName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Phone: ${assignment.customerPhone}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Amount: ₹${assignment.totalAmount}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Status: ${assignment.orderStatus}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = assignment.deliveryAddress,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
        }
    }
}