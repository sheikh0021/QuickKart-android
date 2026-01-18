package com.application.quickkartcustomer.data.remote.api

import com.application.quickkartcustomer.data.remote.dto.AddressDto
import com.application.quickkartcustomer.data.remote.dto.AddressListResponseDto
import com.application.quickkartcustomer.data.remote.dto.OrderDto
import com.application.quickkartcustomer.data.remote.dto.OrderListResponseDto
import com.application.quickkartcustomer.data.remote.dto.OrderRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface OrderApi {
    @POST("orders/create/")
    suspend fun createOrder(@Body request: OrderRequestDto): Response<OrderDto>

@GET("orders/")
suspend fun getOrderHistory(): Response<OrderListResponseDto>

    @GET("orders/{orderId}/")
    suspend fun getOrderDetails(@Path("orderId") orderId: Int): Response<OrderDto>

    @GET("users/addresses/")
    suspend fun getAddresses(): Response<AddressListResponseDto>


    @POST("users/addresses/")
    suspend fun addAddress(@Body address: AddressDto): Response<AddressDto>
}