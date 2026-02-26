package ru.igm.tsp.domain.factory

import ru.igm.tsp.domain.model.message.ChatMessage
import ru.igm.tsp.domain.model.message.Content
import ru.igm.tsp.domain.model.message.Sender
import ru.igm.tsp.domain.model.message.Metadata
import java.util.*

object MessageFactory {
    
    fun createTextMessage(body: String, senderId: String, recipient: String): ChatMessage {
        return ChatMessage(
            messageType = "message",
            uuid = UUID.randomUUID().toString(),
            sender = Sender(id = senderId, name = "Me", type = "CLIENT"),
            recipient = recipient,
            content = Content(type = "text", body = body),
            timestamp = System.currentTimeMillis(),
            direction = "outgoing",
            sessionId = "default-session", // TODO: брать из SessionManager
            metadata = Metadata(status = "sent", relatedMessageId = UUID.randomUUID().toString(), deliveredAt = 0, readAt = 0),
            status = "sent"
        )
    }

    /**
     * Создает команду для очистки истории чата
     */
    fun createClearHistoryCommand(uuidList: String): ChatMessage {
        return ChatMessage(
            uuid = UUID.randomUUID().toString(),
            messageType = "command",
            sender = createCurrentUserSender(),
            content = Content(
                body = "clear_chat_history",
                value = uuidList,
                type = "command"
            ),
            metadata = createMetadata(),
            sessionId = "none",
            timestamp = 0L,
            direction = "0",
            recipient = "0",
            status = "sent"
        )
    }

    private fun createCurrentUserSender(): Sender {
        //ToDo использовать реальные данные
        return Sender("user", "android", "android");
    }

    private fun createMetadata(): Metadata {
        //ToDo использовать реальные данные
        return Metadata("sent", "null", 1L, 2L, "normal");
    }

    /**
     * Создает команду для запроса истории чата
     */
    fun createDownloadHistoryCommand(): ChatMessage {
        return ChatMessage(
            uuid = UUID.randomUUID().toString(),
            messageType = "command",
            sender = createCurrentUserSender(),
            content = Content(
                body = "download_chat_history",
                value = "",
                type = "command"
            ),
            metadata = createMetadata(),
            sessionId = "none",
            timestamp = 0L,
            direction = "0",
            recipient = "0",
            status = "sent"
        )
    }

    /**
     * Создает команду для прерывания генерации ответа
     */
    fun createInterruptGenerationCommand(): ChatMessage {
        return ChatMessage(
            uuid = UUID.randomUUID().toString(),
            messageType = "command",
            sender = createCurrentUserSender(),
            content = Content(
                body = "interrupt_generation",
                value = "",
                type = "command"
            ),
            metadata = createMetadata(),
            sessionId = "none",
            timestamp = 0L,
            direction = "0",
            recipient = "0",
            status = "sent"
        )
    }
    
    // TODO: Добавить createAck
}