package com.example.whisper

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.whisper.data.local.DataStoreManager
import com.example.whisper.ui.screens.ChatScreen
import com.example.whisper.ui.screens.CreateScreen
import com.example.whisper.ui.screens.JoinScreen
import com.example.whisper.ui.screens.MainScreen
import com.example.whisper.ui.screens.SettingsScreen
import com.example.whisper.viewmodel.MainViewModel

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Join : Screen("join")
    data object Create : Screen("create")
    data object Settings : Screen("settings")
    data object Chat : Screen("chat/{roomId}/{roomName}") {
        fun createRoute(roomId: String, roomName: String): String =
            "chat/$roomId/${roomName}"
    }
}

@Composable
fun WhisperNavHost(
    dataStoreManager: DataStoreManager,
    navController: NavHostController = rememberNavController()
) {
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModel.Factory(dataStoreManager)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = mainViewModel,
                onNavigateToJoin = { navController.navigate(Screen.Join.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToChat = { roomId, roomName ->
                    navController.navigate(Screen.Chat.createRoute(roomId, roomName))
                }
            )
        }

        composable(Screen.Create.route) {
            CreateScreen(
                viewModel = mainViewModel,
                onNavigateUp = { navController.navigate(Screen.Main.route)}
            )
        }

        composable(Screen.Join.route) {
            JoinScreen(
                viewModel = mainViewModel,
                onNavigateUp = { navController.popBackStack() },
                onNavigateCreate = { navController.navigate(Screen.Create.route)}
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateUp = { navController.popBackStack() },
                dataStoreManager
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType },
                navArgument("roomName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            ChatScreen(
                roomId = backStackEntry.arguments?.getString("roomId") ?: "",
                roomName = backStackEntry.arguments?.getString("roomName") ?: "",
                onNavigateUp = { navController.popBackStack() },
                dataStoreManager = dataStoreManager
            )
        }
    }
}