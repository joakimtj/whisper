package com.example.whisper

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

class LocalStorage(context: Context) {
    private val TAG = "LocalStorage"
    private val prefs: SharedPreferences = context.getSharedPreferences("whisper_prefs", Context.MODE_PRIVATE)

    fun addJoinedRoom(roomId: String) {
        val existingRooms = getJoinedRooms()
        Log.d(TAG, "Current rooms before adding: $existingRooms")

        val joinedRooms = existingRooms.toMutableSet()
        joinedRooms.add(roomId)

        prefs.edit {
            putStringSet("joined_rooms", joinedRooms)
            commit() // Force immediate write
        }

        // Verify the room was added
        val updatedRooms = getJoinedRooms()
        Log.d(TAG, "Rooms after adding: $updatedRooms")
        if (!updatedRooms.contains(roomId)) {
            Log.e(TAG, "Failed to add room to storage!")
        }
    }

    fun removeJoinedRoom(roomId: String) {
        Log.d(TAG, "Removing room: $roomId")
        val joinedRooms = getJoinedRooms().toMutableSet()
        joinedRooms.remove(roomId)
        prefs.edit {
            putStringSet("joined_rooms", joinedRooms)
            commit() // Force immediate write
        }
        Log.d(TAG, "Rooms after removal: ${getJoinedRooms()}")
    }

    fun getJoinedRooms(): Set<String> {
        val rooms = prefs.getStringSet("joined_rooms", emptySet()) ?: emptySet()
        Log.d(TAG, "Getting joined rooms: $rooms")
        return rooms
    }
}