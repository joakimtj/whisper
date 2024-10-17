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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whisper.DataStore.addMessageToChatRoom
import com.example.whisper.DataStore.getChatRoom
import com.example.whisper.models.ChatMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// TODO: 'Style' messages to use display-name (if any)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(roomId: String, navigateBack: () -> Unit) {
    val chatRoom = remember { mutableStateOf(getChatRoom(roomId)) }
    var message by remember { mutableStateOf("") }
    val messages = chatRoom.value?.messages ?: emptyList()

    var forceUpdate by remember { mutableIntStateOf(0) }

    LaunchedEffect(forceUpdate) {
        chatRoom.value = getChatRoom(roomId)
    }

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
                            val testMessage = ChatMessage(
                                id = UUID.randomUUID().toString(),
                                senderId = "DEBUG",
                                content = "TEST",
                                timestamp = System.currentTimeMillis(),
                                isSentByUser = false
                            )
                            addMessageToChatRoom(roomId, testMessage)
                            // Refresh the chatRoom state to trigger recomposition
                            chatRoom.value = getChatRoom(roomId)
                            forceUpdate++ // Hacky fix for recomposition not triggering despite above comment
                            Log.d("AddTestMessage", "Test message added. Total messages: ${chatRoom.value?.messages?.size}")
                        } catch (e: Exception) {
                            Log.e("AddTestMessage", "Adding test message failed.", e)
                        }
                    })
                    {
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
                items(messages) { msg ->
                    MessageItem(msg)
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
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("Type a message") }
                )
                IconButton(
                    onClick = {
                        if (message.isBlank()) return@IconButton
                        val newMessage = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            senderId = "TEMPORARY",
                            content = message,
                            timestamp = System.currentTimeMillis(),
                            isSentByUser = true
                        )
                        addMessageToChatRoom(roomId, newMessage)
                        message = ""
                        forceUpdate++
                        Log.d("SendMessage", "Message sent. Total messages: ${chatRoom.value?.messages?.size}")
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun MessageItem(msg: ChatMessage) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .align(if (msg.isSentByUser) Alignment.CenterEnd else Alignment.CenterStart)
                .widthIn(max = 280.dp)
        ) {
            Box(
                modifier = Modifier
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
            Text(
                text = formatTimestamp(msg.timestamp),
                modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date(timestamp)
    return sdf.format(date)
}

@Preview
@Composable
fun ChatScreenPreview() {
    ChatScreen("Blop Glarp Appreciation", navigateBack = {})
}