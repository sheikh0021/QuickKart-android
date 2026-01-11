package com.application.quickkartdeliverypartner.presentation.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartdeliverypartner.domain.model.DeliveryAssignment
import com.application.quickkartdeliverypartner.domain.model.DeliveryEarnings
import com.application.quickkartdeliverypartner.domain.repository.DeliveryRepository
import com.application.quickkartdeliverypartner.domain.usecase.DeliveryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryDashboardViewModel @Inject constructor(
    private val deliveryUseCase: DeliveryUseCase
) : ViewModel() {
    private val _assignments = MutableStateFlow<List<DeliveryAssignment>>(emptyList())
    val assignments: StateFlow<List<DeliveryAssignment>> = _assignments

    private val _earnings = MutableStateFlow(DeliveryEarnings())
    val earnings: StateFlow<DeliveryEarnings> = _earnings

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadDashboardData()
    }

    fun loadDashboardData(){
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                deliveryUseCase.getMyAssignments().fold(
                    onSuccess = {assignments ->
                        _assignments.value = assignments
                    },
                    onFailure = {exception ->
                        _error.value = exception.message ?: "Failed to load assignments"
                    }
                )
                deliveryUseCase.getEarnings().fold(
                    onSuccess = {earnings ->
                        _earnings.value = earnings
                    },
                    onFailure = {exception ->
                        _earnings.value = DeliveryEarnings()
                    }
                )
            } catch (e: Exception){
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun clearError(){
        _error.value = null
    }
}