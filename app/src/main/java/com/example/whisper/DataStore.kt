package com.example.whisper

import com.example.whisper.models.ChatMessage
import com.example.whisper.models.ChatRoom
import com.example.whisper.models.Settings
import com.example.whisper.models.User

object DataStore {
    var currentUser: User? = null
    var settings: Settings = Settings()
    val chatRooms: MutableList<ChatRoom> = mutableListOf()

    fun addChatRoom(room: ChatRoom) {
        chatRooms.add(room)
    }

    fun getChatRoom(id: String): ChatRoom? {
        return chatRooms.find { it.id == id }
    }

    fun addMessageToChatRoom(roomId: String, message: ChatMessage) {
        getChatRoom(roomId)?.messages?.add(message)
    }
}