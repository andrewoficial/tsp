package ru.igm.tsp.presentation.viewmodel

import ru.igm.tsp.domain.model.LoginResponse // Предположим, что у нас есть такая модель

data class MenuUiState(
    val username: String? = null,
    val userId: String? = null,
    val role: String? = null
)