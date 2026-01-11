package com.application.quickkartdeliverypartner.domain.usecase

import com.application.quickkartdeliverypartner.domain.model.DeliveryAssignment
import com.application.quickkartdeliverypartner.domain.model.DeliveryEarnings
import com.application.quickkartdeliverypartner.domain.model.DeliveryLocationUpdate
import com.application.quickkartdeliverypartner.domain.model.DeliveryStatusUpdate
import com.application.quickkartdeliverypartner.domain.repository.DeliveryRepository
import javax.inject.Inject


class DeliveryUseCase @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) {
    suspend fun getMyAssignments(): Result<List<DeliveryAssignment>> {
        return deliveryRepository.getMyAssignments()
    }
    suspend fun updateDeliveryStatus(update: DeliveryStatusUpdate): Result<Boolean>{
        return deliveryRepository.updateDeliveryStatus(update)
    }
    suspend fun updateLocation(update: DeliveryLocationUpdate): Result<Boolean>{
        return deliveryRepository.updateLocation(update)
    }
    suspend fun getEarnings(): Result<DeliveryEarnings> {
        return deliveryRepository.getEarnings()
    }
}