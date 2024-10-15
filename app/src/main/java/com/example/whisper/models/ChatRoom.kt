package com.example.whisper.models

data class ChatRoom(
    val id: String,
    val name: String,
    val messages: MutableList<ChatMessage> = mutableListOf(),
    val expires: String
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val content: String,
    val timestamp: Long,
    val isSentByUser: Boolean
)