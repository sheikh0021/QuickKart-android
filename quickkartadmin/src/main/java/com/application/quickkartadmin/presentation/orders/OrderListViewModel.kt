package com.application.quickkartadmin.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartadmin.domain.model.Order
import com.application.quickkartadmin.domain.usecase.AdminUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val adminUseCase: AdminUseCase
): ViewModel() {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus: StateFlow<String?> = _selectedStatus

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadOrders()
    }

    fun loadOrders(){
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            adminUseCase.getAllOrders(_selectedStatus.value).fold(
                onSuccess = {orders ->
                    _orders.value = orders
                    _isLoading.value = false
                },
                onFailure = {exception ->
                    _error.value = exception.message ?: "Failed to load orders"
                    _isLoading.value = false
                }
            )
        }
    }
    fun onStatusSelected(status: String?){
        _selectedStatus.value = status
        loadOrders()
    }

}