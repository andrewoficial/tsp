package ru.igm.tsp.domain.model

data class Content(
    val type: String, // "text", "command" и т.д.
    val body: String,
    val value: String = "", // для команд
    val provider: String = "",
    val language: String = "",
    val elements: List<Any> = emptyList()
)