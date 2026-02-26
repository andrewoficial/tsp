package ru.igm.tsp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.igm.tsp.data.local.SessionManager
import ru.igm.tsp.data.repository.MessageRepositoryImpl
import ru.igm.tsp.data.remote.WebSocketRepository
import ru.igm.tsp.domain.factory.MessageFactory
import ru.igm.tsp.domain.model.ConnectionState
import ru.igm.tsp.domain.model.message.ChatMessage


data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val connectionStatus: ConnectionState = ConnectionState.Disconnected
)

