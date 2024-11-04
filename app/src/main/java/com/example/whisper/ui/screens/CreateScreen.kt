package com.example.whisper.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whisper.ui.dialogs.DateTimePickerDialog
import com.example.whisper.viewmodel.MainViewModel
import com.example.whisper.utils.formatDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(viewModel: MainViewModel = viewModel(), onNavigateUp: () -> Unit) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }

    var showDateTimePicker by remember { mutableStateOf(false)}
    var expirationDateTime by remember { mutableStateOf<Long?>(null) }

    if (showDateTimePicker) {
        DateTimePickerDialog(
            onDateTimeSelected = {
                expirationDateTime = it
                showDateTimePicker = false
            },
            onDismiss = { showDateTimePicker = false }
        )
    }
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
                    IconButton(onClick = onNavigateUp) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expirationDateTime != null) {
                        "Expires: ${formatDateTime(expirationDateTime!!)}"
                    } else {
                        "Select expiration date and time"
                    }
                )
                TextButton(onClick = { showDateTimePicker = true }) {
                    Text(if (expirationDateTime == null) "Select" else "Change")
                }
            }

            Button(
                onClick = {
                    if (name.isBlank())
                    {
                        Toast.makeText(context, "Please enter a name.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (expirationDateTime == null)
                    {
                        Toast.makeText(context, "Please select a date.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.createRoom(
                        name = name,
                        expiresAt = expirationDateTime as Long,
                        onSuccess = { onNavigateUp.invoke()
                        },
                        // Yup! Passing in nothing. :)
                        onError = {}
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}