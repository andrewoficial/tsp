package ru.igm.tsp.domain.model

// Ответ с капчей
data class CaptchaResponse(
    val captchaId: String,
    val captchaImage: String // Base64 строка
)