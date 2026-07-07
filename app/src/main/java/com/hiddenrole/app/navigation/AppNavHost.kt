package com.hiddenrole.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hiddenrole.app.state.GameStateHolder
import com.hiddenrole.app.ui.screens.GameConfigScreen
import com.hiddenrole.app.ui.screens.GameScreen
import com.hiddenrole.app.ui.screens.HistoryScreen
import com.hiddenrole.app.ui.screens.HomeScreen
import com.hiddenrole.app.ui.screens.PlayerSetupScreen
import com.hiddenrole.app.ui.screens.PresetBuilderScreen
import com.hiddenrole.app.ui.screens.RevealScreen

object Routes {
    const val HOME = "home"
    const val PRESET_BUILDER = "preset_builder/{presetId}"
    const val PLAYERS = "players"
    const val GAME_CONFIG = "game_config"
    const val REVEAL = "reveal"
    const val GAME = "game"
    const val HISTORY = "history"

    fun presetBuilder(presetId: String) = "preset_builder/$presetId"
}

@Composable
fun AppNavHost(navController: NavHostController, state: GameStateHolder) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                state = state,
                onCreatePreset = { navController.navigate(Routes.presetBuilder("new")) },
                onEditPreset = { preset -> navController.navigate(Routes.presetBuilder(preset.id)) },
                onPlayPreset = { preset ->
                    state.selectPresetToPlay(preset)
                    navController.navigate(Routes.PLAYERS)
                },
                onOpenHistory = { navController.navigate(Routes.HISTORY) }
            )
        }

        composable(
            Routes.PRESET_BUILDER,
            arguments = listOf(navArgument("presetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val presetId = backStackEntry.arguments?.getString("presetId") ?: "new"
            PresetBuilderScreen(
                state = state,
                presetId = presetId,
                onDone = { navController.popBackStack() }
            )
        }

        composable(Routes.PLAYERS) {
            PlayerSetupScreen(
                state = state,
                onNext = { navController.navigate(Routes.GAME_CONFIG) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.GAME_CONFIG) {
            GameConfigScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onAssign = {
                    state.assignRoles()
                    navController.navigate(Routes.REVEAL)
                }
            )
        }

        composable(Routes.REVEAL) {
            RevealScreen(
                state = state,
                onFinishReveal = {
                    navController.navigate(Routes.GAME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.GAME) {
            GameScreen(
                state = state,
                onGameFinished = {
                    state.resetGame()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                state = state,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
