package com.example.whisper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.whisper.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

// ChatViewModel.kt
class ChatViewModel : ViewModel() {
    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages

    private val _roomName = mutableStateOf("")
    val roomName: String by _roomName

    private var messagesListener: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()

    fun setRoom(roomId: String, name: String) {
        _roomName.value = name
        listenToMessages(roomId)
    }

    private fun listenToMessages(roomId: String) {
        messagesListener?.remove()

        messagesListener = db.collection("rooms")
            .document(roomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    _messages.clear()
                    _messages.addAll(querySnapshot.documents.map { doc ->
                        Message(
                            id = doc.id,
                            content = doc.getString("content") ?: "",
                            senderName = doc.getString("senderName") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            tripcode = doc.getString("tripcode")
                        )
                    })
                }
            }
    }

    fun sendMessage(roomId: String, content: String, senderName: String) {
        if (content.isBlank() || senderName.isBlank()) return

        val message = hashMapOf(
            "content" to content,
            "senderName" to senderName,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("rooms")
            .document(roomId)
            .collection("messages")
            .add(message)
    }

    override fun onCleared() {
        super.onCleared()
        messagesListener?.remove()
    }
}