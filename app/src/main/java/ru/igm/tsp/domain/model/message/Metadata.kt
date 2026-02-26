package ru.igm.tsp.domain.model.message

import com.google.gson.annotations.SerializedName

data class Metadata(
    val status: String? = "null", // "sent", "delivered" и т.д.
    @SerializedName("relatedMessageId") val relatedMessageId: String? = "null",
    val deliveredAt: Long? = 1L,
    val readAt: Long? = 1L,
    val priority: String? = "null"
)