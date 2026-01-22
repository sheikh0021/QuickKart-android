package com.application.quickkartcustomer.core.chat

import com.application.quickkartcustomer.domain.model.ChatMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Singleton that receives new chat messages from FCM and emits them to active observers.
 * ChatViewModel collects and appends to the message list when room matches.
 */
object ChatMessageNotifier {
    private val _incoming = MutableSharedFlow<ChatMessage>(replay = 0, extraBufferCapacity = 64)
    val incoming: SharedFlow<ChatMessage> = _incoming.asSharedFlow()

    fun onNewMessage(message: ChatMessage) {
        _incoming.tryEmit(message)
    }
}
