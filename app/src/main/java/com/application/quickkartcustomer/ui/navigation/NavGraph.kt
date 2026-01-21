package com.application.quickkartcustomer.ui.navigation

import CheckoutScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.application.quickkartcustomer.core.util.PreferencesManager
import com.application.quickkartcustomer.presentation.auth.login.LoginScreen
import com.application.quickkartcustomer.presentation.auth.register.RegisterScreen
import com.application.quickkartcustomer.presentation.cart.CartScreen
import com.application.quickkartcustomer.presentation.home.HomeScreen
import com.application.quickkartcustomer.presentation.order.OrderDetailScreen
import com.application.quickkartcustomer.presentation.order.OrderListScreen
import com.application.quickkartcustomer.presentation.product.ProductListScreen
import com.application.quickkartcustomer.presentation.profile.ProfileScreen
import androidx.compose.runtime.getValue
import com.application.quickkartcustomer.presentation.tracking.OrderTrackingMapScreen
import com.application.quickkartcustomer.presentation.tracking.OrderTrackingScreen


sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")

    object Categories  : Screen("categories")
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
    object OrderTrackingMap : Screen("order_tracking_map/{orderId}"){
        fun createRoute(orderId: Int) = "order_tracking_map/$orderId"
    }
    object OrderTracking : Screen("order_tracking")
    object Profile : Screen("profile")
}

@Composable
fun getCurrentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

fun getBottomNavRoute(route: String?): String {
    return when {
        route == null -> Screen.Home.route
        route == Screen.Home.route -> Screen.Home.route
        route == Screen.Categories.route -> Screen.Categories.route
        route == Screen.OrderTracking.route -> Screen.OrderTracking.route
        route == Screen.Profile.route -> Screen.Profile.route
        route.startsWith(Screen.ProductList.route) -> Screen.Home.route
        route.startsWith(Screen.Cart.route) -> Screen.Home.route
        route.startsWith(Screen.OrderList.route) -> Screen.OrderTracking.route
        route.startsWith(Screen.OrderDetail.route) -> Screen.OrderTracking.route
        else -> Screen.Home.route
    }
}

@Composable
fun NavGraph(navController: NavHostController){
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    
    // Check login state on app start and navigate accordingly
    LaunchedEffect(Unit) {
        if (preferencesManager.isLoggedIn()) {
            // User is logged in, navigate to home screen
            navController.navigate(Screen.Home.route) {
                // Clear back stack to prevent going back to login
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }
    
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
        composable(Screen.Categories.route) {
            com.application.quickkartcustomer.presentation.categories.CategoriesScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(Screen.OrderTracking.route) {
            OrderTrackingScreen(navController)
        }
        composable(
            route = Screen.ProductList.route,
            arguments = listOf(
                navArgument("storeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getInt("storeId") ?: 0
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
        composable(
            route = Screen.OrderTrackingMap.route,
            arguments = listOf(
                navArgument("orderId"){type = NavType.IntType}
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            OrderTrackingMapScreen(
                orderId = orderId,
                onBackClick = {navController.navigateUp()}
            )
        }
    }
}
