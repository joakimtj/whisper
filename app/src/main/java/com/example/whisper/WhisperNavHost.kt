package com.example.whisper

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.whisper.ui.screens.*

@Composable
fun WhisperNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            Log.d("Navigation", "Navigating to MainScreen")
            MainScreen(navController)
        }
        composable("chat/{roomId}", arguments = listOf(navArgument("roomId") { type = NavType.StringType })) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            Log.d("Navigation", "Navigating to ChatScreen with roomId: $roomId")
            ChatScreen(
                roomId = roomId ?: "",
                navigateBack = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
        composable("settings") {
            Log.d("Navigation", "Navigating to SettingsScreen")
            // https://developer.android.com/develop/ui/compose/components/app-bars-navigate
            SettingsScreen(){navController.popBackStack()}
        }
        composable("join") {
            Log.d("Navigation", "Navigating to JoinScreen")
            JoinScreen(navController){navController.popBackStack()}
        }
        composable("create", arguments = listOf(navArgument("roomId") {type = NavType.StringType})) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            Log.d("Navigation", "Navigating to CreateScreen")
            CreateScreen(
                roomId = roomId ?: "",
                navController = navController,
                navigateBack = { navController.popBackStack() }
            )
        }

    }
}