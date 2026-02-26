package ru.igm.tsp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.igm.tsp.data.local.SessionManager

class MenuViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        // Просто читаем данные из SessionManager
        _uiState.value = MenuUiState(
            username = sessionManager.username,
            userId = sessionManager.userId,
            role = sessionManager.token // Просто для примера, можно показать роль
        )
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            sessionManager.clearSession()
            onLogoutSuccess() // Вызываем колбэк для навигации
        }
    }
}