package ru.igm.tsp.data.remote

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import ru.igm.tsp.domain.model.ChatMessage

class WebSocketRepository {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    // Flow для исходящих сообщений (от сервера к нам)
    private val _messages = MutableSharedFlow<ChatMessage>()
    val messages: Flow<ChatMessage> = _messages.asSharedFlow()

    // Можно добавить Flow для статуса соединения, если нужно
    // private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.CLOSED)
    // val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    fun connect(token: String) {
        if (webSocket != null) return // Уже подключены

        val request = Request.Builder()
            .url("wss://your-server.com/ws") // <-- ВАШ WS URL
            .addHeader("Authorization", "Bearer $token") // Если нужно
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connection opened!")
                // _connectionState.value = ConnectionState.OPEN
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received: $text")
                try {
                    val message = parseMessage(text) // Функция для парсинга JSON в ChatMessage
                    message?.let { _messages.tryEmit(it) }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Failed to parse message", e)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Closing: $code $reason")
                // _connectionState.value = ConnectionState.CLOSING
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Connection failed", t)
                // _connectionState.value = ConnectionState.ERROR
            }
        }

        webSocket = client.newWebSocket(request, listener)
    }

    fun sendMessage(message: ChatMessage) {
        webSocket?.send(message.toJsonString()) // Функция для сериализации ChatMessage в JSON
    }

    fun disconnect() {
        webSocket?.close(1000, "User logged out")
        webSocket = null
    }

    // Вспомогательные функции (нужно будет реализовать)
    private fun parseMessage(json: String): ChatMessage? {
        // TODO: Используйте Gson для парсинга JSON в ChatMessage
        return null
    }
    private fun ChatMessage.toJsonString(): String {
        // TODO: Используйте Gson для сериализации
        return ""
    }
}