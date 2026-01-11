package com.application.quickkartdeliverypartner.presentation.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryProfileScreen(
    navController: NavController,
    viewModel: DeliveryDashboardViewModel = hiltViewModel()
) {
    val earnings by viewModel.earnings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
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
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Delivery Partner Profile",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text("Name: John Doe") // TODO: Get from user data
                        Text("Phone: +91 9876543210") // TODO: Get from user data
                        Text("Email: john@example.com") // TODO: Get from user data

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Earnings",
                            style = MaterialTheme.typography.titleMedium
                        )

                        earnings?.let { earnings ->
                            Text("Today's Earnings: ₹${earnings.today}")
                            Text("Weekly Earnings: ₹${earnings.week}")
                            Text("Monthly Earnings: ₹${earnings.month}")
                            Text("Total Earnings: ₹${earnings.total}")
                        } ?: Text("Loading earnings...")

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedButton(
                            onClick = { /* TODO: Implement logout */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }
}