package com.application.quickkartcustomer.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartcustomer.domain.model.Cart
import com.application.quickkartcustomer.domain.model.Product
import com.application.quickkartcustomer.domain.usecase.CartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartUseCase: CartUseCase
): ViewModel() {
    private val _cart = MutableStateFlow<Cart>(Cart())
    val cart: StateFlow<Cart> = _cart

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadCart()
    }

    fun loadCart(){
        viewModelScope.launch {
            _isLoading.value = true
            cartUseCase.getCart().fold(
                onSuccess = {cart ->
                    _cart.value = cart
                    _isLoading.value = false
                },
                onFailure = {exception ->
                    _error.value  = exception.message ?: "Failed to load cart"
                    _isLoading.value = false
                }
            )
        }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            cartUseCase.addToCart(product, quantity).fold(
                onSuccess = {updatedCart ->
                    _cart.value = updatedCart
                },
                onFailure = {exception ->
                    _error.value = exception.message ?: "Failed to add item to cart"
                }
            )
        }
    }
    fun updateQuantity(productId: Int, quantity: Int){
        viewModelScope.launch {
            cartUseCase.updateQuantity(productId, quantity).fold(
                onSuccess = { updatedCart ->
                    _cart.value = updatedCart
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to update the quantity"
                }
            )
        }
    }
    fun removeFromCart(productId: Int){
        viewModelScope.launch {
            cartUseCase.removeFromCart(productId).fold(
                onSuccess = {updatedCart ->
                    _cart.value = updatedCart
                },
                onFailure = {exception ->
                    _error.value = exception.message ?: "Failed to remove item"
                }
            )
        }
    }
    fun clearCart() {
        viewModelScope.launch {
            cartUseCase.clearCart().fold(
                onSuccess = {updatedCart ->
                    _cart.value = updatedCart
                },
                onFailure = {exception ->
                    _error.value = exception.message ?: "Failed to clear cart"
                }
            )
        }
    }
}