package com.application.quickkartadmin.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartadmin.domain.model.DeliveryPartner
import com.application.quickkartadmin.domain.model.Order
import com.application.quickkartadmin.domain.usecase.AdminUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AssignDeliveryViewModel @Inject constructor(
    private val adminUseCase: AdminUseCase
): ViewModel() {
    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder: StateFlow<Order?> = _currentOrder

    private val _deliveryPartners = MutableStateFlow<List<DeliveryPartner>>(emptyList())
    val deliveryPartners: StateFlow<List<DeliveryPartner>> = _deliveryPartners

    private val _isLoading = MutableStateFlow(false)
    val isLoading : StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?>  = _error

    fun loadOrder(orderId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            adminUseCase.getOrderDetails(orderId).fold(
                onSuccess = { order ->
                    _currentOrder.value = order
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to load order details"
                    _isLoading.value = false
                }
            )
        }
        loadDeliveryPartners()
    }
    private fun loadDeliveryPartners(){
        viewModelScope.launch {
            adminUseCase.getDeliveryPartners().fold(
                onSuccess = { partners ->
                    _deliveryPartners.value = partners

                },
                onFailure = {exception ->
                    _error.value = exception.message ?: "Failed to load delivery partners"
                }
            )
        }
    }

    fun assignDeliveryPartner(orderId: Int, deliveryPartnerId: Int){
        viewModelScope.launch {
            _isLoading.value = true

            adminUseCase.assignDeliveryPartner(orderId, deliveryPartnerId).fold(
                onSuccess = {
                    _isLoading.value = false
                    // Assignment successful - could refresh data or navigate back
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to assign delivery partner"
                    _isLoading.value = false

                }
            )
        }
    }
}