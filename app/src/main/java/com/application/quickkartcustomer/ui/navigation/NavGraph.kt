package com.application.quickkartcustomer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph
import androidx.navigation.NavHost
import androidx.navigation.NavHostController


sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ProductList  : Screen("product_list/{storeId}") {
        fun createRoute(storeId: Int) = "product_list/$storeId"
    }
    object ProductDetail : Screen("product_detail/{productId}"){
        fun createRoute(productId: Int) = "product_details/$productId"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object OrderList  : Screen("order_list")
    object OrderDetail  : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: Int) = "order_detail/$orderId"
    }
    object Tracking  : Screen("tracking/{orderId}") {
        fun createRoute(orderId: Int) = "tracking/$orderId"
    }
    object Profile : Screen("profile")
}

@Composable
fun NavGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ){
        composable(Screen.Login.route){
            LoginScreen(navController)
        }
    }
}