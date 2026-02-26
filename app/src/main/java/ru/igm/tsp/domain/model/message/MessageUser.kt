package ru.igm.tsp.domain.model.message

data class MessageUser(
    val id: String,
    val clientId: String,
    val role: String,
    val email: String,
    val verified: Boolean
)