package com.example.whisper.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class ChatRoom(
    val id: String,
    val name: String,
    val messages: MutableList<ChatMessage> = mutableListOf(),
    val expires: Instant  // Using Instant as the recommended type
)

// Helper functions for working with expiration
object ChatRoomUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun isExpired(chatRoom: ChatRoom): Boolean {
        return Instant.now().isAfter(chatRoom.expires)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setExpirationDays(days: Long): Instant {
        return Instant.now().plusSeconds(days * 24 * 60 * 60)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatExpiration(expires: Instant): String {
        val localDateTime = LocalDateTime.ofInstant(expires, ZoneId.systemDefault())
        return localDateTime.toString()
    }
}

data class ChatMessage(
    val id: String,
    val senderId: String,
    val content: String,
    val timestamp: Long,
    val isSentByUser: Boolean
)