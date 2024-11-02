package com.example.whisper

// App.kt
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.padding
import com.example.whisper.data.model.RoomData
import com.example.whisper.ui.dialogs.CreateRoomDialog
import com.example.whisper.ui.screens.ChatScreen
import com.example.whisper.ui.screens.JoinScreen
import com.example.whisper.viewmodel.MainViewModel


// App.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(viewModel: MainViewModel = viewModel()) {
    var currentRoom by remember { mutableStateOf<RoomData?>(null) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var joinScreen by remember { mutableStateOf(false) }

    if (currentRoom != null) {
        ChatScreen(
            roomId = currentRoom!!.id,
            roomName = currentRoom!!.name,
            onNavigateUp = { currentRoom = null }
        )
    } else if (joinScreen) {
        JoinScreen(viewModel, navigateBack = {joinScreen = false})
    }
    else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary),
                    title = { Text("Chat Rooms") },
                    actions = {
                        IconButton(onClick = { showJoinDialog = true }) {
                            Icon(Icons.Default.Add, "Join Room")
                        }
                        IconButton(onClick = { showCreateDialog = true }) {
                            Icon(Icons.Default.Create, "Create Room")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        joinScreen = true;
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create or Join Chat")
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (viewModel.joinedRooms.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No rooms joined yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Create a new room or join an existing one",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    RoomList(
                        rooms = viewModel.joinedRooms,
                        onLeaveRoom = { roomId -> viewModel.leaveRoom(roomId) },
                        onRoomClick = { room -> currentRoom = room },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Error dialog
                errorMessage?.let { message ->
                    AlertDialog(
                        onDismissRequest = { errorMessage = null },
                        title = { Text("Message") },
                        text = { Text(message) },
                        confirmButton = {
                            TextButton(onClick = { errorMessage = null }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }

        // Dialogs
        if (showJoinDialog) {
            JoinRoomDialog(
                onDismiss = { showJoinDialog = false },
                onJoin = { code ->
                    viewModel.joinRoom(
                        code = code,
                        onSuccess = { showJoinDialog = false },
                        onError = { error -> errorMessage = error }
                    )
                }
            )
        }

        if (showCreateDialog) {
            CreateRoomDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, expirationDate ->
                    viewModel.createRoom(
                        name = name,
                        expiresAt = expirationDate,
                        onSuccess = { code ->
                            showCreateDialog = false
                            errorMessage = "Room created! Code: $code"
                        },
                        onError = { error -> errorMessage = error }
                    )
                }
            )
        }
    }
}

@Composable
fun JoinRoomDialog(
    onDismiss: () -> Unit,
    onJoin: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Join Room") },
        text = {
            TextField(
                value = code,
                onValueChange = { code = it.uppercase() },
                label = { Text("Room Code") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onJoin(code) },
                enabled = code.length == 6
            ) {
                Text("Join")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

