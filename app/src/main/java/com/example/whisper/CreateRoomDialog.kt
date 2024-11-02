package com.example.whisper

// CreateRoomDialog.kt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var expirationDateTime by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Room") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Room Name") },
                    singleLine = true,
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    expirationDateTime?.let { expDateTime ->
                        onCreate(name, expDateTime)
                    }
                },
                enabled = name.isNotBlank() && expirationDateTime != null
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showDateTimePicker) {
        DateTimePickerDialog(
            onDateTimeSelected = {
                expirationDateTime = it
                showDateTimePicker = false
            },
            onDismiss = { showDateTimePicker = false }
        )
    }
}