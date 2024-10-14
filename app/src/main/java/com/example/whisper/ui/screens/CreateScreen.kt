package com.example.whisper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.whisper.DataStore.createChatRoom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(roomId: String, navController: NavController, navigateBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var expires by remember { mutableStateOf("") }
    Scaffold(
        topBar =
        {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Select name and expiration") },
                navigationIcon = {
                    // https://developer.android.com/develop/ui/compose/components/app-bars-navigate
                    // Passed navController.popBackStack() in WhisperNavHost
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back from Create/Join."
                        )
                    }
                } )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Enter name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = expires,
                onValueChange = { expires = it },
                label = { Text("Enter expiration") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isBlank())
                        return@Button
                    if (expires.isBlank())
                        return@Button
                    createChatRoom(roomId, name, expires)
                    // Then navigate to it
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }

    }
}

@Preview
@Composable
fun CreateScreenPreview() {
    CreateScreen("", rememberNavController(), navigateBack = {})
}