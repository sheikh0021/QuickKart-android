package com.application.quickkartcustomer.ui.navigation

import CheckoutScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.application.quickkartcustomer.ui.navigation.NavigationStateManager
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
import com.application.quickkartcustomer.presentation.address.MapAddressPickerScreen
import com.application.quickkartcustomer.presentation.tracking.OrderTrackingMapScreen
import com.application.quickkartcustomer.presentation.tracking.OrderTrackingScreen
import com.application.quickkartcustomer.presentation.chat.ChatScreen
import com.google.android.gms.maps.model.LatLng


sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")

    object Categories  : Screen("categories")
    object ProductList  : Screen("product_list/{storeId}") {
        fun createRoute(storeId: Int) = "product_list/$storeId"
    }
    object ProductListByCategory : Screen("product_list_category/{categoryId}") {
        fun createRoute(categoryId: Int) = "product_list_category/$categoryId"
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
    object MapAddressPicker : Screen("map_address_picker")
    object Chat : Screen("chat/{orderId}") {
        fun createRoute(orderId: Int) = "chat/$orderId"
    }
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
fun NavGraph(navController: NavHostController,
             navigationStateManager: NavigationStateManager){
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }

    LaunchedEffect(Unit) {
        if (preferencesManager.isLoggedIn()) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ){
        composable(Screen.Login.route){
            LoginScreen(navController, navigationStateManager)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController, navigationStateManager)
        }
        composable(Screen.Home.route){
            HomeScreen(
                navController = navController,
                navigationStateManager = navigationStateManager
            )
        }
        composable(Screen.Categories.route) {
            com.application.quickkartcustomer.presentation.categories.CategoriesScreen(
                navController = navController,
                navigationStateManager = navigationStateManager
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                navigationStateManager = navigationStateManager
            )
        }
        composable(Screen.OrderTracking.route) {
            OrderTrackingScreen(
                navController = navController,
                navigationStateManager = navigationStateManager
            )
        }
        composable(
            route = Screen.ProductList.route,
            arguments = listOf(
                navArgument("storeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getInt("storeId") ?: 0
            ProductListScreen(navController, navigationStateManager)
        }
        composable(
            route = Screen.ProductListByCategory.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
            ProductListScreen(navController, navigationStateManager)
        }
        composable(Screen.Cart.route) {
            CartScreen(navController, navigationStateManager)
        }
        composable(Screen.Checkout.route) {
            CheckoutScreen(navController, navigationStateManager)
        }
        composable(Screen.OrderList.route) {
            OrderListScreen(
                navController = navController,
                navigationStateManager = navigationStateManager
            )
        }
        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            OrderDetailScreen(
                navController = navController,
                orderId = orderId,
                navigationStateManager = navigationStateManager
            )
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
                onBackClick = {navController.navigateUp()},
                navController = navController,
                navigationStateManager = navigationStateManager
            )
        }
        composable(Screen.MapAddressPicker.route) {
            MapAddressPickerScreen(
                onLocationSelected = { latLng, street, city, state, zipcode ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_location", latLng)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_street", street)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_city", city)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_state", state)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_zipcode", zipcode)

                    navController.navigateUp()
                },
                onBackClick = {navController.navigateUp()},
                navigationStateManager = navigationStateManager
            )
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            val savedStateHandle = backStackEntry.savedStateHandle
            savedStateHandle["orderId"] = orderId
            ChatScreen(
                orderId = orderId,
                deliveryPartnerId = null,
                deliveryPartnerName = null,
                onBackClick = { navController.navigateUp() },
                navigationStateManager = navigationStateManager
            )
        }
    }
}
