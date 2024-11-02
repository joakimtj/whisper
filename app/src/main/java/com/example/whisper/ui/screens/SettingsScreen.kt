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

// TODO: Integrate with Settings var in DataStore
// TODO: Current display-name reflected in TextField
// Tripcode will look like this:
// String: "ThisIsHashed" TripCode: d8091mfak23
// Not a real example, just to illustrate the idea.
// TODO: Implement tripcode hashing and have it update in a separate text element
// Probably need to update the Settings data class to have a 'hash'-field for the hashed code.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit
) {
    var displayName by remember { mutableStateOf("") }
    var tripcode by remember { mutableStateOf("") }

    Scaffold(
        topBar =
        {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Settings") },
                navigationIcon = {
                    // https://developer.android.com/develop/ui/compose/components/app-bars-navigate
                    // Passed navController.popBackStack() in WhisperNavHost
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                } )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            TextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = tripcode,
                onValueChange = { tripcode = it },
                label = { Text("Tripcode") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Save settings */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save")
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navigateBack = {})
}