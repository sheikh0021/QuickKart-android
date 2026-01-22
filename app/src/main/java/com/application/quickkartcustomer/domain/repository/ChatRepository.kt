package com.application.quickkartcustomer.domain.repository

import com.application.quickkartcustomer.domain.model.ChatMessage
import com.application.quickkartcustomer.domain.model.ChatRoom

interface ChatRepository {
    suspend fun getChatRooms(): Result<List<ChatRoom>>
    suspend fun getChatRoom(roomId: Int): Result<ChatRoom>
    suspend fun getOrCreateChatRoom(orderId: Int): Result<ChatRoom>
    suspend fun getChatMessages(roomId: Int): Result<List<ChatMessage>>
    suspend fun sendMessage(roomId: Int, message: String): Result<ChatMessage>
    suspend fun markMessagesAsRead(roomId: Int): Result<Unit>
}
