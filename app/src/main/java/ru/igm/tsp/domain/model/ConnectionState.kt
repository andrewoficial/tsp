package ru.igm.tsp.domain.model

// Sealed class - это продвинутый enum. Позволяет хранить данные для каждого состояния.
sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}