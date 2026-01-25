package com.application.quickkartcustomer.data.repository

import com.application.quickkartcustomer.domain.model.Cart
import com.application.quickkartcustomer.domain.model.CartItem
import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.domain.repository.CartRepository


class CartRepositoryImpl : CartRepository {
    //in memory cart storage and in production we need to store in database or API
    private val cartItems = mutableListOf<CartItem>()

    override suspend fun getCart(): Result<Cart> {
        return Result.success(Cart(cartItems.toList()))
    }

    override suspend fun addToCart(product: Product, quantity: Int): Result<Cart> {
        val existingItem = cartItems.find { it.productId == product.id }

        if (existingItem != null) {
            //update quantity if item exists
            val newQuantity = minOf(existingItem.quantity + quantity, existingItem.maxQuantity)
            updateQuantity(product.id, newQuantity)
        } else {
            //add new item
            val cartItem = CartItem(
                productId = product.id,
                productName = product.name,
                productImage = product.image,
                price = product.price,
                quantity = quantity,
                maxQuantity = if (product.isAvailable) 10 else 0
            )
            cartItems.add(cartItem)
        }
        return getCart()
    }

    override suspend fun updateQuantity(productId: Int, quantity: Int): Result<Cart> {
        val itemIndex = cartItems.indexOfFirst {it.productId == productId}

        if (itemIndex != -1) {
            if (quantity <= 0) {
                cartItems.removeAt(itemIndex)
            } else {
              val updatedItem = cartItems[itemIndex].copy(quantity = quantity)
              cartItems[itemIndex] = updatedItem
            }
        }
        return getCart()
    }

    override suspend fun removeFromCart(productId: Int): Result<Cart> {
        cartItems.removeAll {it.productId == productId}
        return getCart()
    }

    override suspend fun clearCart(): Result<Cart> {
        cartItems.clear()
        return getCart()
    }
}