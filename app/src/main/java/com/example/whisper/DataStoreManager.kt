package com.example.whisper

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class DataStoreManager(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chat_rooms")

    // Store room IDs and their details separately
    private object PreferencesKeys {
        val ROOM_IDS = stringPreferencesKey("room_ids")
        fun roomName(id: String) = stringPreferencesKey("room_${id}_name")
        fun roomCode(id: String) = stringPreferencesKey("room_${id}_code")
        fun roomCreatedAt(id: String) = longPreferencesKey("room_${id}_created_at")
        fun roomExpiresAt(id: String) = longPreferencesKey("room_${id}_expires_at")
        fun roomLastActivity(id: String) = longPreferencesKey("room_${id}_last_activity")
    }

    suspend fun saveRoom(room: RoomData) {
        context.dataStore.edit { preferences ->
            // Get current room IDs
            val currentIds =
                preferences[PreferencesKeys.ROOM_IDS]?.split(",")?.toMutableSet() ?: mutableSetOf()
            currentIds.add(room.id)

            // Save updated room IDs
            preferences[PreferencesKeys.ROOM_IDS] = currentIds.joinToString(",")

            // Save room details
            preferences[PreferencesKeys.roomName(room.id)] = room.name
            preferences[PreferencesKeys.roomCode(room.id)] = room.code
            preferences[PreferencesKeys.roomCreatedAt(room.id)] = room.createdAt
            preferences[PreferencesKeys.roomExpiresAt(room.id)] = room.expiresAt
            preferences[PreferencesKeys.roomLastActivity(room.id)] = room.lastActivity

            Log.d("Timestamp", "DataStore saving expiration: ${formatDateTime(room.expiresAt)}")
        }
    }

        suspend fun removeRoom(roomId: String) {
            context.dataStore.edit { preferences ->
                // Remove from room IDs
                val currentIds = preferences[PreferencesKeys.ROOM_IDS]?.split(",")?.toMutableSet()
                    ?: mutableSetOf()
                currentIds.remove(roomId)
                preferences[PreferencesKeys.ROOM_IDS] = currentIds.joinToString(",")

                // Remove room details
                preferences.remove(PreferencesKeys.roomName(roomId))
                preferences.remove(PreferencesKeys.roomCode(roomId))
                preferences.remove(PreferencesKeys.roomLastActivity(roomId))
            }
        }

        suspend fun getJoinedRooms(): List<RoomData> {
            val preferences = context.dataStore.data.first()
            val roomIds = preferences[PreferencesKeys.ROOM_IDS]?.split(",") ?: emptyList()

            return roomIds.filter { it.isNotEmpty() }.map { id ->
                val expiration = preferences[PreferencesKeys.roomExpiresAt(id)] ?: 0L
                Log.d(
                    "Timestamp",
                    "DataStore loading expiration for $id: ${formatDateTime(expiration)}"
                )

                RoomData(
                    id = id,
                    name = preferences[PreferencesKeys.roomName(id)] ?: "",
                    code = preferences[PreferencesKeys.roomCode(id)] ?: "",
                    createdAt = preferences[PreferencesKeys.roomCreatedAt(id)] ?: 0L,
                    expiresAt = expiration,
                    lastActivity = preferences[PreferencesKeys.roomLastActivity(id)] ?: 0L
                )
            }
        }
    }
