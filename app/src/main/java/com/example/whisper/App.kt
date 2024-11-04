package com.example.whisper

// App.kt
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

// App.kt
@Composable
fun App() {
    val context = LocalContext.current
    val application = context.applicationContext as WhisperApplication

    WhisperNavHost(dataStoreManager = application.container.dataStoreManager)
}
