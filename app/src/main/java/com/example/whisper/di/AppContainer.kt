package com.example.whisper.di

import android.content.Context
import com.example.whisper.data.local.DataStoreManager

// Dependency Injection

class AppContainer(applicationContext: Context) {
    val dataStoreManager = DataStoreManager(applicationContext)
}