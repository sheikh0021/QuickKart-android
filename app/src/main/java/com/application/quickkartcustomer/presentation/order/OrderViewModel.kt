package com.application.quickkartcustomer.presentation.order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartcustomer.domain.model.Order
import com.application.quickkartcustomer.domain.usecase.OrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderUseCase: OrderUseCase
): ViewModel() {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> =_error

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            orderUseCase.getOrderHistory().fold(
                onSuccess = { orders ->
                    _orders.value = orders
                    _isLoading.value = false
                    // Debug logging
                    println("OrderViewModel: Loaded ${orders.size} orders")
                    orders.forEach { order ->
                        println("OrderViewModel: Order ${order.id} - ${order.orderNumber} - ${order.status}")
                    }
                },
                onFailure = {exception ->
                    _error.value = exception.message ?: "Failed to load orders"
                    _isLoading.value = false
                    println("OrderViewModel: Failed to load orders: ${exception.message}")
                }
            )
        }
    }
    fun loadOrderDetails(orderId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            orderUseCase.getOrderDetails(orderId).fold(
                onSuccess = { order ->
                    _selectedOrder.value = order
                    _isLoading.value = false
                },
                onFailure = {exception ->
                    _error.value = exception.message ?: "Failed to load order details"
                    _isLoading.value = false
                }
            )
        }
    }
}