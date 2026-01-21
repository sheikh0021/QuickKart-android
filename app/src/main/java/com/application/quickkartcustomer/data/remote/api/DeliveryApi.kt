package com.application.quickkartcustomer.data.remote.api

import com.application.quickkartcustomer.data.remote.dto.DeliveryLocationDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface DeliveryApi {
    @GET("delivery/orders/{orderId}/location/")
    suspend fun getDeliveryLocation(@Path("orderId") orderId: Int): Response<DeliveryLocationDto>
}