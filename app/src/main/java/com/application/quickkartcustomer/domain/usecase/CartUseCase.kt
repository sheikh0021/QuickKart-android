package com.application.quickkartcustomer.domain.usecase

import com.application.quickkartcustomer.domain.model.Cart
import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.domain.repository.CartRepository
import javax.inject.Inject

class CartUseCase @Inject constructor(private val cartRepository: CartRepository) {
    suspend fun getCart(): Result<Cart>{
        return cartRepository.getCart()
    }

    suspend fun addToCart(product: Product, quantity: Int = 1): Result<Cart>{
        return cartRepository.addToCart(product, quantity)
    }
    suspend fun updateQuantity(productId: Int, quantity: Int): Result<Cart> {
        return cartRepository.updateQuantity(productId, quantity)
    }
    suspend fun removeFromCart(productId: Int): Result<Cart>{
        return cartRepository.removeFromCart(productId)
    }
    suspend fun clearCart(): Result<Cart>{
        return cartRepository.clearCart()
    }
}