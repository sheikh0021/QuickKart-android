package com.application.quickkartcustomer.data.remote.api

import com.application.quickkartcustomer.data.remote.dto.AddressDto
import com.application.quickkartcustomer.data.remote.dto.OrderDto
import com.application.quickkartcustomer.data.remote.dto.OrderRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface OrderApi {
    @POST("orders/")
    suspend fun createOrder(@Body request: OrderRequestDto): Response<OrderDto>

    @GET("orders/")
    suspend fun getOrderHistory(): Response<List<OrderDto>>

    @GET("orders/{orderId}/")
    suspend fun getOrderDetails(@Path("orderId") orderId: Int): Response<OrderDto>

    @GET("user/addresses/")
    suspend fun getAddresses(): Response<List<AddressDto>>

    @POST("user/addresses/")
    suspend fun addAddress(@Body address: AddressDto): Response<AddressDto>
}