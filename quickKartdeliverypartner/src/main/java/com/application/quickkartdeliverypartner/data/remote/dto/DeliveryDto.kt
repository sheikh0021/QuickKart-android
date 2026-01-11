package com.application.quickkartdeliverypartner.data.remote.dto


import com.google.gson.annotations.SerializedName

data class PaginatedResponseDto<T>(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<T>
)

data class DeliveryAssignmentDto (
    @SerializedName("id") val id: Int,
    @SerializedName("order") val order: Int,
    @SerializedName("assigned_at") val assignedAt: String,
    @SerializedName("picked_up_at") val pickedUpAt: String? = null,
    @SerializedName("delivered_at") val deliveredAt: String? = null,
    @SerializedName("estimated_delivery_time") val estimatedDeliveryTime: String? = null,
    @SerializedName("order_details") val orderDetails: OrderDetailsDto,
    @SerializedName("customer_details") val customerDetails: CustomerDetailsDto
)

data class OrderDetailsDto(
    @SerializedName("order_number") val orderNumber: String,
    @SerializedName("status") val status: String,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("delivery_address") val deliveryAddress: String
)

data class CustomerDetailsDto(
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String
)

data class LocationUpdateDto(
    @SerializedName("order") val orderId: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)

data class StatusUpdateDto(
    @SerializedName("status") val status: String
)

data class ApiResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("status") val status: String? = null
)