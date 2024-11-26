package com.example.whisper.di

import android.content.Context
import com.example.whisper.data.local.DataStoreManager
import com.example.whisper.data.network.NetworkConnectivityChecker

// Dependency Injection

class AppContainer(applicationContext: Context) {
    val dataStoreManager = DataStoreManager(applicationContext)
    val networkChecker = NetworkConnectivityChecker(applicationContext)
}