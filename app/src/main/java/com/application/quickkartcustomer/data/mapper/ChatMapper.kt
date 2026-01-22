package com.application.quickkartcustomer.data.mapper

import com.application.quickkartcustomer.data.remote.dto.ChatMessageDto
import com.application.quickkartcustomer.data.remote.dto.ChatRoomDto
import com.application.quickkartcustomer.domain.model.ChatMessage
import com.application.quickkartcustomer.domain.model.ChatRoom

object ChatMapper {
    fun mapToDomain(dto: ChatRoomDto): ChatRoom {
        return ChatRoom(
            id = dto.id,
            orderId = dto.order,
            orderNumber = dto.orderNumber,
            customerId = dto.customer,
            customerName = dto.customerName,
            deliveryPartnerId = dto.deliveryPartner,
            deliveryPartnerName = dto.deliveryPartnerName,
            isActive = dto.isActive,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            unreadCount = dto.unreadCount,
            lastMessage = dto.lastMessage?.let { mapMessageToDomain(it) }
        )
    }

    fun mapMessageToDomain(dto: ChatMessageDto): ChatMessage {
        return ChatMessage(
            id = dto.id,
            roomId = dto.room,
            senderId = dto.senderId,
            senderName = dto.senderName,
            senderType = dto.senderType,
            message = dto.message,
            isRead = dto.isRead,
            createdAt = dto.createdAt
        )
    }

    fun mapMessagesToDomain(dtos: List<ChatMessageDto>): List<ChatMessage> {
        return dtos.map { mapMessageToDomain(it) }
    }
}
