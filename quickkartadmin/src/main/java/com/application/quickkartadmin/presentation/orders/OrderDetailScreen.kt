package com.application.quickkartadmin.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.quickkartadmin.ui.navigation.AdminScreen
import com.application.quickkartadmin.ui.theme.AdminAccent
import com.application.quickkartadmin.ui.theme.AdminPrimary
import com.application.quickkartadmin.ui.theme.StatusConfirmed
import com.application.quickkartadmin.ui.theme.StatusDelivered
import com.application.quickkartadmin.ui.theme.StatusOutForDelivery
import com.application.quickkartadmin.ui.theme.StatusPacked
import com.application.quickkartadmin.ui.theme.StatusPlaced

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: Int,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val order by viewModel.order.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Load order details when screen opens
    androidx.compose.runtime.LaunchedEffect(orderId) {
        viewModel.loadOrderDetails(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Order Details",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AdminPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            order?.let { orderDetails ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFF5F5F5))
                        .padding(16.dp)
                ) {
                    // Order Info Card
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
                                        "Order #${orderDetails.orderNumber}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF212121)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Customer: ${orderDetails.customerName}",
                                        fontSize = 16.sp,
                                        color = Color(0xFF424242)
                                    )
                                    Text(
                                        "Store: ${orderDetails.storeName}",
                                        fontSize = 14.sp,
                                        color = Color(0xFF757575)
                                    )
                                    Text(
                                        "Amount: ₹${orderDetails.totalAmount}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AdminPrimary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        orderDetails.status.replace("_", " ").uppercase(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (orderDetails.status) {
                                            "placed" -> StatusPlaced
                                            "confirmed" -> StatusConfirmed
                                            "packed" -> StatusPacked
                                            "out_for_delivery" -> StatusOutForDelivery
                                            "delivered" -> StatusDelivered
                                            else -> Color(0xFF757575)
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        orderDetails.createdAt,
                                        fontSize = 12.sp,
                                        color = Color(0xFF757575)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Delivery Address Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Delivery Address",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                orderDetails.deliveryAddress,
                                fontSize = 14.sp,
                                color = Color(0xFF424242)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    if (orderDetails.status == "placed" || orderDetails.status == "confirmed") {
                        if (orderDetails.deliveryAssignment == null) {
                            // Show assign delivery partner button
                            Button(
                                onClick = {
                                    navController.navigate(AdminScreen.AssignDelivery.createRoute(orderDetails.id))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AdminPrimary
                                )
                            ) {
                                Text(
                                    "Assign Delivery Partner",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            // Show delivery partner info
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "Delivery Partner Assigned",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AdminAccent
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        orderDetails.deliveryAssignment.deliveryPartnerName,
                                        fontSize = 14.sp,
                                        color = Color(0xFF424242)
                                    )
                                }
                            }
                        }
                    }
                }
            } ?: run {
                // Order not found
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Order not found",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}