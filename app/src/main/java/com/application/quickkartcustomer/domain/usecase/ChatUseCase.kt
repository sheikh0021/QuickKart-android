package com.application.quickkartcustomer.domain.usecase

import com.application.quickkartcustomer.domain.model.ChatMessage
import com.application.quickkartcustomer.domain.model.ChatRoom
import com.application.quickkartcustomer.domain.repository.ChatRepository
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend fun getChatRooms(): Result<List<ChatRoom>> {
        return chatRepository.getChatRooms()
    }

    suspend fun getChatRoom(roomId: Int): Result<ChatRoom> {
        return chatRepository.getChatRoom(roomId)
    }

    suspend fun getOrCreateChatRoom(orderId: Int): Result<ChatRoom> {
        return chatRepository.getOrCreateChatRoom(orderId)
    }

    suspend fun getChatMessages(roomId: Int): Result<List<ChatMessage>> {
        return chatRepository.getChatMessages(roomId)
    }

    suspend fun sendMessage(roomId: Int, message: String): Result<ChatMessage> {
        return chatRepository.sendMessage(roomId, message)
    }

    suspend fun markMessagesAsRead(roomId: Int): Result<Unit> {
        return chatRepository.markMessagesAsRead(roomId)
    }
}
