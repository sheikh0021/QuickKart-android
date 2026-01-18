package com.application.quickkartadmin.data.repository

import com.application.quickkartadmin.data.mapper.toAdmin
import com.application.quickkartadmin.data.mapper.toDashboardStats
import com.application.quickkartadmin.data.mapper.toDeliveryPartnerList
import com.application.quickkartadmin.data.mapper.toOrder
import com.application.quickkartadmin.data.mapper.toOrderList
import com.application.quickkartadmin.data.remote.api.AdminApi
import com.application.quickkartadmin.domain.model.Admin
import javax.inject.Named
import com.application.quickkartadmin.domain.model.DashboardStats
import com.application.quickkartadmin.domain.model.DeliveryPartner
import com.application.quickkartadmin.domain.model.Order
import com.application.quickkartadmin.domain.repository.AdminRepository
import com.google.gson.Gson
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val adminApi: AdminApi,
    @Named("adminLoginApi") private val adminLoginApi: AdminApi
): AdminRepository {
    override suspend fun adminLogin(username: String, password: String): Result<Admin> {
        return try {
            val response = adminLoginApi.adminLogin(mapOf("username" to username, "password" to password))
            if (response.isSuccessful) {
                response.body()?.let { adminDto ->
                    Result.success(adminDto.toAdmin())
                } ?: Result.failure(Exception("Login Failed: Empty response"))
            } else {
                Result.failure(Exception("Login Failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllOrders(status: String?): Result<List<Order>> {
        return try {
            val response = adminApi.getAllOrders(status)
            if (response.isSuccessful) {
                response.body()?.let { orderDtos ->
                    Result.success(orderDtos.toOrderList())
                } ?: Result.failure(Exception("Failed to get orders: Empty Response"))
            } else {
                Result.failure(Exception("Failed to get orders: ${response.message()}"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getOrderDetails(orderId: Int): Result<Order> {
        return try {
            val response = adminApi.getOrderDetails(orderId)
            if (response.isSuccessful){
                response.body()?.let { orderDto ->
                    Result.success(orderDto.toOrder())
                } ?: Result.failure(Exception("Failed to get order details: Empty Response"))
            } else {
                Result.failure(Exception("Failed to get order details: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun assignDeliveryPartner(
        orderId: Int,
        deliverPartnerId: Int
    ): Result<String> {
        return try {
            val response = adminApi.assignDeliveryPartner(orderId, mapOf("delivery_partner_id" to deliverPartnerId))
            if (response.isSuccessful){
                val responseBody = response.body()
                val message = responseBody?.get("message") ?: "Delivery partner assigned successfully"
                Result.success(message)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody?.contains("error") == true){
                    try {
                        val errorJson = Gson().fromJson(errorBody, Map::class.java)
                        errorJson["error"] as? String ?: response.message()
                    } catch (e: Exception){}
                    response.message()
                } else {
                    response.message()
                }
                Result.failure(Exception("Failed to assign delivery partner : $errorMessage"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun updateOrderStatus(orderId: Int, status: String): Result<String> {
        return try {
            val response = adminApi.updateOrderStatus(orderId, mapOf("status" to status))
            if (response.isSuccessful){
                Result.success("Order status updated successfully")
            } else {
                Result.failure(Exception("Failed to update order status: ${response.message()}"))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getDeliveryPartners(): Result<List<DeliveryPartner>> {
        return try {
            val response = adminApi.getDeliveryPartners()
            if (response.isSuccessful){
                response.body()?.let { partnerDtos ->
                    Result.success(partnerDtos.toDeliveryPartnerList())
                } ?: Result.failure(Exception("Failed to get delivery partners: Empty Response"))
            } else {
                Result.failure(Exception("Failed to get delivery partners: ${response.message()}"))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getDashboardStats(): Result<DashboardStats> {
        return try {
            val response = adminApi.getDashboardStats()
            if (response.isSuccessful){
                response.body()?.let { statsDto ->
                    Result.success(statsDto.toDashboardStats())
                } ?: Result.failure(Exception("Failed to get dashboard stats: Empty Response"))
            } else {
                Result.failure(Exception("Failed to get dashboard stats: ${response.message()}"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun updateFcmToken(token: String): Result<String> {
        return try {
            val response = adminApi.updateFcmToken(mapOf("fcm_token" to token))
            if (response.isSuccessful) {
                Result.success("FCM token updated successfully")
            } else {
                Result.failure(Exception("Failed to update FCM token: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}