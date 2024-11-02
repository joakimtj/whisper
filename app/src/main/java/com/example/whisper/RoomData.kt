package com.example.whisper

data class RoomData (
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val createdAt: Long = 0,
    val expiresAt: Long = 0,
    val lastActivity: Long = 0
)