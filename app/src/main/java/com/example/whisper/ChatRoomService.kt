package com.example.whisper

import com.example.whisper.models.ChatMessage
import com.example.whisper.models.ChatRoom
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatRoomService {
    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> = _chatRooms

    fun createChatRoom(name: String, expirationTime: Instant): ChatRoom {
        val newChatRoom = ChatRoom(
            id = generateUniqueId(),
            name = name,
            expires = expirationTime
        )
        _chatRooms.value += newChatRoom
        return newChatRoom
    }

    fun getChatRoom(id: String): ChatRoom? {
        return _chatRooms.value.find { it.id == id }
    }

    fun addMessage(chatRoomId: String, message: ChatMessage) {
        val updatedRooms = _chatRooms.value.map { room ->
            if (room.id == chatRoomId) {
                room.apply {
                    messages.add(message)
                }
            } else {
                room
            }
        }
        _chatRooms.value = updatedRooms
    }

    fun deleteChatRoom(id: String) {
        _chatRooms.value = _chatRooms.value.filter { it.id != id }
    }

    fun deleteExpiredChatRooms() {
        val currentTime = Instant.now()
        _chatRooms.value = _chatRooms.value.filter { it.expires.isAfter(currentTime) }
    }

    private fun generateUniqueId(): String {
        // Implement a method to generate a unique ID (e.g., UUID.randomUUID().toString())
        return ""  // Placeholder
    }
}