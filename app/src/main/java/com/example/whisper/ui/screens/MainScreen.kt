package com.example.whisper.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.whisper.DataStore.chatRooms
import com.example.whisper.models.ChatRoom
import com.example.whisper.models.ChatRoomUtils.formatTimestampDate
import com.example.whisper.models.ChatRoomUtils.formatTimestampHr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    val chats = remember { chatRooms }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Whisper") },
                actions = {
                    IconButton(onClick = {
                        Log.d("SettingsScreen", "Icon clicked. Attempting to navigate to SettingsScreen")
                        try {
                            navController.navigate("settings")
                        } catch (e: Exception) {
                            Log.e("MainScreen", "Navigation to Settings failed", e)
                            Toast.makeText(context, "Navigation failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "SettingsScreen icon"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.d("MainScreen", "FAB clicked. Attempting to navigate to createJoin")
                    try {
                        navController.navigate("join")
                    } catch (e: Exception) {
                        Log.e("MainScreen", "Navigation failed", e)
                        Toast.makeText(context, "Navigation failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create or Join Chat")
            }
        }
    ) { padding ->
        if (chats.isEmpty()) {
            // Display a message when there are no chat rooms
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text(
                    text = "No chat rooms available. \nCreate or join a room to get started!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(chats) { chatRoom ->
                        ChatListItem(chatRoom) {
                            navController.navigate("chat/${chatRoom.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItem(chatRoom: ChatRoom, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text(text = chatRoom.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = formatTimestampDate(chatRoom.expires.toEpochMilli()), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(rememberNavController())
}