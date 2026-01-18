package com.application.quickkartcustomer.data.remote.dto

import com.google.gson.annotations.SerializedName


data class AddressDto(
    val id: Int? = null,
    val street: String,
    val city: String,
    val state: String,
    @SerializedName("zip_code") val zipCode: String,
    @SerializedName("is_default") val isDefault: Boolean = false
)

data class AddressListResponseDto(
    @SerializedName("results") val addresses: List<AddressDto>,
    val count: Int,
    val next: String?,
    val previous: String?
)

data class OrderItemDto(
    val id: Int,
    @SerializedName("order") val orderId: Int,
    @SerializedName("product") val productId: Int,
    val quantity: Int,
    @SerializedName("unit_price") val unitPrice: Double,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_image") val productImage: String
)

data class OrderRequestDto(
    val items: List<OrderItemRequestDto>,
    @SerializedName("address_id") val addressId: Int,
    @SerializedName("payment_method") val paymentMethod: String = "COD",
    val notes: String? = null
)

data class OrderItemRequestDto(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    val quantity: Int,
    val price: Double,
    val total: Double
)

data class OrderListResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<OrderDto>
)

data class OrderDto(
    val id: Int,
    @SerializedName("customer") val customerId: Int,
    @SerializedName("store") val storeId: Int,
    @SerializedName("order_number") val orderNumber: String,
    val status: String,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("delivery_fee") val deliveryFee: Double,
    @SerializedName("delivery_address") val deliveryAddress: String,
    @SerializedName("delivery_latitude") val deliveryLatitude: Double?,
    @SerializedName("delivery_longitude") val deliveryLongitude: Double?,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("payment_status") val paymentStatus: String,
    val items: List<OrderItemDto>? = emptyList(),
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("store_name") val storeName: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("estimated_delivery") val estimatedDelivery: String?
)