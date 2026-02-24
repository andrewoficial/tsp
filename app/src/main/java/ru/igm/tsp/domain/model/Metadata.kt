package ru.igm.tsp.domain.model

data class Metadata(
    val status: String, // "sent", "delivered" и т.д.
    val relatedMessageId: String,
    val deliveredAt: Long,
    val readAt: Long,
    val priority: String = "normal"
)