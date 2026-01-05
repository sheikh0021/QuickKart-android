package com.application.quickkartcustomer.data.mapper

import com.application.quickkartcustomer.data.remote.dto.AddressDto
import com.application.quickkartcustomer.data.remote.dto.OrderDto
import com.application.quickkartcustomer.data.remote.dto.OrderItemDto
import com.application.quickkartcustomer.domain.model.Address
import com.application.quickkartcustomer.domain.model.Order
import com.application.quickkartcustomer.domain.model.OrderItem
import com.application.quickkartcustomer.domain.model.OrderStatus
import jakarta.inject.Inject


class OrderMapper @Inject constructor() {
    fun mapOrderToDomain(dto: OrderDto): Order {
        return Order(
            id = dto.id,
            customer = dto.customerId,
            store = dto.storeId,
            orderNumber = dto.orderNumber,
            status = dto.status,
            totalAmount = dto.totalAmount,
            deliveryFee = dto.deliveryFee,
            deliveryAddress = dto.deliveryAddress,
            deliveryLatitude = dto.deliveryLatitude,
            deliveryLongitude = dto.deliveryLongitude,
            paymentMethod = dto.paymentMethod,
            paymentStatus = dto.paymentStatus,
            items = dto.items.map { mapOrderItemToDomain(it) },
            customerName = dto.customerName,
            storeName = dto.storeName
        )
    }

    fun mapOrderItemToDomain(dto: OrderItemDto): OrderItem {
        return OrderItem(
            id = dto.id,
            order = dto.orderId,
            product = dto.productId,
            quantity = dto.quantity,
            unitPrice = dto.unitPrice,
            totalPrice = dto.totalPrice,
            productName = dto.productName,
            productImage = dto.productImage
        )
    }

    fun mapAddressToDomain(dto: AddressDto): Address {
        return Address (
            id = dto.id,
            street = dto.street,
            city = dto.city,
            state = dto.state,
            zipCode = dto.zipCode,
            isDefault = dto.isDefault
        )
    }
    fun mapStatusToDomain(status: String): OrderStatus {
        return when (status.lowercase()) {
            "placed" -> OrderStatus.PLACED
            "packed" -> OrderStatus.PACKED
            "out_for_delivery" -> OrderStatus.OUT_FOR_DELIVERY
            "delivered" -> OrderStatus.DELIVERED
            "cancelled" -> OrderStatus.CANCELLED
            else -> OrderStatus.PLACED
        }
    }
}