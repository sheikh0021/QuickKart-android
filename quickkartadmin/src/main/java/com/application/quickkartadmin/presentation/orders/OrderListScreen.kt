package com.application.quickkartadmin.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.application.quickkartadmin.domain.model.Order
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
fun OrderListScreen(
    navController: NavController,
    viewModel: OrderListViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()

    val statusOptions = listOf(
        "All" to null,
        "Placed" to "placed",
        "Confirmed" to "confirmed",
        "Packed" to "packed",
        "Out for Delivery" to "out_for_delivery",
        "Delivered" to "delivered",
        "Cancelled" to "cancelled"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Order Management",
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
                },
                actions = {
                    IconButton(onClick = { viewModel.loadOrders() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Status Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = statusOptions.indexOfFirst { it.second == selectedStatus },
                containerColor = Color.White,
                contentColor = AdminPrimary,
                edgePadding = 16.dp
            ) {
                statusOptions.forEach { (label, status) ->
                    Tab(
                        selected = selectedStatus == status,
                        onClick = { viewModel.onStatusSelected(status) },
                        text = { Text(label) }
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (orders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "No orders",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No orders found",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        OrderCard(
                            order = order,
                            onClick = {
                                navController.navigate(AdminScreen.OrderDetail.createRoute(order.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                        "Order #${order.orderNumber}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Text(
                        order.customerName,
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                    Text(
                        order.storeName,
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        order.status.replace("_", " ").uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (order.status) {
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
                        "₹${order.totalAmount}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                order.deliveryAddress,
                fontSize = 12.sp,
                color = Color(0xFF757575),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    order.createdAt,
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )

                if (order.deliveryAssignment != null) {
                    Text(
                        "Assigned to ${order.deliveryAssignment.deliveryPartnerName}",
                        fontSize = 12.sp,
                        color = AdminAccent,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}