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
            "lastActivity" to now
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
                    lastActivity = now.toDate().time
                )
                _joinedRooms.add(newRoom)
                saveRoom(newRoom)
                onSuccess(code)
            }
            .addOnFailureListener {
                onError("Failed to create room: ${it.message}")
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

                val room = RoomData(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    code = document.getString("code") ?: "",
                    createdAt = createdAt,
                    expiresAt = expiresAt,
                    lastActivity = lastActivity
                )

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
