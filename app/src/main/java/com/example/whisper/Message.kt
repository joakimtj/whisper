package com.example.whisper

data class Message(
    val id: String = "",
    val content: String = "",
    val senderName: String = "",
    val timestamp: Long = 0,
    val tripcode: String? = null
)