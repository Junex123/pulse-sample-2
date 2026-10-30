package com.example.data.model

data class AiCoachMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val quickPrompts: List<String> = emptyList(),
    val isError: Boolean = false
)

enum class MessageSender {
    USER,
    COACH
}
