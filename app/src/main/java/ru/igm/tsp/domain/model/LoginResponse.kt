package ru.igm.tsp.domain.model

// Ответ от сервера при успешном логине
data class LoginResponse(
    val token: String,
    val role: String,
    val id: String // ID пользователя
)