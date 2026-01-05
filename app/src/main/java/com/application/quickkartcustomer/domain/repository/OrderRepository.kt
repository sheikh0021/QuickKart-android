package com.application.quickkartcustomer.domain.repository

import com.application.quickkartcustomer.domain.model.Order
import com.application.quickkartcustomer.domain.model.OrderRequest
import com.application.quickkartcustomer.domain.model.Address


interface OrderRepository {
    suspend fun createOrder(request: OrderRequest): Result<Order>
    suspend fun getOrders(): Result<List<Order>>
    suspend fun getOrderDetails(orderId: Int): Result<Order>
    suspend fun getAddresses(): Result<List<Address>>
    suspend fun addAddress(address: Address): Result<Address>
    suspend fun getOrderHistory(): Result<List<Order>>

}