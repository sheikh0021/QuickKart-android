package com.application.quickkartdeliverypartner.presentation.delivery

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartdeliverypartner.core.network.RetrofitClient
import com.application.quickkartdeliverypartner.core.util.PreferencesManager
import com.application.quickkartdeliverypartner.data.mapper.DeliveryMapper
import com.application.quickkartdeliverypartner.data.remote.api.DeliveryApi
import com.application.quickkartdeliverypartner.data.repository.DeliveryRepositoryImpl
import com.application.quickkartdeliverypartner.domain.model.DeliveryAssignment
import com.application.quickkartdeliverypartner.domain.model.DeliveryStatusUpdate
import com.application.quickkartdeliverypartner.domain.repository.DeliveryRepository
import com.application.quickkartdeliverypartner.domain.usecase.DeliveryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeliveryAssignmentsViewModel(
    private val context: Context
): ViewModel() {
    private val preferencesManager = PreferencesManager(context)
    private val deliveryApi: DeliveryApi = RetrofitClient.getAuthenticatedClient().create(DeliveryApi::class.java)
    private val deliveryMapper = DeliveryMapper()
    private val deliveryRepository: DeliveryRepository = DeliveryRepositoryImpl(deliveryApi, deliveryMapper)
    private val deliveryUseCase = DeliveryUseCase(deliveryRepository)

    private val _assignments = MutableStateFlow<List<DeliveryAssignment>>(emptyList())
    val assignments: StateFlow<List<DeliveryAssignment>> = _assignments

    private val _isLoading = MutableStateFlow(false)
    val isLoading : StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> =_error

    private val _updateStatus = MutableStateFlow<String?>(null)
    val updateStatus: StateFlow<String?> = _updateStatus

    init {
        // Initialize RetrofitClient with preferences
        RetrofitClient.init(preferencesManager)
        loadAssignments()
    }

    fun loadAssignments(){
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
            } catch (e: Exception){
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateDeliveryStatus(assignmentId: Int, status: String){
        viewModelScope.launch {
            _updateStatus.value = "Updating status"
            _error.value = null

            try {
                val update = DeliveryStatusUpdate(assignmentId, status)
                deliveryUseCase.updateDeliveryStatus(update).fold(
                    onSuccess = {success ->
                        if (success) {
                            _updateStatus.value = "Status updated successfully"
                            loadAssignments()
                        } else {
                            _error.value = "Failed to update status"
                        }

                    },
                    onFailure = {exception ->
                        _error.value = exception.message ?: "Failed to update status"
                    }
                )
            } catch (e: Exception){
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    _updateStatus.value = null
                }
            }
        }
    }

    fun clearError(){
        _error.value = null
    }
}