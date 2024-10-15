package com.example.whisper.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.whisper.DataStore.getChatRoom
import com.example.whisper.models.ChatMessage

// TODO: Use ChatRoom from DataStore for messages and misc data.
// TODO: Separate incoming and outgoing messages into two columns.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(roomId: String, navigateBack: () -> Unit) {
    val chatRoom = getChatRoom(roomId)
    var message by remember { mutableStateOf("") }
    // Will look into the SnapshotStateMap thing it mentions here at some point.
    val messages = remember { mutableStateOf(chatRoom?.messages?.toMutableList() ?: mutableListOf()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(roomId) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Log.d("AddTestMessage", "Click AddTestMsg.")
                        try {
                            messages.value = messages.value.toMutableList().apply {
                                add(ChatMessage("1", "DEBUG", "TEST", System.currentTimeMillis(), false))
                            }
                            Log.d("AddTestMessage", "Test message added successfully. Total messages: ${messages.value.size}")
                        } catch (e: Exception) {
                            Log.e("AddTestMessage", "Adding test message failed.", e)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Test Message"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(messages.value) { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(if (msg.isSentByUser) Alignment.CenterEnd else Alignment.CenterStart)
                                .widthIn(max = 280.dp)
                                .background(
                                    color = if (msg.isSentByUser)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Text(
                                text = msg.content,
                                modifier = Modifier.padding(12.dp),
                                color = if (msg.isSentByUser)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message") }
                )
                IconButton(
                    onClick = {
                        if (message.isBlank()) return@IconButton
                        messages.value = messages.value.toMutableList().apply {
                            add(ChatMessage("0", "TEMPORARY", message, System.currentTimeMillis(), true))
                        }
                        message = ""
                        Log.d("SendMessage", "Message sent. Total messages: ${messages.value.size}")
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    ChatScreen("Blop Glarp Appreciation", navigateBack = {})
}