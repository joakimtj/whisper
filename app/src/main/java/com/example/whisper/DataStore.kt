package com.example.whisper

import android.util.Log
import com.example.whisper.models.ChatMessage
import com.example.whisper.models.ChatRoom
import com.example.whisper.models.Settings
import com.example.whisper.models.User

// TODO: Integrate with Firebase once most app functionality is finished.

object DataStore {
    var currentUser: User? = null // Not sure what this will be used for when...
    var settings: Settings = Settings() // ...settings contains display name and trip
    val chatRooms: MutableList<ChatRoom> = mutableListOf()

    fun createChatRoom(roomId: String, name: String, expires: String) {
        val newRoom = ChatRoom(roomId, name, expires = expires)
        addChatRoom(newRoom)
    }

    fun addChatRoom(room: ChatRoom) {
        chatRooms.add(room)
    }

    fun hasChatRoom(id: String): Boolean {
        return chatRooms.any { it.id == id }
    }

    fun getChatRoom(id: String): ChatRoom? {
        return chatRooms.find { it.id == id }
    }

    fun addMessageToChatRoom(roomId: String, message: ChatMessage) {
        getChatRoom(roomId)?.messages?.add(message)
    }

    fun updateChatRoom(updatedRoom: ChatRoom) {
        val index = chatRooms.indexOfFirst { it.id == updatedRoom.id }
        if (index != -1) {
            // Update existing room
            chatRooms[index] = updatedRoom
            Log.d("DataStore", "ChatRoom updated. ID: ${updatedRoom.id}, Total messages: ${updatedRoom.messages.size}")
        } else {
            // Add new room
            chatRooms.add(updatedRoom)
            Log.d("DataStore", "New ChatRoom added. ID: ${updatedRoom.id}")
        }
    }
}