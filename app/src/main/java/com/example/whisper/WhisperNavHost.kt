package com.example.whisper

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.whisper.ui.screens.*

@Composable
fun WhisperNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            Log.d("Navigation", "Navigating to MainScreen")
            MainScreen(navController)
        }
        composable("chat/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            Log.d("Navigation", "Navigating to ChatScreen with roomId: $roomId")
            ChatScreen(roomId = roomId ?: "")
        }
        composable("settings") {
            Log.d("Navigation", "Navigating to SettingsScreen")
            // https://developer.android.com/develop/ui/compose/components/app-bars-navigate
            SettingsScreen(){navController.popBackStack()}
        }
        composable("createJoin") {
            Log.d("Navigation", "Navigating to CreateJoinScreen")
            CreateJoinScreen(navController)
        }
    }
}