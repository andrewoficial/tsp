package ru.igm.tsp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.igm.tsp.data.remote.RetrofitInstance
import ru.igm.tsp.data.repository.AuthRepository
import ru.igm.tsp.domain.model.LoginRequest

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(RetrofitInstance.api)

    // Приватный MutableStateFlow для изменения состояния
    private val _uiState = MutableStateFlow(AuthViewState())
    // Публичный StateFlow, на который будет подписан UI (только для чтения)
    val uiState: StateFlow<AuthViewState> = _uiState.asStateFlow()

    init {
        loadCaptcha()
    }

    fun loadCaptcha() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.getCaptcha()
            result.fold(
                onSuccess = { captcha ->
                    _uiState.value = _uiState.value.copy(isLoading = false, captchaData = captcha)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Не удалось загрузить капчу: ${error.message}"
                    )
                }
            )
        }
    }

    fun login(username: String, password: String, captchaValue: String) {
        val currentCaptcha = _uiState.value.captchaData
        if (currentCaptcha == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Капча не загружена. Попробуйте еще раз.")
            return
        }

        val request = LoginRequest(
            username = username,
            password = password,
            captchaId = currentCaptcha.captchaId,
            captchaValue = captchaValue
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.login(request)
            result.fold(
                onSuccess = { loginResponse ->
                    // TODO: Здесь нужно сохранить токен и перейти на экран чата
                    println("Успешный вход! Токен: ${loginResponse.token}")
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoginSuccessful = true)
                },
                onFailure = { error ->
                    // При ошибке логина перезагружаем капчу
                    loadCaptcha()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Ошибка входа: ${error.message}"
                    )
                }
            )
        }
    }
}