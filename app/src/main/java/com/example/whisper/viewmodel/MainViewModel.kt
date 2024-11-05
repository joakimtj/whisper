package com.example.whisper.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.whisper.data.local.DataStoreManager
import com.example.whisper.data.model.RoomData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.Date

// MainViewModel.kt
class MainViewModel(
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    private val _joinedRooms = mutableStateListOf<RoomData>()
    val joinedRooms: List<RoomData> = _joinedRooms

    private val db = FirebaseFirestore.getInstance()
    private val scope = viewModelScope

    init {
        loadJoinedRooms()
    }

    private fun loadJoinedRooms() {
        scope.launch {
            val savedRooms = dataStoreManager.getJoinedRooms()
            _joinedRooms.clear()
            _joinedRooms.addAll(savedRooms)
        }
    }

    private fun saveRoom(room: RoomData) {
        scope.launch {
            dataStoreManager.saveRoom(room)
        }
    }

    fun createRoom(
        name: String,
        expiresAt: Long,
        public: Boolean = false,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val now = Timestamp.now()
        val code = generateRoomCode()
        val room = hashMapOf(
            "name" to name,
            "code" to code,
            "createdAt" to now,
            "expiresAt" to Timestamp(Date(expiresAt)),
            "lastActivity" to now,
            "public" to public
        )

        db.collection("rooms")
            .add(room)
            .addOnSuccessListener { documentRef ->
                val newRoom = RoomData(
                    id = documentRef.id,
                    name = name,
                    code = code,
                    createdAt = now.toDate().time,
                    expiresAt = expiresAt,
                    lastActivity = now.toDate().time,
                    public = public
                )
                _joinedRooms.add(newRoom)
                saveRoom(newRoom)
                onSuccess(code)
            }
            .addOnFailureListener {
                onError("Failed to create room: ${it.message}")
            }
    }

    fun joinRandomRoom(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentTime = System.currentTimeMillis()

        db.collection("rooms")
            .whereEqualTo("public", true)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onError("No available public rooms found")
                    return@addOnSuccessListener
                }

                // Get random room from available rooms
                val randomIndex = (0 until documents.size()).random()
                val document = documents.documents[randomIndex]

                try {
                    // Safely get timestamps with null checks
                    val room = RoomData(
                        id = document.id,
                        name = document.getString("name")
                            ?: throw IllegalStateException("Room name is required"),
                        code = document.getString("code")
                            ?: throw IllegalStateException("Room code is required"),
                        createdAt = document.getTimestamp("createdAt")?.toDate()?.time
                            ?: currentTime,
                        expiresAt = document.getTimestamp("expiresAt")?.toDate()?.time
                            ?: throw IllegalStateException("Expiration time is required"),
                        lastActivity = document.getTimestamp("lastActivity")?.toDate()?.time
                            ?: currentTime,
                        public = document.getBoolean("public") ?: true
                    )

                    // Check if room is already joined
                    if (_joinedRooms.any { it.id == room.id }) {
                        onError("You're already in this room")
                        return@addOnSuccessListener
                    }

                    // Check room capacity if needed
                    document.getLong("memberCount")?.let { currentCount ->
                        val maxMembers = document.getLong("maxMembers") ?: 50
                        if (currentCount >= maxMembers) {
                            onError("Room is full")
                            return@addOnSuccessListener
                        }
                    }

                    // Update member count atomically
                    val roomRef = db.collection("rooms").document(room.id)
                    db.runTransaction { transaction ->
                        val snapshot = transaction.get(roomRef)
                        val currentCount = snapshot.getLong("memberCount") ?: 0
                        transaction.update(roomRef, "memberCount", currentCount + 1)
                    }.addOnSuccessListener {
                        // Add room to joined rooms and save locally
                        _joinedRooms.add(room)
                        saveRoom(room)
                        onSuccess()
                    }.addOnFailureListener { e ->
                        onError("Failed to join room: ${e.message}")
                    }

                } catch (e: IllegalStateException) {
                    onError("Invalid room data: ${e.message}")
                } catch (e: Exception) {
                    onError("Unexpected error: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                onError("Failed to fetch rooms: ${e.message}")
            }
    }

    fun joinRoom(code: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("rooms")
            .whereEqualTo("code", code)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onError("Room not found")
                    return@addOnSuccessListener
                }

                val document = documents.documents[0]

                // Safely get timestamps
                val createdAt = document.getTimestamp("createdAt")?.toDate()?.time
                    ?: System.currentTimeMillis()
                val expiresAt = document.getTimestamp("expiresAt")?.toDate()?.time
                    ?: System.currentTimeMillis()
                val lastActivity = document.getTimestamp("lastActivity")?.toDate()?.time
                    ?: System.currentTimeMillis()

                // By not using public attrib here, the chatroom list, will still show leave btn
                val room = RoomData(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    code = document.getString("code") ?: "",
                    createdAt = createdAt,
                    expiresAt = expiresAt,
                    lastActivity = lastActivity
                )

                // Check if room is already joined
                if (_joinedRooms.any { it.id == room.id }) {
                    onError("You're already in this room")
                    return@addOnSuccessListener
                }

                if (!_joinedRooms.any { it.id == room.id }) {
                    _joinedRooms.add(room)
                    saveRoom(room)
                    onSuccess()
                }
            }
            .addOnFailureListener {
                onError("Failed to join room: ${it.message}")
            }
    }

    private fun updateLastActivity(roomId: String) {
        db.collection("rooms")
            .document(roomId)
            .update("lastActivity", Timestamp.now())
    }


    fun leaveRoom(roomId: String) {
        scope.launch {
            dataStoreManager.removeRoom(roomId)
            _joinedRooms.removeAll { it.id == roomId }
        }
    }

    private fun generateRoomCode(): String {
        return (1..6)
            .map { ('A'..'Z').random() }
            .joinToString("")
    }

    class Factory(
        private val dataStoreManager: DataStoreManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(dataStoreManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
