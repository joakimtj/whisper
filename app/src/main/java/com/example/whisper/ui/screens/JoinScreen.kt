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
import com.example.whisper.DataStore.hasChatRoom

/*
*   For the alpha we will only check if a chatroom exists in memory
*   When we implement Firebase ofc we will have to check against the backend
*/

// TODO: If a chatroom does not exist, navigate user to create screen to input name + expiration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinScreen(navController: NavController, navigateBack: () -> Unit) {
    var code by remember { mutableStateOf("") }
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
                title = { Text("Create or Join Chat") },
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
                value = code,
                onValueChange = { code = it },
                label = { Text("Enter Code") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (code.isBlank())
                        return@Button // Exit function early
                    if (hasChatRoom(code))
                        navController.navigate(code) // Just for now.
                    navController.navigate("create")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create or Join")
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

@Preview
@Composable
fun JoinScreenPreview() {
    JoinScreen(rememberNavController(), navigateBack = {})
}