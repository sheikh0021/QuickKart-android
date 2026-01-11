package com.application.quickkartdeliverypartner.data.repository

import com.application.quickkartdeliverypartner.data.mapper.DeliveryMapper
import com.application.quickkartdeliverypartner.data.remote.api.DeliveryApi
import com.application.quickkartdeliverypartner.data.remote.dto.LocationUpdateDto
import com.application.quickkartdeliverypartner.data.remote.dto.StatusUpdateDto
import com.application.quickkartdeliverypartner.domain.model.DeliveryAssignment
import com.application.quickkartdeliverypartner.domain.model.DeliveryEarnings
import com.application.quickkartdeliverypartner.domain.model.DeliveryLocationUpdate
import com.application.quickkartdeliverypartner.domain.model.DeliveryStatusUpdate
import com.application.quickkartdeliverypartner.domain.repository.DeliveryRepository
import javax.inject.Inject

class DeliveryRepositoryImpl @Inject constructor(
    private val deliveryApi: DeliveryApi,
    private val deliveryMapper: DeliveryMapper
) : DeliveryRepository {
    override suspend fun getMyAssignments(): Result<List<DeliveryAssignment>> {
        return try {
            val response = deliveryApi.getAssignments()
            if (response.isSuccessful) {
                response.body()?.let { paginatedResponse ->
                    val assignments = paginatedResponse.results.map { deliveryMapper.mapDeliveryAssignmentToDomain(it) }
                    Result.success(assignments)
                } ?: Result.failure(Exception("Empty Response"))
            } else {
                Result.failure(Exception("Failed to fetch assignments: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDeliveryStatus(update: DeliveryStatusUpdate): Result<Boolean> {
        return try {
            val statusDto = StatusUpdateDto(status = update.status)
            val response = deliveryApi.updateStatus(update.assignmentId, statusDto)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to update status: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLocation(update: DeliveryLocationUpdate): Result<Boolean> {
        return try {
            val locationDto = LocationUpdateDto(
                orderId = update.orderId,
                latitude = update.latitude,
                longitude = update.longitude
            )
            val response = deliveryApi.updateLocation(locationDto)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to update location: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEarnings(): Result<DeliveryEarnings> {
        return Result.success(DeliveryEarnings())
    }
}