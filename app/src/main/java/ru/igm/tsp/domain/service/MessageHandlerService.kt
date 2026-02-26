package ru.igm.tsp.domain.service

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import ru.igm.tsp.domain.model.message.ChatMessage
import ru.igm.tsp.domain.model.message.MessageUser
import ru.igm.tsp.domain.repository.MessageRepository

class MessageHandlerService(
    private val messageRepository: MessageRepository
) {
    private val TAG = "MessageHandler"
    private val gson = Gson()

    fun handleMessage(rawMessage: String) {
        Log.d(TAG, "Handling raw message: ${rawMessage.take(100)}...")

        // 1. Валидация
        if (!MessageValidator.isValidRawMessage(rawMessage)) {
            Log.w(TAG, "Invalid raw message format. Skipping.")
            return
        }

        // 2. Парсинг
        val parsedMessage = MessageParser.parse(rawMessage)
        if (parsedMessage == null) {
            Log.w(TAG, "Failed to parse message. Skipping.")
            return
        }

        Log.d(TAG, "Successfully parsed message with UUID: ${parsedMessage.uuid}, type: ${parsedMessage.messageType}")

        // 3. Обработка по типу
        when (parsedMessage.messageType) {
            "message" -> {
                Log.d(TAG, "Processing 'message' type")
                messageRepository.addMessage(parsedMessage)
                // TODO: Отправить ACK
            }
            "status" -> {
                Log.d(TAG, "Processing 'status' type: ${parsedMessage.content.body}")
                handleStatusMessage(parsedMessage)
            // TODO: Обработать статус (например, "AI печатает...")
            }
            "command" -> {
                Log.d(TAG, "Processing 'command' type")
                handleCommandMessage(parsedMessage)
            }
            "archive" -> {
                Log.d(TAG, "Processing 'archive' type")
                handleArchiveMessage(parsedMessage)
                // TODO: Отправить ACK
            }
            "ACK" -> {
                Log.d(TAG, "Processing 'ACK' for ${parsedMessage.metadata?.relatedMessageId}")
                // TODO: Обновить статус исходящего сообщения
            }
            "error" -> {
                Log.d(TAG, "Processing 'error' type: ${parsedMessage.content.body}")
                // TODO: Показать ошибку пользователю
            }
            else -> {
                Log.w(TAG, "Unknown message type: ${parsedMessage.messageType}")
            }
        }
    }

    private fun handleStatusMessage(message: ChatMessage) {
        val statusAction = message.content.body
        if (statusAction.isNullOrEmpty()) {
            Log.w(TAG, "Status message has no body, cannot process action.")
            return
        }

        // Парсим действие вида "generation:start" или "upload:fail"
        val parts = statusAction.split(":")
        if (parts.size < 2) {
            Log.w(TAG, "Invalid status action format: $statusAction")
            return
        }

        val process = parts[0]
        val action = parts[1]

        when (process) {
            "generation" -> {
                when (action) {
                    "start" -> {
                        Log.d(TAG, "Generation started")
                        // TODO: Установить статус "AI печатает..."
                    }
                    "end", "fail", "denied" -> {
                        Log.d(TAG, "Generation ended")
                        // TODO: Сбросить статус "AI печатает..."
                    }
                }
            }
            "upload" -> {
                when (action) {
                    "start" -> {
                        Log.d(TAG, "Upload started")
                        // TODO: Установить статус "Загрузка файла..."
                    }
                    "end", "fail", "denied" -> {
                        Log.d(TAG, "Upload ended")
                        // TODO: Сбросить статус "Загрузка файла..."
                    }
                }
            }
        }
    }

    private fun handleCommandMessage(message: ChatMessage) {
        when (message.content.body) {
            "user_obj" -> {
                try {
                    val userObj = gson.fromJson(message.content.value, MessageUser::class.java)
                    Log.d(TAG, "Received user info: $userObj")
                    // TODO: Обновить информацию о пользователе
                } catch (e: JsonSyntaxException) {
                    Log.e(TAG, "Error parsing user object", e)
                }
            }
            "clear_chat_history" -> {
                Log.d(TAG, "Received clear chat history confirmation")
                // TODO: Очистить историю чата локально
                messageRepository.clearMessages()
            }
            "download_chat_history" -> {
                Log.d(TAG, "Received chat history")
                // Обработка будет в handleArchiveMessage
            }
            else -> {
                Log.w(TAG, "Unknown command: ${message.content.body}")
            }
        }
    }

    private fun handleArchiveMessage(message: ChatMessage) {
        Log.d(TAG, "Processing archive message")

        if (message.content.elements.isEmpty()) {
            Log.w(TAG, "Archive message has no elements")
            return
        }

        val archivedMessages = mutableListOf<ChatMessage>()

        for (element in message.content.elements) {
            if (element.type == "message" && !element.attachments.isNullOrBlank()) {
                try {
                    // Парсим attachments как JSON-объект
                    val attachmentsJson = gson.fromJson(element.attachments, JsonObject::class.java)
                    // Извлекаем строку из поля "message"
                    val messageJson = attachmentsJson.get("message")?.asString
                    if (!messageJson.isNullOrBlank()) {
                        val archivedMessage = gson.fromJson(messageJson, ChatMessage::class.java)
                        archivedMessages.add(archivedMessage)
                    }
                } catch (e: JsonSyntaxException) {
                    Log.e(TAG, "Error parsing attachments or message", e)
                }
            }
        }

        if (archivedMessages.isNotEmpty()) {
            //ToDo Доделать вставку сообщений
            Log.w(TAG, "Adding ${archivedMessages.size} archived messages NOT IMPLEMENTED")
            //messageRepository.addMessages(archivedMessages)
        }
    }

    private fun handleQueueListMessage(message: ChatMessage) {
        Log.d(TAG, "Processing queue list message")

        if (message.content.elements.isNullOrEmpty()) {
            Log.w(TAG, "Queue list message has no elements")
            return
        }

        val userListStr = message.content.elements[0].text
        if (userListStr.isNullOrEmpty()) {
            Log.w(TAG, "Empty user list string received")
            return
        }

        try {
            // Парсим список пользователей
            val users = userListStr
                .removeSurrounding("[", "]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
                .filter { it.isNotEmpty() }

            Log.d(TAG, "Received user list: $users")
            // TODO: Обновить список ожидающих пользователей

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing user list", e)
        }
    }

}