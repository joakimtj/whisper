package com.example.whisper.models

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale

data class ChatRoom(
    val id: String,
    val name: String,
    val messages: MutableList<ChatMessage> = mutableListOf(),
    val expires: Instant  // Using Instant as the recommended type
)

// Helper functions for working with expiration
object ChatRoomUtils {

    fun isExpired(chatRoom: ChatRoom): Boolean {
        return Instant.now().isAfter(chatRoom.expires)
    }

    fun setExpirationDays(days: Long): Instant {
        return Instant.now().plusSeconds(days * 24 * 60 * 60)
    }

    fun formatExpiration(expires: Instant): String {
        val localDateTime = LocalDateTime.ofInstant(expires, ZoneId.systemDefault())
        return localDateTime.toString()
    }

    fun formatTimestampHr(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }
    fun formatTimestampDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy 'at' HH:mm", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }

}

data class ChatMessage(
    val id: String,
    val senderId: String,
    val content: String,
    val timestamp: Long,
    val isSentByUser: Boolean
)