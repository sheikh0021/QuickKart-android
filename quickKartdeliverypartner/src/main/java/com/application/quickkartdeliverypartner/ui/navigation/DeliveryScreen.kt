package com.application.quickkartdeliverypartner.ui.navigation


sealed class DeliveryScreen(val route: String) {
    object Login : DeliveryScreen("delivery_login")
    object Register : DeliveryScreen("delivery_register")
    object Dashboard: DeliveryScreen("delivery_dashboard")
    object Assignments: DeliveryScreen("delivery_assignments")
    object Profile: DeliveryScreen("delivery_profile")
    object Tracking: DeliveryScreen("delivery_tracking/{orderId}") {
        fun createRoute(orderId: Int) = "delivery_tracking/$orderId"
    }
}