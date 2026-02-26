package ru.igm.tsp.data.remote

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import ru.igm.tsp.data.local.ConfigManager
import ru.igm.tsp.domain.model.ConnectionState
import kotlin.math.pow

/**
 * Репозиторий для работы с WebSocket соединением.
 * Отвечает исключительно за транспортный уровень:
 * 1. Установление и разрыв соединения.
 * 2. Отправка и получение СЫРЫХ строковых сообщений.
 * 3. Управление состоянием соединения и логикой переподключения.
 *
 */
class WebSocketRepository(
    private val configManager: ConfigManager
) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var currentToken: String = ""

    // Flow для СЫРЫХ строковых сообщений от сервера.
    private val _messages = MutableSharedFlow<String>(
        replay = 0,  // Не храним старые сообщения
        extraBufferCapacity = 64,  // Буфер для 64 сообщений
        onBufferOverflow = BufferOverflow.DROP_OLDEST  // Если буфер переполнен, удаляем старые сообщения
    )
    val messages: Flow<String> = _messages.asSharedFlow()


    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    // --- Логика переподключения ---
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private val reconnectDelayBase = 2000L // 2 секунды
    private var reconnectJob: Job? = null

    fun connect(token: String) {
        reconnectJob?.cancel()

        if (webSocket != null) {
            Log.d("WebSocket", "Already connected or connecting. Disconnecting first.")
            disconnect()
        }

        currentToken = token
        _connectionState.value = ConnectionState.Connecting

        val wsUrl = configManager.wsUrl
        Log.i("WebSocket", "Attempting to connect to URL: $wsUrl")

        val request = Request.Builder()
            .url(wsUrl)
            .addHeader("Sec-WebSocket-Protocol", token)
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connection opened! Server responded with: ${response.code} ${response.message}")
                reconnectAttempts = 0
                _connectionState.value = ConnectionState.Connected
            }

            // КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: просто эмитим сырую строку
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received raw: $text")
                Log.d("WebSocket", "Emitting message to Flow...")
                val emitted = _messages.tryEmit(text)
                Log.d("WebSocket", "Message emission result: $emitted")

                // Дополнительное логирование для отладки
                if (!emitted) {
                    Log.w("WebSocket", "Failed to emit message. Buffer may be full or no subscribers.")
                    Log.d("WebSocket", "Subscription count: ${_messages.subscriptionCount.value}")
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Closing: $code $reason")
                _connectionState.value = ConnectionState.Disconnected
                if (code != 1000) {
                    scheduleReconnect()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Connection failed", t)
                if (response?.code == 401) {
                    Log.w("WebSocket", "Authentication failed (401). Session expired.")
                    _connectionState.value = ConnectionState.Error("Сессия истекла. Пожалуйста, войдите снова.")
                    return
                }
                _connectionState.value = ConnectionState.Error(t.message ?: "Unknown error")
                scheduleReconnect()
            }
        }

        webSocket = client.newWebSocket(request, listener)
    }

    private fun scheduleReconnect() {
        if (reconnectAttempts >= maxReconnectAttempts) {
            Log.e("WebSocket", "Max reconnect attempts reached.")
            _connectionState.value = ConnectionState.Error("Не удалось подключиться после нескольких попыток.")
            return
        }

        reconnectAttempts++
        val delay = reconnectDelayBase * (2.0.pow(reconnectAttempts.toDouble())).toLong()

        Log.w("WebSocket", "Attempting to reconnect in ${delay}ms... (Attempt $reconnectAttempts)")

        reconnectJob = CoroutineScope(Dispatchers.IO).launch {
            delay(delay)
            Log.i("WebSocket", "Reconnecting...")
            connect(currentToken)
        }
    }

    /**
     * Отправляет СЫРОЕ строковое сообщение.
     * Сериализация ChatMessage в JSON должна происходить ВНЕ этого репозитория
     * (например, в ViewModel или UseCase).
     */
    fun sendMessage(rawMessage: String) {
        if (webSocket?.send(rawMessage) == true) {
            Log.d("WebSocket", "Message sent successfully")
        } else {
            Log.w("WebSocket", "Failed to send message. WebSocket is not open.")
        }
    }

    fun disconnect() {
        Log.d("WebSocket", "Disconnecting...")
        reconnectJob?.cancel()
        webSocket?.close(1000, "User logged out")
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
    }
}