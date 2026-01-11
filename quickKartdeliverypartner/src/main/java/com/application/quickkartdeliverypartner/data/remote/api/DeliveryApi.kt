package com.application.quickkartdeliverypartner.data.remote.api

import com.application.quickkartdeliverypartner.data.remote.dto.ApiResponse
import com.application.quickkartdeliverypartner.data.remote.dto.DeliveryAssignmentDto
import com.application.quickkartdeliverypartner.data.remote.dto.LocationUpdateDto
import com.application.quickkartdeliverypartner.data.remote.dto.PaginatedResponseDto
import com.application.quickkartdeliverypartner.data.remote.dto.StatusUpdateDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DeliveryApi {
    @GET("delivery/assignments/")
    suspend fun getAssignments(): Response<PaginatedResponseDto<DeliveryAssignmentDto>>

    @POST("delivery/assignments/{assignmentId}/status/")
    suspend fun updateStatus(
        @Path("assignmentId") assignmentId: Int,
        @Body statusUpdate: StatusUpdateDto
    ): Response<ApiResponse>

    @POST("delivery/location/")
    suspend fun updateLocation(@Body locationUpdate: LocationUpdateDto): Response<ApiResponse>
}