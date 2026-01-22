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
    val storeName: String,
    val createdAt: String
)



data class Address(
    val id: Int? = null,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isDefault: Boolean = false
) {
    val fullAddress: String
        get() = "$street, $city, $state - $zipCode "
    val hasCoordinates: Boolean
        get() = latitude != null && longitude != null
}

data class OrderRequest(
    val items: List<OrderItem>,
    val addressId: Int,
    val paymentMethod: String = "COD",
    val notes: String? = null
)

enum class OrderStatus{
    PLACED,
    PACKED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED;

    companion object {
        fun fromString(status: String): OrderStatus {
            return when (status.lowercase()){
                "placed" -> PLACED
                "packed" -> PACKED
                "out_for_delivery" -> OUT_FOR_DELIVERY
                "delivered" -> DELIVERED
                "cancelled" -> CANCELLED
                else -> PLACED
            }
        }
    }
}
