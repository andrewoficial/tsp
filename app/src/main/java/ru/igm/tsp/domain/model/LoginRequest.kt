package ru.igm.tsp.domain.model

// Запрос на логин (аналог вашего requestBody)
data class LoginRequest(
    val username: String? = null,
    val password: String? = null,
    val deviceSerialNumber: String? = null,
    val deviceAccessCode: String? = null,
    val captchaId: String,
    val captchaValue: String
)