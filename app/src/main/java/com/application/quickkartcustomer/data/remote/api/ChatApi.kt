package com.application.quickkartcustomer.data.remote.api

import com.application.quickkartcustomer.data.remote.dto.ChatMessageDto
import com.application.quickkartcustomer.data.remote.dto.ChatMessageListResponseDto
import com.application.quickkartcustomer.data.remote.dto.ChatRoomDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class SendMessageRequest(val message: String)

interface ChatApi {
    @GET("chat/rooms/")
    suspend fun getChatRooms(): Response<List<ChatRoomDto>>

    @GET("chat/rooms/{roomId}/")
    suspend fun getChatRoom(@Path("roomId") roomId: Int): Response<ChatRoomDto>

    @GET("chat/orders/{orderId}/room/")
    suspend fun getOrCreateChatRoom(@Path("orderId") orderId: Int): Response<ChatRoomDto>

    @GET("chat/rooms/{roomId}/messages/")
    suspend fun getChatMessages(@Path("roomId") roomId: Int): Response<ChatMessageListResponseDto>

    @POST("chat/rooms/{roomId}/messages/send/")
    suspend fun sendMessage(@Path("roomId") roomId: Int, @Body body: SendMessageRequest): Response<ChatMessageDto>

    @POST("chat/rooms/{roomId}/mark-read/")
    suspend fun markMessagesAsRead(@Path("roomId") roomId: Int): Response<Map<String, String>>
}
