package com.application.quickkartcustomer.presentation.order

import android.widget.Space
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.application.quickkartcustomer.domain.model.Order
import com.application.quickkartcustomer.ui.navigation.Screen
import com.application.quickkartcustomer.ui.navigation.NavigationStateManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: Int,
    navigationStateManager: NavigationStateManager,
    viewModel: OrderViewModel = hiltViewModel()
){
    val order by viewModel.selectedOrder.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) {
        viewModel.loadOrderDetails(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details", fontSize = 18.sp, fontWeight = FontWeight.Bold ) },
                navigationIcon ={
                    IconButton(onClick = {navController.navigateUp()}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading ->{
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                order == null -> {
                    Text(
                        text = "Order Not Found",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }
                else ->{
                    OrderDetailContent(order = order!!, navController = navController)
                }
            }
        }
    }

}

@Composable
fun OrderDetailContent(order: Order, navController: NavController ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //Order status
        item {
            Card(modifier = Modifier.fillParentMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Order Status",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = order.status.replace("_", " ").uppercase(),
                        fontSize = 18.sp,
                        color = when (order.status.lowercase()) {
                            "delivered" -> Color(0xFF4CAF50)
                            "out_for_delivery" -> Color(0xFFFF9800)
                            "packed" -> Color(0xFF2196F3)
                            "confirmed" -> Color(0xFFFF9800)
                            "placed" -> Color(0xFF2196F3)
                            else -> Color.Gray
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        // order items
        item {
            Text(
                text = "Order Items",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        items(order.items) { item ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.productName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${item.unitPrice} * ${item.quantity}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = "${item.totalPrice}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        //order summary
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Order Summary",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Subtotal: "
                        )
                        Text(text = "${order.deliveryFee}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total: ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(text = "${order.totalAmount + order.deliveryFee}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
        }
        //delivery address
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Delivery Address",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = order.deliveryAddress)
                }
            }
        }
        //track order button
        if (order.status in listOf("placed", "confirmed", "packed", "out_for_delivery", "confirmed", "packed", "out_for_delivery", "PLACED", "PACKED", "OUT_FOR_DELIVERY", "CONFIRMED", "PACKED", "OUT_FOR_DELIVERY").map { it.lowercase() }){
            item {
                Button(onClick = {
                    navController.navigate(Screen.OrderTracking.route)
                },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Track Order")
                }
            }
        }
    }
}