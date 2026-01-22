package com.application.quickkartdeliverypartner.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.application.quickkartdeliverypartner.core.util.PreferencesManager
import com.application.quickkartdeliverypartner.domain.model.DeliveryAssignment
import com.application.quickkartdeliverypartner.presentation.auth.login.DeliveryLoginScreen
import com.application.quickkartdeliverypartner.presentation.auth.login.DeliveryRegisterScreen
import com.application.quickkartdeliverypartner.presentation.delivery.DeliveryAssignmentsScreen
import com.application.quickkartdeliverypartner.presentation.delivery.DeliveryDashboardScreen
import com.application.quickkartdeliverypartner.presentation.delivery.DeliveryProfileScreen
import com.application.quickkartdeliverypartner.presentation.delivery.DeliveryTrackingMapScreen
import com.application.quickkartdeliverypartner.presentation.delivery.DeliveryTrackingScreen
import com.application.quickkartdeliverypartner.presentation.chat.ChatScreen

@Composable
fun DeliveryPartnerNavGraph(
    navController: NavHostController
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    
    // Check login state on app start and navigate accordingly
    LaunchedEffect(Unit) {
        if (preferencesManager.isLoggedIn() && preferencesManager.isTokenValid()) {
            // Check if user is a delivery partner
            val userType = preferencesManager.getUserType()
            if (userType == "delivery_partner") {
                // Initialize RetrofitClient with saved token for authenticated API calls
                com.application.quickkartdeliverypartner.core.network.RetrofitClient.init(preferencesManager)
                
                // User is logged in as delivery partner, navigate to assignments
                navController.navigate(DeliveryScreen.Assignments.route) {
                    // Clear back stack to prevent going back to login
                    popUpTo(DeliveryScreen.Login.route) { inclusive = true }
                }
            } else {
                // User type mismatch, clear data and show login
                preferencesManager.clearData()
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = DeliveryScreen.Login.route
    ) {
        composable(DeliveryScreen.Login.route) {
            DeliveryLoginScreen(navController)
        }
        composable(DeliveryScreen.Register.route) {
            DeliveryRegisterScreen(navController)
        }
        composable(DeliveryScreen.Assignments.route) {
            DeliveryAssignmentsScreen(navController = navController)
        }
        composable(
            route = DeliveryScreen.TrackingMap.route,
            arguments = listOf(navArgument("orderId"){type = NavType.IntType})
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            val savedStateHandle = backStackEntry.savedStateHandle

            // Try to get assignment from SavedStateHandle (multiple sources)
            // First try: current backStackEntry (in case it was set here)
            var assignment = savedStateHandle.get<DeliveryAssignment>("delivery_assignment")
            
            // Second try: previousBackStackEntry (where it was actually set)
            if (assignment == null) {
                assignment = navController.previousBackStackEntry?.savedStateHandle?.get<DeliveryAssignment>("delivery_assignment")
                // Copy to current SavedStateHandle for persistence across configuration changes
                if (assignment != null) {
                    savedStateHandle["delivery_assignment"] = assignment
                }
            }

            if (assignment != null){
                DeliveryTrackingMapScreen(
                    navController = navController,
                    orderId = orderId,
                    assignment = assignment
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Assignment data not found.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "Order ID: $orderId",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(onClick = { navController.navigateUp() }) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
        composable(
            route = DeliveryScreen.Chat.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            val savedStateHandle = backStackEntry.savedStateHandle
            savedStateHandle["orderId"] = orderId
            ChatScreen(
                orderId = orderId,
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}