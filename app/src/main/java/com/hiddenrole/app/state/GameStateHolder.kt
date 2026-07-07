package com.hiddenrole.app.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hiddenrole.app.data.HistoryStorage
import com.hiddenrole.app.data.PresetStorage
import com.hiddenrole.app.data.RosterStorage
import com.hiddenrole.app.data.SettingsStorage
import com.hiddenrole.app.data.classicMafiaPreset
import com.hiddenrole.app.model.AppSettings
import com.hiddenrole.app.model.GameHistoryEntry
import com.hiddenrole.app.model.GamePhase
import com.hiddenrole.app.model.Player
import com.hiddenrole.app.model.PlayerResult
import com.hiddenrole.app.model.RoleDef
import com.hiddenrole.app.model.RolePreset
import com.hiddenrole.app.model.SavedPlayer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class GameStateHolder(
    private val presetStorage: PresetStorage,
    private val historyStorage: HistoryStorage,
    private val rosterStorage: RosterStorage,
    private val settingsStorage: SettingsStorage
) {
    // --- قالب‌های نقش (سناریوها) ---
    val presets = mutableStateListOf<RolePreset>()
    var selectedPreset by mutableStateOf<RolePreset?>(null)

    // --- لیست دائمی بازیکن‌ها ---
    val roster = mutableStateListOf<SavedPlayer>()

    // --- تنظیمات ---
    var settings by mutableStateOf(AppSettings())

    // --- بازیکن‌های همین بازی و تنظیم نقش ---
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
        roster.addAll(rosterStorage.load())
        settings = settingsStorage.load()
    }

    // ---------- مدیریت سناریوها ----------
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

    // ---------- لیست دائمی بازیکن‌ها ----------
    fun addRosterPlayer(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || roster.any { it.name == trimmed }) return
        val newId = (roster.maxOfOrNull { it.id } ?: 0) + 1
        roster.add(SavedPlayer(id = newId, name = trimmed))
        rosterStorage.save(roster.toList())
    }

    fun removeRosterPlayer(id: Int) {
        roster.removeAll { it.id == id }
        rosterStorage.save(roster.toList())
    }

    // ---------- تنظیمات ----------
    fun updateSettings(newSettings: AppSettings) {
        settings = newSettings
        settingsStorage.save(newSettings)
    }

    // ---------- بازیکن‌های این بازی ----------
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
        timerDurationDay = settings.defaultDayTimerSeconds
        timerDurationNight = settings.defaultNightTimerSeconds
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
    fun finishGame(winnerTeamId: String) {
        val preset = selectedPreset ?: return
        val winnerTeamName = preset.teamName(winnerTeamId)
        val results = players.map { p ->
            val teamId = p.role?.teamId ?: ""
            PlayerResult(
                name = p.name,
                roleName = p.role?.name ?: "-",
                teamName = preset.teamName(teamId),
                won = teamId == winnerTeamId
            )
        }
        val entry = GameHistoryEntry(
            id = System.currentTimeMillis(),
            date = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date()),
            presetName = preset.name,
            results = results,
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

    // ---------- بازنشانی کامل ----------
    fun resetAllData() {
        presets.clear()
        presets.add(classicMafiaPreset())
        presetStorage.save(presets.toList())

        roster.clear()
        rosterStorage.save(roster.toList())

        history.clear()
        historyStorage.save(history.toList())

        settings = AppSettings()
        settingsStorage.save(settings)

        resetGame()
    }

    companion object {
        fun newId(): String = UUID.randomUUID().toString()
    }
}
