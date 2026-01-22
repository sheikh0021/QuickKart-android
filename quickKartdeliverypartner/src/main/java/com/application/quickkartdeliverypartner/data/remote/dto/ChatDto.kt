package com.application.quickkartdeliverypartner.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatRoomDto(
    val id: Int,
    val order: Int,
    @SerializedName("order_number") val orderNumber: String,
    val customer: Int,
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("delivery_partner") val deliveryPartner: Int,
    @SerializedName("delivery_partner_name") val deliveryPartnerName: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("unread_count") val unreadCount: Int = 0,
    @SerializedName("last_message") val lastMessage: ChatMessageDto?
)

data class ChatMessageDto(
    val id: Int,
    val room: Int,
    val sender: Int,
    @SerializedName("sender_id") val senderId: Int,
    @SerializedName("sender_name") val senderName: String,
    @SerializedName("sender_type") val senderType: String,
    val message: String,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("created_at") val createdAt: String
)

data class ChatMessageListResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ChatMessageDto>
)
