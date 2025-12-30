package com.application.quickkartcustomer.domain.model

data class Store(
    val id: Int,
    val name: String,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phoneNumber: String,
    val image: String?,
    val isActive: Boolean,
    val deliveryRadius: Int,
    val minimumOrder: Double,
    val deliveryFee: Double
)

