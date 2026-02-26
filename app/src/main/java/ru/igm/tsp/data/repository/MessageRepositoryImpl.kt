package ru.igm.tsp.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.igm.tsp.domain.model.message.ChatMessage
import ru.igm.tsp.domain.repository.MessageRepository

class MessageRepositoryImpl : MessageRepository {
    private val TAG = "MessageRepositoryImpl"
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    override val messages: Flow<List<ChatMessage>> = _messages.asStateFlow()

    override fun addMessage(message: ChatMessage) {
        val startTime = System.currentTimeMillis()
        Log.d(TAG, "addMessage() called with UUID: ${message.uuid}")

        val currentList = _messages.value
        val newList = currentList + message

        // Проверяем, не является ли это дубликатом
        val isDuplicate = currentList.any { it.uuid == message.uuid }
        if (isDuplicate) {
            Log.w(TAG, "Duplicate message detected with UUID: ${message.uuid}. Not adding to list.")
            return
        }

        Log.d(TAG, "New list size: ${newList.size}. Emitting new list.")
        _messages.value = newList

        val endTime = System.currentTimeMillis()
        Log.d(TAG, "addMessage() completed in ${endTime - startTime}ms")
    }

    override fun clearMessages() {
        Log.d(TAG, "clearMessages() called")
        _messages.value = emptyList()
    }

    // Добавляем метод для пакетного добавления сообщений
    fun addMessages(messages: List<ChatMessage>) {
        val startTime = System.currentTimeMillis()
        Log.d(TAG, "addMessages() called with ${messages.size} messages")

        val currentList = _messages.value
        // Фильтруем дубликаты
        val newMessages = messages.filter { newMsg ->
            !currentList.any { existingMsg -> existingMsg.uuid == newMsg.uuid }
        }

        if (newMessages.isNotEmpty()) {
            val newList = currentList + newMessages
            Log.d(TAG, "Adding ${newMessages.size} new messages. New list size: ${newList.size}")
            _messages.value = newList
        } else {
            Log.d(TAG, "No new messages to add (all were duplicates)")
        }

        val endTime = System.currentTimeMillis()
        Log.d(TAG, "addMessages() completed in ${endTime - startTime}ms")
    }
}