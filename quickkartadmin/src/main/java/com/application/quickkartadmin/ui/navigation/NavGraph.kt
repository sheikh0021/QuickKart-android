package com.application.quickkartadmin.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.application.quickkartadmin.presentation.auth.login.AdminLoginScreen
import com.application.quickkartadmin.presentation.dashboard.AdminDashboardScreen
import com.application.quickkartadmin.presentation.orders.AssignDeliveryScreen
import com.application.quickkartadmin.presentation.orders.OrderDetailScreen
import com.application.quickkartadmin.presentation.orders.OrderListScreen


@Composable
fun AdminNavGraph(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AdminScreen.Login.route
    ) {
        composable(AdminScreen.Login.route) {
            AdminLoginScreen(navController = navController)
        }
        composable(AdminScreen.Dashboard.route){
            AdminDashboardScreen(navController = navController)
        }
        composable(AdminScreen.OrderList.route){
            OrderListScreen(navController = navController)
        }
        composable(
            route = AdminScreen.AssignDelivery.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            AssignDeliveryScreen(navController = navController, orderId = orderId)
        }
        composable(
            route = AdminScreen.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            OrderDetailScreen(navController = navController, orderId = orderId)
        }
        composable(AdminScreen.DeliveryPartners.route){
            // TODO: Implement DeliveryPartnersScreen
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Delivery Partners Screen - Coming Soon")
            }
        }
        composable(AdminScreen.Reports.route){
            // TODO: Implement ReportsScreen
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Reports Screen - Coming Soon")
            }
        }
    }
}