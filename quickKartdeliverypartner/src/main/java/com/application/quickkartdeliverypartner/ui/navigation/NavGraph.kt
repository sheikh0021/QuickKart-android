package com.application.quickkartdeliverypartner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.application.quickkartdeliverypartner.presentation.auth.login.DeliveryLoginScreen
import com.application.quickkartdeliverypartner.presentation.auth.login.DeliveryRegisterScreen
import com.application.quickkartdeliverypartner.presentation.delivery.DeliveryAssignmentsScreen
import com.application.quickkartdeliverypartner.presentation.delivery.DeliveryDashboardScreen
import com.application.quickkartdeliverypartner.presentation.delivery.DeliveryProfileScreen
import com.application.quickkartdeliverypartner.presentation.delivery.DeliveryTrackingScreen

@Composable
fun DeliveryPartnerNavGraph(
    navController: NavHostController
) {
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
            DeliveryAssignmentsScreen()
        }
    }
}