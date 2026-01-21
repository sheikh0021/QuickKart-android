package com.application.quickkartcustomer.domain.model


data class DeliveryLocation(
    val deliveryPartner: DeliveryPartner,
    val location: Location? = null,
    val assignmentStatus: AssignmentStatus,
    val message: String? = null
)

data class DeliveryPartner(
    val name: String,
    val phone: String
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val timestamp: String
)

data class AssignmentStatus(
    val pickedUp: Boolean,
    val outForDelivery: Boolean,
    val delivered: Boolean
)
