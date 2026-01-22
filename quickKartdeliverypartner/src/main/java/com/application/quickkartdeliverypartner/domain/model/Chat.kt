package com.application.quickkartdeliverypartner.domain.model

data class ChatMessage(
    val id: Int,
    val roomId: Int,
    val senderId: Int,
    val senderName: String,
    val senderType: String, // "customer" or "delivery_partner"
    val message: String,
    val isRead: Boolean,
    val createdAt: String
) {
    val isSentByMe: Boolean
        get() = senderType == "delivery_partner"
}

data class ChatRoom(
    val id: Int,
    val orderId: Int,
    val orderNumber: String,
    val customerId: Int,
    val customerName: String,
    val deliveryPartnerId: Int,
    val deliveryPartnerName: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val unreadCount: Int = 0,
    val lastMessage: ChatMessage? = null
)
