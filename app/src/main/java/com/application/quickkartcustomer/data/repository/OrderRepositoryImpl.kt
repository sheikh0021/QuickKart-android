package com.application.quickkartcustomer.data.repository

import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import com.application.quickkartcustomer.data.mapper.OrderMapper
import com.application.quickkartcustomer.data.remote.api.OrderApi
import com.application.quickkartcustomer.data.remote.dto.AddressDto
import com.application.quickkartcustomer.data.remote.dto.OrderItemDto
import com.application.quickkartcustomer.data.remote.dto.OrderItemRequestDto
import com.application.quickkartcustomer.data.remote.dto.OrderListResponseDto
import com.application.quickkartcustomer.data.remote.dto.OrderRequestDto
import com.application.quickkartcustomer.domain.model.Address
import com.application.quickkartcustomer.domain.model.Order
import com.application.quickkartcustomer.domain.model.OrderRequest
import com.application.quickkartcustomer.domain.repository.OrderRepository
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderApi: OrderApi,
    private val orderMapper: OrderMapper
) : OrderRepository {

    override suspend fun createOrder(request: OrderRequest): Result<Order> {
        return try {
            val dto  = OrderRequestDto(
                items = request.items.map { item ->
                    OrderItemRequestDto(
                        productId = item.product,
                        productName = item.productName,
                        quantity = item.quantity,
                        price = item.unitPrice,
                        total = item.totalPrice
                    )
                },
                addressId = request.addressId,
                paymentMethod = request.paymentMethod,
                notes = request.notes
            )

            val response = orderApi.createOrder(dto)
            if (response.isSuccessful) {
                response.body()?.let { orderDto ->
                    Result.success(orderMapper.mapOrderToDomain(orderDto))
                } ?: Result.failure(Exception("Empty Response"))
            } else {
                Result.failure(Exception("Failed to create the order: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getOrderHistory(): Result<List<Order>> {
        return try {
            println("OrderRepositoryImpl: Calling getOrderHistory API")
            val response = orderApi.getOrderHistory()
            println("OrderRepositoryImpl: API response code: ${response.code()}")
            println("OrderRepositoryImpl: API response message: ${response.message()}")

            if (response.isSuccessful) {
                response.body()?.let { paginatedResponse ->
                    println("OrderRepositoryImpl: Received paginated response with ${paginatedResponse.results.size} orders")
                    val orders = paginatedResponse.results.map { orderMapper.mapOrderToDomain(it) }
                    println("OrderRepositoryImpl: Mapped to ${orders.size} domain orders")
                    Result.success(orders)
                } ?: run {
                    println("OrderRepositoryImpl: Empty response body")
                    Result.failure(Exception("Empty response"))
                }
            } else {
                println("OrderRepositoryImpl: API call failed with code ${response.code()}")
                Result.failure(Exception("Failed to fetch orders: ${response.message()}"))
            }
        } catch (e: Exception) {
            println("OrderRepositoryImpl: Exception during API call: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getOrderDetails(orderId: Int): Result<Order> {
        return try {
            val response = orderApi.getOrderDetails(orderId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(orderMapper.mapOrderToDomain(dto))
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to fetch order details: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAddresses(): Result<List<Address>> {
        return try {
            val response = orderApi.getAddresses()
            if (response.isSuccessful && response.body() != null) {
                val responseDto = response.body()!!
                val addresses = responseDto.addresses.map {orderMapper.mapAddressToDomain(it)}
                Result.success(addresses)
            }else {
                Result.failure(Exception("Failed to load addresses: ${response.message()}"))
            }
        }catch (e: Exception) {
            Result.failure(Exception("Failed to load addresses: ${e.message}"))
        }
    }

    override suspend fun getOrders(): Result<List<Order>> {
        return getOrderHistory()
    }

    override suspend fun addAddress(address: Address): Result<Address> {
        return try {
            val dto = AddressDto(
                street = address.street,
                city = address.city,
                state = address.state,
                zipCode = address.zipCode,
                isDefault = address.isDefault
            )

            val response = orderApi.addAddress(dto)
            if (response.isSuccessful) {
                response.body()?.let { addressDto ->
                    Result.success(orderMapper.mapAddressToDomain(addressDto))
                } ?: Result.failure(Exception("Empty Response"))
            } else {
                Result.failure(Exception("Failed to add address: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}