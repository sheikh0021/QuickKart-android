package com.application.quickkartcustomer.ui.navigation

import CheckoutScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.application.quickkartcustomer.presentation.auth.login.LoginScreen
import com.application.quickkartcustomer.presentation.auth.register.RegisterScreen
import com.application.quickkartcustomer.presentation.cart.CartScreen
import com.application.quickkartcustomer.presentation.home.HomeScreen
import com.application.quickkartcustomer.presentation.order.OrderDetailScreen
import com.application.quickkartcustomer.presentation.order.OrderListScreen
import com.application.quickkartcustomer.presentation.product.ProductListScreen
import com.application.quickkartcustomer.presentation.profile.ProfileScreen


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
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.Home.route){
            HomeScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(
            route = Screen.ProductList.route,
            arguments = listOf(
                navArgument("storeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            ProductListScreen(navController)
        }
        composable(Screen.Cart.route) {
            CartScreen(navController)
        }
        composable(Screen.Checkout.route) {
            CheckoutScreen(navController)
        }
        composable(Screen.OrderList.route) {
            OrderListScreen(navController)
        }
        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            OrderDetailScreen(navController, orderId)
        }
    }
}

@Composable
fun CustomerNavGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ){
        composable(Screen.Login.route){
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
    }
}
