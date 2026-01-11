package com.application.quickkartdeliverypartner.data.mapper

import com.application.quickkartdeliverypartner.data.remote.dto.DeliveryAssignmentDto
import com.application.quickkartdeliverypartner.domain.model.DeliveryAssignment


class DeliveryMapper {
    fun mapDeliveryAssignmentToDomain(dto: DeliveryAssignmentDto): DeliveryAssignment {
        return DeliveryAssignment(
            id = dto.id,
            orderId = dto.order,
            orderNumber = dto.orderDetails.orderNumber,
            orderStatus = dto.orderDetails.status,
            totalAmount = dto.orderDetails.totalAmount,
            deliveryAddress = dto.orderDetails.deliveryAddress,
            customerName = dto.customerDetails.name,
            customerPhone = dto.customerDetails.phone,
            assignedAt = dto.assignedAt,
            pickedUpAt = dto.pickedUpAt,
            deliveredAt = dto.deliveredAt,
            estimatedDeliveryTime = dto.estimatedDeliveryTime
        )
    }
}