package com.example.whisper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJoinScreen(navController: NavController) {
    var code by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Create or Join Chat") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Enter Code") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (code.isNotBlank()) {
                        navController.navigate("chat/$code")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create or Join Chat")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Or")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Implement QR code scanning */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Scan QR Code")
            }
        }
    }
}