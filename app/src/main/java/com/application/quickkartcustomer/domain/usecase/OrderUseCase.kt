package com.application.quickkartcustomer.domain.usecase

import com.application.quickkartcustomer.domain.model.Address
import com.application.quickkartcustomer.domain.model.Order
import com.application.quickkartcustomer.domain.model.OrderRequest
import com.application.quickkartcustomer.domain.repository.OrderRepository
import javax.inject.Inject


class OrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend fun createOrder(request: OrderRequest): Result<Order> {
        return orderRepository.createOrder(request)
    }
    suspend fun getOrders(): Result<List<Order>> {
        return orderRepository.getOrders()
    }
    suspend fun getOrderHistory(): Result<List<Order>> {
        return orderRepository.getOrderHistory()
    }
    suspend fun getOrderDetails(orderId: Int): Result<Order>{
        return orderRepository.getOrderDetails(orderId)
    }
    suspend fun getAddresses(): Result<List<Address>> {
        return orderRepository.getAddresses()
    }
    suspend fun addAddress(address: Address): Result<Address> {
        return orderRepository.addAddress(address)
    }
}