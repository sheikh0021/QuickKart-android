package com.application.quickkartdeliverypartner.presentation.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartdeliverypartner.domain.model.DeliveryAssignment
import com.application.quickkartdeliverypartner.domain.model.DeliveryLocationUpdate
import com.application.quickkartdeliverypartner.domain.usecase.DeliveryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryTrackingViewModel @Inject constructor(
    private val deliveryUseCase: DeliveryUseCase
): ViewModel() {

    private val _assignment = MutableStateFlow<DeliveryAssignment?>(null)
    val assignment: StateFlow<DeliveryAssignment?> = _assignment

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAssignment(orderId: Int, assignments: List<DeliveryAssignment>){
        _assignment.value = assignments.find { it.orderId == orderId }
    }

    fun updateLocation(orderId: Int, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val locationUpdate = DeliveryLocationUpdate(orderId, latitude, longitude)
                deliveryUseCase.updateLocation(locationUpdate).fold(
                    onSuccess = {success ->
                        if (!success) {
                            _error.value = "Failed to update location"
                        }
                    },
                    onFailure = {exception ->
                        _error.value = exception.message ?: "Failed to update location"
                    }
                )
            }catch (e: Exception){
                _error.value = e.message ?: "Unknown error occurred"
            }
        }
    }
    fun clearError(){
        _error.value = null
    }

}