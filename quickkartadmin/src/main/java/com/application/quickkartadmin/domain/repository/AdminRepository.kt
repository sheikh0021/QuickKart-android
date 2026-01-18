package com.application.quickkartadmin.domain.repository

import com.application.quickkartadmin.domain.model.Admin
import com.application.quickkartadmin.domain.model.DashboardStats
import com.application.quickkartadmin.domain.model.DeliveryPartner
import com.application.quickkartadmin.domain.model.Order


interface AdminRepository {
    suspend fun adminLogin(username: String, password: String): Result<Admin>
    suspend fun getAllOrders(status: String? = null): Result<List<Order>>
    suspend fun getOrderDetails(orderId: Int): Result<Order>
    suspend fun assignDeliveryPartner(orderId: Int, deliverPartnerId: Int): Result<String>
    suspend fun updateOrderStatus(orderId: Int, status: String): Result<String>
    suspend fun getDeliveryPartners(): Result<List<DeliveryPartner>>
    suspend fun getDashboardStats(): Result<DashboardStats>
    suspend fun updateFcmToken(token: String): Result<String>
}