package com.example.whisper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whisper.data.local.DataStoreManager
import com.example.whisper.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    dataStoreManager: DataStoreManager
) {
    var displayName by remember { mutableStateOf("") }
    var tripcode by remember { mutableStateOf("") }
    var generatedTripcode by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        dataStoreManager.getUserName().collect { name ->
            displayName = name
        }
    }

    LaunchedEffect(Unit) {
        dataStoreManager.getTripcodeInput().collect { savedTripcode ->
            tripcode = savedTripcode
        }
    }

    LaunchedEffect(Unit) {
        dataStoreManager.getTripcode().collect { code ->
            generatedTripcode = code
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
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
            if (generatedTripcode.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your identifier: !${generatedTripcode}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        dataStoreManager.saveUserName(displayName)
                        if (tripcode.isNotEmpty()) {
                            dataStoreManager.saveTripcode(tripcode)
                        }

                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save")
            }
        }
    }
}