package com.application.quickkartadmin.data.remote.api

import com.application.quickkartadmin.data.remote.dto.AdminDto
import com.application.quickkartadmin.data.remote.dto.DashboardStatsDto
import com.application.quickkartadmin.data.remote.dto.DeliveryPartnerDto
import com.application.quickkartadmin.data.remote.dto.OrderDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminApi {
    @POST("admin/login/")
    suspend fun adminLogin(@Body loginRequest: Map<String, String>): Response<AdminDto>

    @GET("admin/orders/")
    suspend fun getAllOrders(@Query("status") status: String? = null): Response<List<OrderDto>>

    @GET("admin/orders/{orderId}/")
    suspend fun getOrderDetails(@Path("orderId") orderId: Int): Response<OrderDto>

    @POST("admin/orders/{orderId}/assign-delivery/")
    suspend fun assignDeliveryPartner(
        @Path("orderId") orderId: Int,
        @Body request: Map<String, Int>
    ): Response<Map<String, String>>

    @POST("admin/orders/{orderId}/update-status/")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: Int,
        @Body request: Map<String, String>
    ): Response<Map<String, String>>

    @GET("admin/delivery-partners/")
    suspend fun getDeliveryPartners(): Response<List<DeliveryPartnerDto>>

    @GET("admin/dashboard-stats/")
    suspend fun getDashboardStats(): Response<DashboardStatsDto>

    @POST("users/update-fcm-token/")
    suspend fun updateFcmToken(@Body request: Map<String, String>): Response<Map<String, String>>
}