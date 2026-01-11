package com.application.quickkartdeliverypartner.domain.repository

import com.application.quickkartdeliverypartner.domain.model.DeliveryAssignment
import com.application.quickkartdeliverypartner.domain.model.DeliveryEarnings
import com.application.quickkartdeliverypartner.domain.model.DeliveryLocationUpdate
import com.application.quickkartdeliverypartner.domain.model.DeliveryStatusUpdate

interface DeliveryRepository {
    suspend fun getMyAssignments(): Result<List<DeliveryAssignment>>
    suspend fun updateDeliveryStatus(update: DeliveryStatusUpdate): Result<Boolean>
    suspend fun updateLocation(update: DeliveryLocationUpdate): Result<Boolean>
    suspend fun getEarnings(): Result<DeliveryEarnings>
}