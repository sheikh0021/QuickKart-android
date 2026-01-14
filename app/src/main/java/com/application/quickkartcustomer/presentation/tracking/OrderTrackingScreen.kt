package com.application.quickkartcustomer.presentation.tracking

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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.application.quickkartcustomer.presentation.order.OrderViewModel
import com.application.quickkartcustomer.ui.components.QuickKartBottomNavigation
import com.application.quickkartcustomer.ui.navigation.Screen
import com.application.quickkartcustomer.ui.navigation.getBottomNavRoute
import com.application.quickkartcustomer.ui.theme.DarkBlue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    navController: NavController,
    viewModel: OrderViewModel = hiltViewModel()
){
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val navHostController = navController as NavHostController
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomNavRoute = getBottomNavRoute(currentRoute)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Order Tracking",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue
                ),
                navigationIcon = {
                    IconButton(onClick = {navController.navigateUp()}) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                            )
                    }
                }
            )
        },
        bottomBar = {
            QuickKartBottomNavigation(
                currentRoute = bottomNavRoute,
                onItemClick = {route ->
                    when (route) {
                        Screen.Home.route -> {
                            navController.navigate(Screen.Home.route){
                                popUpTo(Screen.Home.route) {inclusive = false}
                            }
                        }
                        Screen.Categories.route -> {
                            navController.navigate(Screen.Categories.route) {
                                popUpTo(Screen.Home.route) {inclusive = false}
                            }
                        }
                        Screen.Profile.route -> {
                            navController.navigate(Screen.Profile.route){
                                popUpTo(Screen.Home.route) {inclusive = true}
                            }
                        }
                        "more" -> {
                            //its there already no need to navigate now.
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF5F5F5))
        ){
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                orders.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "No orders",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No Active Orders",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Your Order tracking will appear here",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedButton(
                            onClick = {navController.navigate(Screen.OrderList.route)}
                        ) {
                            Text("View Order History")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                "Active Orders",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )
                        }
                        items(orders.filter { it.status in listOf("confirmed", "packed",
                            "out_for_delivery") }) {order ->
                            OrderTrackingCard(
                                order = order,
                                onClick = {
                                    navController.navigate(Screen.OrderDetail.createRoute(order.id))
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick ={navController.navigate(Screen.OrderList.route)},
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View All Orders")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTrackingCard(
    order: com.application.quickkartcustomer.domain.model.Order,
    onClick: () -> Unit
){
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick=onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Order #${order.orderNumber}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        order.storeName,
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        order.status.replace("_", " ").uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (order.status) {
                            "confirmed" -> Color(0xFFFF9800)
                            "packed" -> Color(0xFF2196F3)
                            "out_for_delivery" -> Color(0xFF4CAF50)
                            else -> Color(0xFF757575)
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${order.totalAmount}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Estimated Delivery",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                    Text(
                        "30-35 minutes",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF212121)
                    )
                }
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "View details",
                    tint = Color(0xFF757575)
                )
            }
        }
    }
}