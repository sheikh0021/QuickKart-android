package com.application.quickkartdeliverypartner.data.repository

import com.application.quickkartdeliverypartner.data.mapper.ChatMapper
import com.application.quickkartdeliverypartner.data.remote.api.ChatApi
import com.application.quickkartdeliverypartner.data.remote.api.SendMessageRequest
import com.application.quickkartdeliverypartner.domain.model.ChatMessage
import com.application.quickkartdeliverypartner.domain.model.ChatRoom
import com.application.quickkartdeliverypartner.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi
) : ChatRepository {
    
    override suspend fun getChatRooms(): Result<List<ChatRoom>> {
        return try {
            val response = chatApi.getChatRooms()
            if (response.isSuccessful && response.body() != null) {
                val rooms = response.body()!!.map { ChatMapper.mapToDomain(it) }
                Result.success(rooms)
            } else {
                Result.failure(Exception("Failed to fetch chat rooms"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChatRoom(roomId: Int): Result<ChatRoom> {
        return try {
            val response = chatApi.getChatRoom(roomId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(ChatMapper.mapToDomain(response.body()!!))
            } else {
                Result.failure(Exception("Failed to fetch chat room"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrCreateChatRoom(orderId: Int): Result<ChatRoom> {
        return try {
            val response = chatApi.getOrCreateChatRoom(orderId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(ChatMapper.mapToDomain(response.body()!!))
            } else {
                Result.failure(Exception("Failed to get or create chat room"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChatMessages(roomId: Int): Result<List<ChatMessage>> {
        return try {
            val response = chatApi.getChatMessages(roomId)
            if (response.isSuccessful) {
                val body = response.body()
                val list = body?.results ?: emptyList()
                Result.success(ChatMapper.mapMessagesToDomain(list))
            } else {
                Result.failure(Exception("Failed to fetch messages: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendMessage(roomId: Int, message: String): Result<ChatMessage> {
        return try {
            val response = chatApi.sendMessage(roomId, SendMessageRequest(message))
            if (response.isSuccessful && response.body() != null) {
                Result.success(ChatMapper.mapMessageToDomain(response.body()!!))
            } else {
                Result.failure(Exception("Failed to send message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markMessagesAsRead(roomId: Int): Result<Unit> {
        return try {
            val response = chatApi.markMessagesAsRead(roomId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark messages as read"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
