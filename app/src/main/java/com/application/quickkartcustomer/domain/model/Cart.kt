package com.application.quickkartcustomer.domain.model


data class CartItem(
    val productId: Int,
    val productName: String,
    val productImage: String?,
    val price: Double,
    val quantity: Int,
    val maxQuantity: Int = 10
) {
    val totalPrice: Double
        get() = price * quantity
}

data class Cart(
val items: List<CartItem> = emptyList()
) {
    val totalItems: Int
        get() = items.sumOf { it.quantity }

    val totalPrice: Double
        get() = items.sumOf { it.totalPrice }

    val isEmpty: Boolean
        get() = items.isEmpty()
}
