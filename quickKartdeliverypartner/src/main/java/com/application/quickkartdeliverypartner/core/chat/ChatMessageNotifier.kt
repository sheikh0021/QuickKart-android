package com.application.quickkartdeliverypartner.core.chat

import com.application.quickkartdeliverypartner.domain.model.ChatMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ChatMessageNotifier {
    private val _incoming = MutableSharedFlow<ChatMessage>(replay = 0, extraBufferCapacity = 64)
    val incoming: SharedFlow<ChatMessage> = _incoming.asSharedFlow()

    fun onNewMessage(message: ChatMessage) {
        _incoming.tryEmit(message)
    }
}
