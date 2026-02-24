package ru.igm.tsp.presentation.viewmodel

import ru.igm.tsp.domain.model.CaptchaResponse

data class AuthViewState(
    val isLoading: Boolean = false,
    val captchaData: CaptchaResponse? = null,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)