package com.application.quickkartdeliverypartner.domain.model


data class DeliveryAssignment(
    val id: Int,
    val orderId: Int,
    val orderNumber: String,
    val orderStatus: String,
    val totalAmount: Double,
    val deliveryAddress: String,
    val customerName: String,
    val customerPhone: String,
    val assignedAt: String,
    val pickedUpAt: String? = null,
    val deliveredAt: String? = null,
    val estimatedDeliveryTime: String? = null
)

data class DeliveryEarnings(
    val today: Double = 0.0,
    val week: Double = 0.0,
    val month: Double = 0.0,
    val total: Double = 0.0
)

data class DeliveryLocationUpdate(
    val orderId: Int,
    val latitude: Double,
    val longitude: Double
)

data class DeliveryStatusUpdate(
    val assignmentId: Int,
    val status: String
)

data class AuthResponse(
    val user: User,
    val tokens: Tokens
)

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val userType: String,
    val profileImage: String? = null
)

data class Tokens(
    val access: String,
    val refresh: String
)