package ru.igm.tsp.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.igm.tsp.domain.model.message.ChatMessage

interface MessageRepository {
    val messages: Flow<List<ChatMessage>>
    fun addMessage(message: ChatMessage)
    fun clearMessages()
}