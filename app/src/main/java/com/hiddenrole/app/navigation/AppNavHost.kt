package com.hiddenrole.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hiddenrole.app.state.GameStateHolder
import com.hiddenrole.app.ui.screens.AbilitiesScreen
import com.hiddenrole.app.ui.screens.GameConfigScreen
import com.hiddenrole.app.ui.screens.GameScreen
import com.hiddenrole.app.ui.screens.HistoryScreen
import com.hiddenrole.app.ui.screens.HomeScreen
import com.hiddenrole.app.ui.screens.PlayerSetupScreen
import com.hiddenrole.app.ui.screens.PresetBuilderScreen
import com.hiddenrole.app.ui.screens.RevealScreen
import com.hiddenrole.app.ui.screens.RoleTemplatesScreen
import com.hiddenrole.app.ui.screens.RosterScreen
import com.hiddenrole.app.ui.screens.ScenariosScreen
import com.hiddenrole.app.ui.screens.SettingsScreen
import com.hiddenrole.app.ui.screens.StatsScreen

object Routes {
    const val HOME = "home"
    const val SCENARIOS = "scenarios"
    const val SCENARIO_BUILDER = "scenario_builder/{presetId}"
    const val ROLE_TEMPLATES = "role_templates"
    const val ABILITIES = "abilities"
    const val ROSTER = "roster"
    const val GAME_PLAYERS = "game_players"
    const val GAME_CONFIG = "game_config"
    const val REVEAL = "reveal"
    const val GAME = "game"
    const val HISTORY = "history"
    const val STATS = "stats"
    const val SETTINGS = "settings"

    fun scenarioBuilder(presetId: String) = "scenario_builder/$presetId"
}

@Composable
fun AppNavHost(navController: NavHostController, state: GameStateHolder) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(state = state, onNavigate = { route -> navController.navigate(route) })
        }

        composable(Routes.SCENARIOS) {
            ScenariosScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onCreatePreset = { navController.navigate(Routes.scenarioBuilder("new")) },
                onEditPreset = { preset -> navController.navigate(Routes.scenarioBuilder(preset.id)) },
                onPlayPreset = { preset ->
                    state.selectPresetToPlay(preset)
                    navController.navigate(Routes.GAME_PLAYERS)
                },
                onOpenRoleTemplates = { navController.navigate(Routes.ROLE_TEMPLATES) },
                onOpenAbilities = { navController.navigate(Routes.ABILITIES) }
            )
        }

        composable(Routes.ROLE_TEMPLATES) {
            RoleTemplatesScreen(state = state, onBack = { navController.popBackStack() })
        }

        composable(Routes.ABILITIES) {
            AbilitiesScreen(state = state, onBack = { navController.popBackStack() })
        }

        composable(
            Routes.SCENARIO_BUILDER,
            arguments = listOf(navArgument("presetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val presetId = backStackEntry.arguments?.getString("presetId") ?: "new"
            PresetBuilderScreen(
                state = state,
                presetId = presetId,
                onDone = { navController.popBackStack() }
            )
        }

        composable(Routes.ROSTER) {
            RosterScreen(state = state, onBack = { navController.popBackStack() })
        }

        composable(Routes.GAME_PLAYERS) {
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
            HistoryScreen(state = state, onBack = { navController.popBackStack() })
        }

        composable(Routes.STATS) {
            StatsScreen(state = state, onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(state = state, onBack = { navController.popBackStack() })
        }
    }
}
