package com.example.whisper.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.whisper.data.local.DataStoreManager
import com.example.whisper.data.model.RoomData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {
    private val _publicRooms = mutableStateListOf<RoomData>()
    val publicRooms: List<RoomData> = _publicRooms

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    init {
        Log.d("ExploreViewModel", "Attempt viewModel init")
        fetchPublicRooms()
    }

    fun fetchPublicRooms() {
        viewModelScope.launch {
            try {
                Log.d("Fetch", "Attempting to fetch")

                _isLoading.value = true
                _error.value = null
                _publicRooms.clear()

                val currentTime = System.currentTimeMillis()

                /*  Reintroduce these later I guess. (When collection no longer has invalid data?)
                    .whereGreaterThan("expiresAt", Timestamp(currentTime / 1000, 0))
                    .orderBy("expiresAt", Query.Direction.ASCENDING)
                    .orderBy("lastActivity", Query.Direction.DESCENDING)
                    .limit(50) // Limit to prevent loading too many rooms
                 */
                db.collection("rooms")
                    .whereEqualTo("public", true)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            _error.value = "No public rooms available"
                            return@addOnSuccessListener
                        }

                        val rooms = documents.mapNotNull { document ->
                            try {
                                RoomData(
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
                            } catch (e: Exception) {
                                // Log error but continue processing other rooms
                                Log.d("Error","Error parsing room document: ${e.message}")
                                null
                            }
                        }
                        Log.d("FETCH", "Size of rooms: ${rooms.size}")
                        _publicRooms.addAll(rooms)

                        // Set error if we filtered out all rooms due to parsing errors
                        if (_publicRooms.isEmpty()) {
                            _error.value = "No valid public rooms available"
                        }
                    }
                    .addOnFailureListener { e ->
                        _error.value = "Failed to fetch rooms: ${e.message}"
                    }

            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Function to retry fetching rooms
    fun retryFetch() {
        fetchPublicRooms()
    }

    // Function to refresh rooms (can be called from pull-to-refresh)
    fun refreshRooms() {
        fetchPublicRooms()
    }

    companion object {
        class Factory : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
                    return ExploreViewModel() as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}