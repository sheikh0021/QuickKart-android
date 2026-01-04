package com.application.quickkartcustomer.domain.repository

import com.application.quickkartcustomer.domain.model.Cart
import com.application.quickkartcustomer.domain.model.Product

interface CartRepository {
    suspend fun getCart(): Result<Cart>
    suspend fun addToCart(product: Product, quantity: Int = 1): Result<Cart>
    suspend fun updateQuantity(productId: Int, quantity: Int): Result<Cart>
    suspend fun removeFromCart(productId: Int): Result<Cart>
    suspend fun clearCart(): Result<Cart>
}