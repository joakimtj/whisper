package com.example.whisper

// App.kt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.whisper.WhisperNavHost

// App.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val context = LocalContext.current
    val application = context.applicationContext as WhisperApplication

    WhisperNavHost(dataStoreManager = application.container.dataStoreManager)
}
