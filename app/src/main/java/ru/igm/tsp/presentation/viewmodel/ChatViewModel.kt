package ru.igm.tsp.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.igm.tsp.data.local.ConfigManager
import ru.igm.tsp.data.local.SessionManager
import ru.igm.tsp.data.remote.GsonProvider
import ru.igm.tsp.data.remote.adapters.LongTypeAdapter
import ru.igm.tsp.data.remote.WebSocketRepository
import ru.igm.tsp.data.remote.adapters.BooleanTypeAdapter
import ru.igm.tsp.data.remote.adapters.DoubleTypeAdapter
import ru.igm.tsp.data.remote.adapters.IntTypeAdapter
import ru.igm.tsp.data.repository.MessageRepositoryImpl
import ru.igm.tsp.domain.factory.MessageFactory
import ru.igm.tsp.domain.model.ConnectionState
import ru.igm.tsp.domain.model.message.ChatMessage
import ru.igm.tsp.domain.service.MessageHandlerService

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = "ChatViewModel"
    private val sessionManager = SessionManager(application)
    private val configManager = ConfigManager(application)

    private val webSocketRepository = WebSocketRepository(configManager)
    private val messageRepository = MessageRepositoryImpl()

    private val smartGson: Gson = GsonProvider.createGson()

    private val messageHandler = MessageHandlerService(messageRepository)

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        Log.d("ChatViewModel", "ChatViewModel init block called!")
        connectToWebSocket()
        listenForWebSocketEvents()
        Log.d("ChatViewModel", "ChatViewModel init block completed!")
    }

    private fun connectToWebSocket() {
        val token = sessionManager.token
        if (token != null) {
            webSocketRepository.connect(token)
        }
    }

    private fun listenForWebSocketEvents() {
        Log.d(TAG, "listenForWebSocketEvents() called!")
        // ГЛАВНЫЙ БЛОК: Запускаем корутину и начинаем слушать Flow от WebSocketRepository
        viewModelScope.launch {
            Log.d(TAG, "listenForWebSocketEvents(): Coroutine started. Now collecting...")
            webSocketRepository.messages.collect { rawMessage ->
                val startTime = System.currentTimeMillis()
                Log.d(TAG, "listenForWebSocketEvents(): collect block received raw message (length: ${rawMessage.length})")
                Log.d(TAG, "Raw message content: ${rawMessage.take(200)}...")

                try {
                    Log.d(TAG, "Run message handling")
                    messageHandler.handleMessage(rawMessage)
                    Log.d(TAG, "Message handled successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling message", e)
                }

                val endTime = System.currentTimeMillis()
                Log.d(TAG, "listenForWebSocketEvents(): Message handling took ${endTime - startTime}ms")
            }
        }

        // ВТОРОСТЕПЕННЫЙ БЛОК: Обновляем UI с оптимизацией
        viewModelScope.launch {
            var lastMessageCount = 0
            var lastConnectionStatus: ConnectionState? = null

            combine(
                messageRepository.messages,
                webSocketRepository.connectionState
            ) { messageList, status ->
                val messageCount = messageList.size
                val statusChanged = status != lastConnectionStatus
                val messageCountChanged = messageCount != lastMessageCount

                // Логируем только когда есть реальные изменения
                if (statusChanged || messageCountChanged) {
                    Log.d(TAG, "listenForWebSocketEvents(): combine block called. " +
                            "Message count: $messageCount (was $lastMessageCount), " +
                            "Status: $status (was $lastConnectionStatus)")

                    lastMessageCount = messageCount
                    lastConnectionStatus = status
                }

                ChatUiState(messages = messageList, connectionStatus = status)
            }.collect { newState ->
                val startTime = System.currentTimeMillis()
                _uiState.value = newState
                val endTime = System.currentTimeMillis()
                Log.d(TAG, "listenForWebSocketEvents(): UI state update took ${endTime - startTime}ms")
            }
        }
    }

    fun sendMessage(text: String) {
        val startTime = System.currentTimeMillis()
        Log.d("ChatViewModel", "sendMessage() called with text: '$text'")

        val newMessage = MessageFactory.createTextMessage(
            body = text,
            senderId = sessionManager.userId ?: return,
            recipient = "support"
        )

        messageRepository.addMessage(newMessage)
        val jsonMessage = smartGson.toJson(newMessage)
        webSocketRepository.sendMessage(jsonMessage)

        val endTime = System.currentTimeMillis()
        Log.d("ChatViewModel", "sendMessage() completed in ${endTime - startTime}ms")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("ChatViewModel", "onCleared() called. Disconnecting WebSocket.")
        webSocketRepository.disconnect()
    }

    /**
     * Запрашивает историю чата с сервера
     */
    fun downloadHistory() {
        Log.d(TAG, "Requesting chat history...")
        val command = MessageFactory.createDownloadHistoryCommand()
        val jsonCommand = smartGson.toJson(command)
        webSocketRepository.sendMessage(jsonCommand)
    }

    /**
     * Очищает историю чата
     */
    fun clearHistory() {
//        Log.d(TAG, "Clearing chat history...")
//
//        // Получаем список UUID всех сообщений
//        val messages = messageRepository.messages.value
//        val uuidList = messages.joinToString(",") { .uuid }
//
//        // Отправляем команду на сервер
//        val command = MessageFactory.createClearHistoryCommand(uuidList)
//        val jsonCommand = smartGson.toJson(command)
//        webSocketRepository.sendMessage(jsonCommand)
//
//        // Очищаем локально
//        messageRepository.clearMessages()
    }

    /**
     * Прерывает генерацию ответа AI
     */
    fun interruptGeneration() {
        Log.d(TAG, "Interrupting generation...")
        val command = MessageFactory.createInterruptGenerationCommand()
        val jsonCommand = smartGson.toJson(command)
        webSocketRepository.sendMessage(jsonCommand)
    }

}