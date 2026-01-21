package com.application.quickkartcustomer.presentation.tracking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartcustomer.domain.model.DeliveryLocation
import com.application.quickkartcustomer.domain.usecase.OrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OrderTrackingViewModel @Inject constructor(
    private val orderUseCase: OrderUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val orderId: Int = checkNotNull(savedStateHandle["orderId"])

    private val _deliveryLocation = MutableStateFlow<DeliveryLocation?>(null)
    val deliveryLocation: StateFlow<DeliveryLocation?> = _deliveryLocation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean?> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking

    init {
        loadDeliveryLocation()
    }

    fun loadDeliveryLocation(){
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            orderUseCase.getDeliveryLocation(orderId).fold(
                onSuccess = {location ->
                    _deliveryLocation.value = location
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to load delivery location"
                    _isLoading.value = false
                }
            )
        }
    }
    fun refreshLocation(){
        loadDeliveryLocation()
    }
    fun startLiveTracking(){
        if (_isTracking.value) return

        _isTracking.value = true
        viewModelScope.launch {
            while (isActive && _isTracking.value){
                loadDeliveryLocation()
                delay(10000)
            }
        }
    }

    fun stopLiveTracking(){
        _isTracking.value = false
    }

    override fun onCleared() {
        super.onCleared()
        stopLiveTracking()
    }
}