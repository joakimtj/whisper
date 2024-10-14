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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    val chats = remember { mutableStateListOf(
        "Chat 1" to "Expires in 2 hours",
        "Chat 2" to "Expires in 1 day",
        "Chat 3" to "Expires in 30 minutes"
    ) }

    Scaffold(
        /*
        * https://developer.android.com/develop/ui/compose/components/app-bar
        * App bar code is based on the above documentation.
        * */
        topBar = { CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = { Text("Whisper") },
            // navigationIcon = { ... } Think this is reserved for navigate back stuff.
            actions = {
                IconButton(onClick = {
                    Log.d("SettingsScreen",
                        "Icon clicked. Attempting to navigate to SettingsScreen")
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
        ) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.d("MainScreen", "FAB clicked. Attempting to navigate to createJoin")
                    try {
                        navController.navigate("createJoin")
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

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(rememberNavController())
}