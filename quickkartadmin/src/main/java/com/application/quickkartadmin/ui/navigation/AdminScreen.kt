package com.application.quickkartadmin.ui.navigation


sealed class AdminScreen(val route: String){
    object Login : AdminScreen("login")
    object Dashboard: AdminScreen("dashboard")
    object OrderList: AdminScreen("order_list")
    object OrderDetail: AdminScreen("order_detail/{orderId}"){
        fun createRoute(orderId: Int) = "order_detail/$orderId"
    }
    object AssignDelivery : AdminScreen("assign_delivery/{orderId}"){
        fun createRoute(orderId: Int) = "assign_delivery/$orderId"
    }
    object DeliveryPartners: AdminScreen("delivery_partners")
    object Reports: AdminScreen("reports")
}