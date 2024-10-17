package com.example.whisper

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.whisper.models.ChatMessage
import com.example.whisper.models.ChatRoom
import com.example.whisper.models.Settings
import com.example.whisper.models.User
import java.time.Instant

// TODO: Integrate with Firebase once most app functionality is finished.

object DataStore {
    var currentUser: User? = null // Not sure what this will be used for when...
    var settings: Settings = Settings() // ...settings contains display name and trip
    val chatRooms: MutableList<ChatRoom> = mutableListOf()

    fun createChatRoom(roomId: String, name: String, expires: Long) {
        val expirationInstant = Instant.now().plusSeconds(expires)
        val newRoom = ChatRoom(roomId, name, expires = expirationInstant)
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
        val room = getChatRoom(roomId)
        if (room != null) {
            room.messages.add(message)
            updateChatRoom(room)
            Log.d("DataStore", "Message added to ChatRoom. RoomID: $roomId, Total messages: ${room.messages.size}")
        } else {
            Log.e("DataStore", "Failed to add message. ChatRoom not found. RoomID: $roomId")
        }
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