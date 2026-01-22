package com.application.quickkartcustomer.presentation.checkout


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartcustomer.domain.model.*
import com.application.quickkartcustomer.domain.usecase.CartUseCase
import com.application.quickkartcustomer.domain.usecase.OrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartUseCase: CartUseCase,
    private val orderUseCase: OrderUseCase
) : ViewModel() {

    private val _cart = MutableStateFlow<Cart>(Cart())
    val cart: StateFlow<Cart> = _cart

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses

    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress: StateFlow<Address?> = _selectedAddress

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _orderPlaced = MutableStateFlow<Order?>(null)
    val orderPlaced: StateFlow<Order?> = _orderPlaced

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadCheckoutData()
    }

    fun loadCheckoutData() {
        viewModelScope.launch {
            _isLoading.value = true

            // Load cart
            cartUseCase.getCart().fold(
                onSuccess = { cart ->
                    _cart.value = cart
                },
                onFailure = { exception ->
                    _error.value = "Failed to load cart: ${exception.message}"
                }
            )

            // Load addresses
            orderUseCase.getAddresses().fold(
                onSuccess = { addresses ->
                    _addresses.value = addresses
                    val defaultAddress = addresses.find { it.isDefault }
                    val addressToSelect = defaultAddress ?: addresses.firstOrNull()
                    _selectedAddress.value = addressToSelect
                },
                onFailure = { exception ->
                    _error.value = "Failed to load addresses: ${exception.message}"
                }
            )

            _isLoading.value = false
        }
    }

    fun selectAddress(address: Address) {
        _selectedAddress.value = address
    }

    fun placeOrder(notes: String? = null) {
        val cart = _cart.value
        val selectedAddress = _selectedAddress.value

        if (cart.isEmpty) {
            _error.value = "Cart is empty"
            return
        }

        if (selectedAddress == null) {
            _error.value = "Please select a delivery address"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val orderItems = cart.items.map { cartItem ->
                OrderItem(
                    id = 0,
                    order = 0,
                    product = cartItem.productId,
                    unitPrice = cartItem.price,
                    totalPrice = cartItem.totalPrice,
                    productName = cartItem.productName,
                    productImage = cartItem.productImage ?: "",
                    quantity = cartItem.quantity
                )
            }

            val orderRequest = OrderRequest(
                items = orderItems,
                addressId = selectedAddress.id!!,
                paymentMethod = "COD",
                notes = notes
            )

            orderUseCase.createOrder(orderRequest).fold(
                onSuccess = { order ->
                    _orderPlaced.value = order
                    // Clear cart after successful order
                    viewModelScope.launch {
                        cartUseCase.clearCart()
                    }
                },
                onFailure = { exception ->
                    _error.value = "Failed to place order: ${exception.message}"
                }
            )

            _isLoading.value = false
        }
    }

    fun addNewAddress(street: String, city: String, state: String, zipCode: String) {
        viewModelScope.launch {
            val newAddress = Address(
                street = street,
                city = city,
                state = state,
                zipCode = zipCode,
                isDefault = _addresses.value?.isEmpty() == true
            )

            orderUseCase.addAddress(newAddress).fold(
                onSuccess = { addedAddress ->
                    val currentAddresses = _addresses.value ?: emptyList()
                    _addresses.value = currentAddresses + addedAddress
                    _selectedAddress.value = addedAddress
                },
                onFailure = { exception ->
                    _error.value = "Failed to add address: ${exception.message}"
                }
            )
        }
    }

    fun addNewAddressWithCoordinates(
        street: String,
        city: String,
        state: String,
        zipCode: String,
        latitude: Double,
        longitude: Double
    ){
        viewModelScope.launch {
            val newAddress = Address(
                street = street,
                city = city,
                state = state,
                zipCode = zipCode,
                latitude = latitude,
                longitude = longitude,
                isDefault = _addresses.value?.isEmpty() == true
            )
            orderUseCase.addAddress(newAddress).fold(
                onSuccess = {addedAddress ->
                    val currentAddresses = _addresses.value ?: emptyList()
                    _addresses.value = currentAddresses + addedAddress
                    _selectedAddress.value = addedAddress
                },
                onFailure = {exception ->
                    _error.value = "Failed to add address: ${exception.message}"
                }
            )
        }
    }
}