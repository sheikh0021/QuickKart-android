package com.application.quickkartadmin.domain.usecase

import androidx.lifecycle.LiveData
import com.application.quickkartadmin.domain.model.Admin
import com.application.quickkartadmin.domain.model.DashboardStats
import com.application.quickkartadmin.domain.model.DeliveryPartner
import com.application.quickkartadmin.domain.model.Order
import com.application.quickkartadmin.domain.repository.AdminRepository
import javax.inject.Inject


class AdminUseCase @Inject constructor(
    private val adminRepository: AdminRepository
) {
    suspend fun login(username: String, password: String): Result<Admin> {
        return adminRepository.adminLogin(username, password)
    }

    suspend fun getAllOrders(status: String? = null): Result<List<Order>> {
        return adminRepository.getAllOrders(status)
    }

    suspend fun getOrderDetails(orderId: Int): Result<Order>{
        return adminRepository.getOrderDetails(orderId)
    }
    suspend fun assignDeliveryPartner(orderId: Int, deliveryPartnerId: Int): Result<String> {
        return adminRepository.assignDeliveryPartner(orderId, deliveryPartnerId)
    }
    suspend fun updateOrderStatus(orderId: Int, status: String): Result<String> {
        return adminRepository.updateOrderStatus(orderId, status )
    }
    suspend fun getDeliveryPartners(): Result<List<DeliveryPartner>> {
        return adminRepository.getDeliveryPartners()
    }
    suspend fun getDashboardStats(): Result<DashboardStats> {
        return adminRepository.getDashboardStats()
    }

    suspend fun updateFcmToken(token: String): Result<String> {
        return adminRepository.updateFcmToken(token)
    }
}