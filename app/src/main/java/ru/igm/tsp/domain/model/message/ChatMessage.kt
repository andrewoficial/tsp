package ru.igm.tsp.domain.model.message

import ru.igm.tsp.domain.model.message.Content
import ru.igm.tsp.domain.model.message.Sender

data class ChatMessage(
    val messageType: String,
    val uuid: String,
    val sender: Sender,
    val sessionId: String,
    val timestamp: Long,
    val direction: String, // "outgoing", "incoming"
    val recipient: String,
    val content: Content,
    val metadata: Metadata,
    val status: String // "sent" (дублирует metadata.status, оставим для совместимости)
)