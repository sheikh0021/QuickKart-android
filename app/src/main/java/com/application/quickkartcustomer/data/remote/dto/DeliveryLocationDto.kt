package com.application.quickkartcustomer.data.remote.dto


data class DeliveryLocationDto(
    val delivery_partner: DeliveryPartnerDto? = null, // Can be null for completed deliveries
    val location: LocationDto? = null,
    val assignment_status: AssignmentStatusDto,
    val message: String? = null
)

data class DeliveryPartnerDto(
    val name: String,
    val phone: String
)

data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val timestamp: String
)

data class AssignmentStatusDto(
    val picked_up: Boolean,
    val out_for_Delivery: Boolean,
    val delivered: Boolean
)
