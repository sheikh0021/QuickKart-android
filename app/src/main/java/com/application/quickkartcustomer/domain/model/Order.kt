package com.application.quickkartcustomer.domain.model

data class OrderItem(
    val id: Int,
    val order: Int,
    val product: Int,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    val productName: String,
    val productImage: String
)

data class Order(
    val id: Int,
    val customer:  Int,
    val store: Int,
    val orderNumber: String,
    val status: String,
    val totalAmount: Double,
    val deliveryFee: Double,
    val deliveryAddress: String,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val paymentMethod: String,
    val paymentStatus: String,
    val items: List<OrderItem>,
    val customerName: String,
    val storeName: String
)
