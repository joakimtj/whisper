package com.example.whisper.ui.screens.join

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whisper.R
import com.example.whisper.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinScreen(viewModel: MainViewModel = viewModel(),
               onNavigateUp: () -> Unit,
               onNavigateCreate: () -> Unit)
{
    val context = LocalContext.current
    var code by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar =
        {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(stringResource(R.string.create_or_join)) },
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
                value = code,
                onValueChange = { code = it },
                label = { Text(stringResource(R.string.enter_code)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (code.isBlank())
                    {
                        Toast.makeText(context, "Please enter a code.",
                            Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.joinRoom(
                        code,
                        onError = {
                                onNavigateCreate()
                        },
                        onSuccess = {
                            onNavigateUp()
                        }
                    )
                }
            ) {
                Text(stringResource(R.string.join_or_create))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Or")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "These things - they take time.",
                        Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Scan QR Code")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.join_random))
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.joinRandomRoom(
                        onSuccess = { onNavigateUp() },
                        onError = {
                            errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.spin_room))
            }
        }

    }
}
