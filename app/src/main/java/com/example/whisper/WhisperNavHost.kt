package com.example.whisper

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.whisper.data.local.DataStoreManager
import com.example.whisper.data.network.NetworkConnectivityChecker
import com.example.whisper.ui.screens.chat.ChatScreen
import com.example.whisper.ui.screens.create.CreateScreen
import com.example.whisper.ui.screens.explore.ExploreScreen
import com.example.whisper.ui.screens.join.JoinScreen
import com.example.whisper.ui.screens.main.MainScreen
import com.example.whisper.ui.screens.settings.SettingsScreen
import com.example.whisper.viewmodel.ExploreViewModel
import com.example.whisper.viewmodel.MainViewModel

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Join : Screen("join")
    data object Create : Screen("create")
    data object Settings : Screen("settings")
    data object Chat : Screen("chat/{roomId}/{roomName}?roomCode={roomCode}") {
        fun createRoute(roomId: String, roomName: String): String =
            "chat/$roomId/${roomName}"
    }
    data object Explore : Screen("explore/{roomId}/{roomName}")
}

@Composable
fun WhisperNavHost(
    dataStoreManager: DataStoreManager,
    networkChecker: NetworkConnectivityChecker,
    navController: NavHostController = rememberNavController()
) {
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModel.Factory(dataStoreManager, networkChecker)
    )

    val exploreViewModel: ExploreViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = mainViewModel,
                onNavigateToJoin = { navController.navigate(Screen.Join.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToChat = { roomId, roomName, roomCode ->
                    navController.navigate(
                        Screen.Chat.createRoute(roomId, roomName) +
                                "?roomCode=$roomCode"  // Add as query parameter
                    )
                },
                onNavigateToExplore = { navController.navigate(Screen.Explore.route)}
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
                navArgument("roomName") { type = NavType.StringType },
                navArgument("roomCode") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            ChatScreen(
                roomId = backStackEntry.arguments?.getString("roomId") ?: "",
                roomName = backStackEntry.arguments?.getString("roomName") ?: "",
                onNavigateUp = { navController.navigate(Screen.Main.route) },
                roomCode = roomCode,
                dataStoreManager = dataStoreManager
            )
        }

        composable(Screen.Explore.route) {
            ExploreScreen(
                exploreViewModel,
                mainViewModel,
                onNavigateToChat = { roomId, roomName, roomCode ->
                    navController.navigate(
                        Screen.Chat.createRoute(roomId, roomName) +
                                "?roomCode=$roomCode"  // Add as query parameter
                    )
                },
                onNavigateUp = { navController.navigate(Screen.Main.route) },
            )
        }

    }
}