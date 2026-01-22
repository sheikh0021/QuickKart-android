package com.application.quickkartcustomer.data.repository

import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import com.application.quickkartcustomer.data.mapper.OrderMapper
import com.application.quickkartcustomer.data.remote.api.DeliveryApi
import com.application.quickkartcustomer.data.remote.api.OrderApi
import com.application.quickkartcustomer.data.remote.dto.AddressDto
import com.application.quickkartcustomer.data.remote.dto.OrderDto
import com.application.quickkartcustomer.data.remote.dto.OrderItemDto
import com.application.quickkartcustomer.data.remote.dto.OrderItemRequestDto
import com.application.quickkartcustomer.data.remote.dto.OrderListResponseDto
import com.application.quickkartcustomer.data.remote.dto.OrderRequestDto
import com.application.quickkartcustomer.domain.model.Address
import com.application.quickkartcustomer.domain.model.AssignmentStatus
import com.application.quickkartcustomer.domain.model.DeliveryLocation
import com.application.quickkartcustomer.domain.model.DeliveryPartner
import com.application.quickkartcustomer.domain.model.Location
import com.application.quickkartcustomer.domain.model.Order
import com.application.quickkartcustomer.domain.model.OrderRequest
import com.application.quickkartcustomer.domain.repository.OrderRepository
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderApi: OrderApi,
    private val orderMapper: OrderMapper,
    private val deliveryApi: DeliveryApi,
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
            val allOrders = mutableListOf<OrderDto>()
            var currentPage = 1
            var hasNextPage = true
            val maxPages = 5 // Limit to prevent infinite loops

            while (hasNextPage && currentPage <= maxPages) {
                val response = orderApi.getOrderHistory(currentPage)
                println("OrderRepositoryImpl: Page $currentPage - API response code: ${response.code()}")

                if (response.isSuccessful) {
                    response.body()?.let { paginatedResponse ->
                        println("OrderRepositoryImpl: Page $currentPage - Received ${paginatedResponse.results.size} orders")
                        allOrders.addAll(paginatedResponse.results)

                        // Check if there are more pages
                        hasNextPage = paginatedResponse.next != null
                        currentPage++
                    } ?: run {
                        println("OrderRepositoryImpl: Empty response body on page $currentPage")
                        hasNextPage = false
                    }
                } else {
                    println("OrderRepositoryImpl: API call failed on page $currentPage with code ${response.code()}")
                    return Result.failure(Exception("Failed to fetch orders: ${response.message()}"))
                }
            }

            println("OrderRepositoryImpl: Total orders collected: ${allOrders.size}")

            // Sort orders by creation date descending (newest first)
            val sortedResults: List<OrderDto> = allOrders.sortedByDescending { result: OrderDto ->
                try {
                    java.time.Instant.parse(result.createdAt)
                } catch (e: Exception) {
                    java.time.Instant.now() // fallback
                }
            }

            val orders = sortedResults.map { orderMapper.mapOrderToDomain(it) }
            println("OrderRepositoryImpl: Mapped and sorted ${orders.size} domain orders")
            Result.success(orders)
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

    override suspend fun getDeliveryLocation(orderId: Int): Result<DeliveryLocation> {
        return try {
            val response = deliveryApi.getDeliveryLocation(orderId)
            if (response.isSuccessful){
                response.body()?.let { dto ->
                    val deliveryLocation = DeliveryLocation(
                        deliveryPartner = dto.delivery_partner?.let { partner ->
                            DeliveryPartner(
                                name = partner.name,
                                phone = partner.phone
                            )
                        } ?: DeliveryPartner(
                            name = "N/A", // Default for completed deliveries
                            phone = "N/A"
                        ),
                        location = dto.location?.let {
                            Location(
                                latitude = it.latitude,
                                longitude = it.longitude,
                                timestamp = it.timestamp
                            )
                        },
                        assignmentStatus = AssignmentStatus(
                            pickedUp = dto.assignment_status.picked_up,
                            outForDelivery = dto.assignment_status.out_for_Delivery,
                            delivered = dto.assignment_status.delivered
                        ),
                        message = dto.message
                    )
                    Result.success(deliveryLocation)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to fetch delivery location: ${response.message()}"))
            }
        } catch (e: Exception){
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

    override suspend fun getOrderById(orderId: Int): Result<Order> {
        return try {
            val response = orderApi.getOrderById(orderId)

            if (response.isSuccessful && response.body() != null){
                Result.success(orderMapper.mapOrderToDomain(response.body()!!))
            } else {
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }


}