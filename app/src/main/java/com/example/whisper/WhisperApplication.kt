package com.example.whisper

import android.app.Application
import com.example.whisper.di.AppContainer

class WhisperApplication : Application() {
    // Instance of AppContainer that will be used by all activities/composables
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}