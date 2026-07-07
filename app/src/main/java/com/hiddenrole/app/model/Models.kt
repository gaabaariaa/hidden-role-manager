package com.hiddenrole.app.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.Serializable

@Serializable
data class TeamDef(
    val id: String,
    val name: String
)

@Serializable
data class RoleDef(
    val id: String,
    val name: String,
    val teamId: String,
    val description: String = "",
    val hasNightAction: Boolean = false,
    val isFiller: Boolean = false,
    val defaultCount: Int = 1
)

@Serializable
data class RolePreset(
    val id: String,
    val name: String,
    val teams: List<TeamDef>,
    val roles: List<RoleDef>
) {
    fun fillerRole(): RoleDef? = roles.find { it.isFiller }
    fun specialRoles(): List<RoleDef> = roles.filterNot { it.isFiller }
    fun teamName(teamId: String): String = teams.find { it.id == teamId }?.name ?: "بدون تیم"
}

class Player(
    val id: Int,
    val name: String
) {
    var role: RoleDef? by mutableStateOf(null)
    var isAlive: Boolean by mutableStateOf(true)
    var votes: Int by mutableStateOf(0)
}

enum class GamePhase { DAY, NIGHT }

/** یک بازیکن ثابت که به لیست دائمی («بازیکنان») اضافه شده. */
@Serializable
data class SavedPlayer(
    val id: Int,
    val name: String
)

@Serializable
data class AppSettings(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val defaultDayTimerSeconds: Int = 120,
    val defaultNightTimerSeconds: Int = 60
)

@Serializable
data class PlayerResult(
    val name: String,
    val roleName: String,
    val teamName: String,
    val won: Boolean
)

@Serializable
data class GameHistoryEntry(
    val id: Long,
    val date: String,
    val presetName: String,
    val results: List<PlayerResult>,
    val winnerTeamName: String,
    val totalRounds: Int
)
