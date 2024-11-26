package com.example.whisper.ui.screens.main

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whisper.R
import com.example.whisper.ui.screens.main.components.RoomList
import com.example.whisper.ui.dialogs.CreateRoomDialog
import com.example.whisper.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToJoin: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToChat: (String, String, String) -> Unit,
    onNavigateToExplore: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }

    val isNetworkAvailable = viewModel.isNetworkAvailable()

    Log.d("NETWORK", isNetworkAvailable.toString())

    if (!isNetworkAvailable) {

        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(
                message = "No internet connection",
                duration = SnackbarDuration.Indefinite
            )
        }
    }

    // Dialogs -- deprecated
    var showJoinDialog by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text(stringResource(id = R.string.app_name)) },
                navigationIcon = {
                    TextButton(
                        onClick = { onNavigateToExplore() }
                    ) {
                        Text(
                            stringResource(R.string.discover),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToSettings() }) {
                        Icon(Icons.Default.Settings, "Edit settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onNavigateToJoin()
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
                        text = stringResource(id = R.string.no_joined_rooms),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.create_join_suggestion),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                RoomList(
                    rooms = viewModel.joinedRooms,
                    onLeaveRoom = { roomId -> viewModel.leaveRoom(roomId) },
                    onRoomClick = { room -> onNavigateToChat(room.id, room.name, room.code) },
                    modifier = Modifier.fillMaxSize(),
                    isPublic = false
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