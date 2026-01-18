package com.application.quickkartadmin.domain.model

data class Order(
    val id: Int,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val storeName: String,
    val status: String,
    val totalAmount: Double,
    val deliveryFee: Double,
    val deliveryAddress: String,
    val createdAt: String,
    val updatedAt: String,
    val orderItems: List<OrderItem>,
    val deliveryAssignment: DeliveryAssignment?
)

data class OrderItem(
    val id: Int,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)

data class DeliveryAssignment(
    val id: Int,
    val deliveryPartnerName: String,
    val deliveryPartnerPhone: String,
    val assignedAt: String,
    val pickedUpAt: String?,
    val deliveredAt: String?
)

data class DeliveryPartner(
    val id: Int,
    val username: String,
    val fullName: String,
    val completedDeliveries: Int,
    val isAvailable: Boolean
)

data class DashboardStats(
    val newOrders: Int,
    val activeDeliveries: Int,
    val totalRevenue: Double,
    val totalPartners: Int
)
