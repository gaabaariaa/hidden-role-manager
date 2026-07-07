package com.hiddenrole.app.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hiddenrole.app.data.HistoryStorage
import com.hiddenrole.app.data.PresetStorage
import com.hiddenrole.app.model.GameHistoryEntry
import com.hiddenrole.app.model.GamePhase
import com.hiddenrole.app.model.Player
import com.hiddenrole.app.model.RoleDef
import com.hiddenrole.app.model.RolePreset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class GameStateHolder(
    private val presetStorage: PresetStorage,
    private val historyStorage: HistoryStorage
) {
    // --- قالب‌های نقش ---
    val presets = mutableStateListOf<RolePreset>()
    var selectedPreset by mutableStateOf<RolePreset?>(null)

    // --- بازیکن‌ها و تنظیم بازی ---
    val players = mutableStateListOf<Player>()
    val roleCounts = mutableStateMapOf<String, Int>() // roleId -> count

    var revealIndex by mutableStateOf(0)
    var phase by mutableStateOf(GamePhase.NIGHT)
    var roundNumber by mutableStateOf(1)

    var timerSeconds by mutableStateOf(60)
    var timerRunning by mutableStateOf(false)
    var timerDurationDay by mutableStateOf(120)
    var timerDurationNight by mutableStateOf(60)

    var votingActive by mutableStateOf(false)

    val history = mutableStateListOf<GameHistoryEntry>()

    init {
        presets.addAll(presetStorage.load())
        history.addAll(historyStorage.load())
    }

    // ---------- مدیریت قالب‌ها ----------
    fun savePreset(preset: RolePreset) {
        val index = presets.indexOfFirst { it.id == preset.id }
        if (index >= 0) presets[index] = preset else presets.add(preset)
        presetStorage.save(presets.toList())
    }

    fun deletePreset(presetId: String) {
        presets.removeAll { it.id == presetId }
        presetStorage.save(presets.toList())
    }

    fun selectPresetToPlay(preset: RolePreset) {
        selectedPreset = preset
        roleCounts.clear()
        preset.specialRoles().forEach { role -> roleCounts[role.id] = role.defaultCount }
    }

    // ---------- بازیکن‌ها ----------
    fun addPlayer(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val newId = (players.maxOfOrNull { it.id } ?: 0) + 1
        players.add(Player(id = newId, name = trimmed))
    }

    fun removePlayer(id: Int) {
        players.removeAll { it.id == id }
    }

    // ---------- تقسیم نقش ----------
    fun totalConfiguredRoles(): Int = roleCounts.values.sum()

    fun canAssignRoles(): Boolean {
        val preset = selectedPreset ?: return false
        return players.size >= 3 &&
            preset.fillerRole() != null &&
            totalConfiguredRoles() <= players.size
    }

    fun assignRoles() {
        val preset = selectedPreset ?: return
        val filler = preset.fillerRole() ?: return
        val pool = mutableListOf<RoleDef>()
        preset.specialRoles().forEach { role ->
            val count = roleCounts[role.id] ?: 0
            repeat(count) { pool.add(role) }
        }
        while (pool.size < players.size) pool.add(filler)
        pool.shuffle()

        players.forEachIndexed { index, player ->
            player.role = pool[index]
            player.isAlive = true
            player.votes = 0
        }
        revealIndex = 0
        roundNumber = 1
        phase = GamePhase.NIGHT
        timerSeconds = timerDurationNight
        timerRunning = false
        votingActive = false
    }

    fun nextReveal() {
        if (revealIndex < players.size - 1) revealIndex++
    }

    fun isRevealDone(): Boolean = revealIndex >= players.size - 1

    // ---------- فاز و تایمر ----------
    fun switchPhase() {
        phase = if (phase == GamePhase.NIGHT) {
            GamePhase.DAY
        } else {
            roundNumber++
            GamePhase.NIGHT
        }
        timerSeconds = if (phase == GamePhase.DAY) timerDurationDay else timerDurationNight
        timerRunning = false
        votingActive = false
    }

    fun resetTimer() {
        timerSeconds = if (phase == GamePhase.DAY) timerDurationDay else timerDurationNight
        timerRunning = false
    }

    // ---------- رأی‌گیری ----------
    fun startVoting() {
        players.forEach { it.votes = 0 }
        votingActive = true
    }

    fun addVote(playerId: Int) {
        players.find { it.id == playerId }?.let { it.votes += 1 }
    }

    fun clearVote(playerId: Int) {
        players.find { it.id == playerId }?.let { if (it.votes > 0) it.votes -= 1 }
    }

    fun endVoting() {
        votingActive = false
    }

    fun eliminate(id: Int) {
        players.find { it.id == id }?.isAlive = false
    }

    fun toggleAlive(id: Int) {
        players.find { it.id == id }?.let { it.isAlive = !it.isAlive }
    }

    fun aliveCountByTeam(teamId: String): Int {
        return players.count { it.isAlive && it.role?.teamId == teamId }
    }

    // ---------- پایان بازی ----------
    fun finishGame(winnerTeamName: String) {
        val preset = selectedPreset
        val entry = GameHistoryEntry(
            id = System.currentTimeMillis(),
            date = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date()),
            presetName = preset?.name ?: "-",
            playerNames = players.map { it.name },
            winnerTeamName = winnerTeamName,
            totalRounds = roundNumber
        )
        history.add(0, entry)
        historyStorage.addEntry(entry)
    }

    fun resetGame() {
        players.clear()
        roleCounts.clear()
        selectedPreset = null
        revealIndex = 0
        roundNumber = 1
        phase = GamePhase.NIGHT
        timerRunning = false
        votingActive = false
    }

    companion object {
        fun newId(): String = UUID.randomUUID().toString()
    }
}
