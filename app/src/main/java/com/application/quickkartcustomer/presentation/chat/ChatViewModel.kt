package com.application.quickkartcustomer.presentation.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.quickkartcustomer.core.chat.ChatMessageNotifier
import com.application.quickkartcustomer.domain.model.ChatMessage
import com.application.quickkartcustomer.domain.model.ChatRoom
import com.application.quickkartcustomer.domain.usecase.ChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orderId: Int? = savedStateHandle.get<Int>("orderId")
    private val roomId: Int? = savedStateHandle.get<Int>("roomId")

    private val _chatRoom = MutableStateFlow<ChatRoom?>(null)
    val chatRoom: StateFlow<ChatRoom?> = _chatRoom.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    init {
        observeIncomingMessages()
        loadChatRoom()
        startPeriodicRefetch()
    }

    /** Refetch messages every 5s while we have a room, for real-time updates (FCM fallback). */
    private fun startPeriodicRefetch() {
        viewModelScope.launch {
            while (true) {
                delay(5_000)
                _chatRoom.value?.let { room -> loadMessages(room.id) }
            }
        }
    }

    private fun observeIncomingMessages() {
        viewModelScope.launch {
            ChatMessageNotifier.incoming.collect { message ->
                val currentRoomId = _chatRoom.value?.id ?: return@collect
                if (message.roomId == currentRoomId && !_messages.value.any { it.id == message.id }) {
                    _messages.value = _messages.value + message
                }
            }
        }
    }

    private fun loadMessages(roomId: Int) {
        viewModelScope.launch {
            chatUseCase.getChatMessages(roomId).fold(
                onSuccess = { list ->
                    _messages.value = list
                    markMessagesAsRead(roomId)
                },
                onFailure = { e ->
                    if (_messages.value.isEmpty()) {
                        _error.value = e.message ?: "Failed to load messages"
                    }
                }
            )
        }
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) return
        val room = _chatRoom.value ?: return
        viewModelScope.launch {
            _isSending.value = true
            chatUseCase.sendMessage(room.id, message).fold(
                onSuccess = { sent ->
                    if (!_messages.value.any { it.id == sent.id }) {
                        _messages.value = _messages.value + sent
                    }
                },
                onFailure = { e ->
                    _error.value = e.message ?: "Failed to send message"
                }
            )
            _isSending.value = false
        }
    }

    fun markMessagesAsRead(roomId: Int) {
        viewModelScope.launch {
            chatUseCase.markMessagesAsRead(roomId)
        }
    }

    fun loadChatRoom() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = when {
                roomId != null -> chatUseCase.getChatRoom(roomId)
                orderId != null -> chatUseCase.getOrCreateChatRoom(orderId)
                else -> {
                    _isLoading.value = false
                    _error.value = "No order ID or room ID provided"
                    return@launch
                }
            }

            result.fold(
                onSuccess = { room ->
                    _chatRoom.value = room
                    loadMessages(room.id)
                    _isLoading.value = false
                },
                onFailure = { e ->
                    _error.value = e.message ?: "Failed to load chat room"
                    _isLoading.value = false
                }
            )
        }
    }
}
