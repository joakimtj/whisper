package com.example.whisper.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
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

// TODO: Change expiration to be a drop-down with sets of values.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(roomId: String, navController: NavController, navigateBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedExpirationOption by remember { mutableStateOf<ExpirationOption?>(null) }

    val expirationOptions = listOf(
        ExpirationOption("60 seconds", 60),
        ExpirationOption("24 hours", 24 * 60 * 60),
        ExpirationOption("7 days", 7 * 24 * 60 * 60),
        ExpirationOption("30 days", 30 * 24 * 60 * 60)
    )
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

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                TextField(
                    value = selectedExpirationOption?.label ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select expiration") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    expirationOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                selectedExpirationOption = option
                                expanded = false
                            }
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isBlank() || selectedExpirationOption == null)
                    {
                        return@Button
                    }
                    createChatRoom(roomId, name, selectedExpirationOption!!.seconds)
                    navController.navigate("chat/$roomId")
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

data class ExpirationOption(val label: String, val seconds: Long)