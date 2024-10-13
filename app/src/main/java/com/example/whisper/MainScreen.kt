package com.example.whisper

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val chats = remember { mutableStateListOf(
        "Chat 1" to "Expires in 2 hours",
        "Chat 2" to "Expires in 1 day",
        "Chat 3" to "Expires in 30 minutes"
    ) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Whisper") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("createJoin") }) {
                Icon(Icons.Default.Add, contentDescription = "Create or Join Chat")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(chats) { (chatName, expirationTime) ->
                ChatListItem(chatName, expirationTime) {
                    navController.navigate("chat/$chatName")
                }
            }
        }
    }
}

@Composable
fun ChatListItem(chatName: String, expirationTime: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = chatName, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = expirationTime, style = MaterialTheme.typography.bodyMedium)
        }
    }
}