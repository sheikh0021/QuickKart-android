package com.application.quickkartadmin.data.mapper

import com.application.quickkartadmin.data.remote.dto.AdminDto
import com.application.quickkartadmin.data.remote.dto.AdminUserDto
import com.application.quickkartadmin.data.remote.dto.DashboardStatsDto
import com.application.quickkartadmin.data.remote.dto.DeliveryAssignmentDto
import com.application.quickkartadmin.data.remote.dto.DeliveryPartnerDto
import com.application.quickkartadmin.data.remote.dto.OrderDto
import com.application.quickkartadmin.data.remote.dto.OrderItemDto
import com.application.quickkartadmin.domain.model.Admin
import com.application.quickkartadmin.domain.model.AdminUser
import com.application.quickkartadmin.domain.model.DashboardStats
import com.application.quickkartadmin.domain.model.DeliveryAssignment
import com.application.quickkartadmin.domain.model.DeliveryPartner
import com.application.quickkartadmin.domain.model.Order
import com.application.quickkartadmin.domain.model.OrderItem


fun AdminDto.toAdmin(): Admin {
    return Admin(
        token = access,
        user = user.toAdminUser()
    )
}

fun AdminUserDto.toAdminUser(): AdminUser{
    return AdminUser(
        id = id,
        username = username,
        fullName = full_name,
        userType = user_type
    )
}

fun OrderDto.toOrder(): Order{
    return Order(
        id = id,
        orderNumber = order_number,
        customerName = customer_name,
        customerPhone = customer_phone,
        storeName = store_name,
        status = status,
        totalAmount = total_amount,
        deliveryFee = delivery_fee,
        deliveryAddress = delivery_address,
        createdAt = created_at,
        updatedAt = updated_at,
        orderItems = order_items.map { it.toOrderItem() },
        deliveryAssignment = delivery_assignment?.toDeliveryAssignment()
    )
}

fun OrderItemDto.toOrderItem(): OrderItem{
    return OrderItem(
        id = id,
        productName = product_name,
        quantity = quantity,
        unitPrice = unit_price,
        totalPrice = total_price
    )
}
fun DeliveryAssignmentDto.toDeliveryAssignment(): DeliveryAssignment {
    return DeliveryAssignment(
        id = id,
        deliveryPartnerName = delivery_partner_name,
        deliveryPartnerPhone = delivery_partner_phone,
        assignedAt = assigned_at,
        pickedUpAt = picked_up_at,
        deliveredAt = delivered_at
    )
}

fun DeliveryPartnerDto.toDeliveryPartner(): DeliveryPartner{
    return DeliveryPartner(
        id = id,
        username = username,
        fullName = full_name,
        completedDeliveries = completed_deliveries,
        isAvailable = is_available
    )
}

fun DashboardStatsDto.toDashboardStats(): DashboardStats{
    return DashboardStats(
        newOrders = new_orders,
        activeDeliveries = active_deliveries,
        totalRevenue = total_revenue,
        totalPartners = total_partners
    )
}

fun List<OrderDto>.toOrderList(): List<Order> = map { it.toOrder() }
fun List<DeliveryPartnerDto>.toDeliveryPartnerList(): List<DeliveryPartner> = map { it.toDeliveryPartner() }