package com.application.quickkartadmin.data.remote.dto


data class AdminDto(
    val access: String,
    val refresh: String,
    val user: AdminUserDto
)

data class AdminUserDto(
    val id: Int,
    val username: String,
    val full_name: String,
    val user_type: String
)

data class OrderDto(
    val id: Int,
    val order_number: String,
    val customer_name: String,
    val customer_phone: String,
    val store_name: String,
    val status: String,
    val total_amount: Double,
    val delivery_fee: Double,
    val delivery_address: String,
    val created_at: String,
    val updated_at: String,
    val order_items: List<OrderItemDto>,
    val delivery_assignment: DeliveryAssignmentDto?
)

data class OrderItemDto(
    val id: Int,
    val product_name: String,
    val quantity: Int,
    val unit_price: Double,
    val total_price: Double
)

data class DeliveryAssignmentDto(
    val id: Int,
    val delivery_partner_name: String,
    val delivery_partner_phone: String,
    val assigned_at: String,
    val picked_up_at: String?,
    val delivered_at: String?
)

data class DeliveryPartnerDto(
    val id: Int,
    val username: String,
    val full_name: String,
    val completed_deliveries: Int,
    val is_available: Boolean
)

data class DashboardStatsDto(
    val new_orders: Int,
    val active_deliveries: Int,
    val total_revenue: Double,
    val total_partners: Int
)
