package com.hiddenrole.app.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hiddenrole.app.data.AbilityStorage
import com.hiddenrole.app.data.HistoryStorage
import com.hiddenrole.app.data.PresetStorage
import com.hiddenrole.app.data.RoleTemplateStorage
import com.hiddenrole.app.data.RosterStorage
import com.hiddenrole.app.data.SettingsStorage
import com.hiddenrole.app.data.classicMafiaPreset
import com.hiddenrole.app.model.Ability
import com.hiddenrole.app.model.AppSettings
import com.hiddenrole.app.model.AssignedRole
import com.hiddenrole.app.model.GameHistoryEntry
import com.hiddenrole.app.model.GamePhase
import com.hiddenrole.app.model.Player
import com.hiddenrole.app.model.PlayerResult
import com.hiddenrole.app.model.RolePreset
import com.hiddenrole.app.model.RoleTemplate
import com.hiddenrole.app.model.SavedPlayer
import com.hiddenrole.app.model.ScenarioRole
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class GameStateHolder(
    private val presetStorage: PresetStorage,
    private val historyStorage: HistoryStorage,
    private val rosterStorage: RosterStorage,
    private val settingsStorage: SettingsStorage,
    private val abilityStorage: AbilityStorage,
    private val roleTemplateStorage: RoleTemplateStorage
) {
    // --- کتابخونه‌ی قابلیت‌ها و نقش‌ها (قوانین کلی) ---
    val abilities = mutableStateListOf<Ability>()
    val roleTemplates = mutableStateListOf<RoleTemplate>()

    // --- سناریوها ---
    val presets = mutableStateListOf<RolePreset>()
    var selectedPreset by mutableStateOf<RolePreset?>(null)

    // --- لیست دائمی بازیکن‌ها ---
    val roster = mutableStateListOf<SavedPlayer>()

    // --- تنظیمات ---
    var settings by mutableStateOf(AppSettings())

    // --- بازیکن‌های همین بازی و تنظیم نقش ---
    val players = mutableStateListOf<Player>()
    val roleCounts = mutableStateMapOf<String, Int>() // scenarioRoleId -> count

    var revealIndex by mutableStateOf(0)
    var phase by mutableStateOf(GamePhase.NIGHT)
    var roundNumber by mutableStateOf(1)

    var timerSeconds by mutableStateOf(0)
    var timerRunning by mutableStateOf(false)
    var timerDurationDay by mutableStateOf(120)

    var votingActive by mutableStateOf(false)

    val challengeQueue = mutableStateListOf<Int>()

    val history = mutableStateListOf<GameHistoryEntry>()

    init {
        abilities.addAll(abilityStorage.load())
        roleTemplates.addAll(roleTemplateStorage.load())
        presets.addAll(presetStorage.load())
        history.addAll(historyStorage.load())
        roster.addAll(rosterStorage.load())
        settings = settingsStorage.load()
    }

    // ---------- کتابخونه‌ی قابلیت‌ها ----------
    fun abilityFor(id: String): Ability? = abilities.find { it.id == id }

    fun saveAbility(ability: Ability) {
        val index = abilities.indexOfFirst { it.id == ability.id }
        if (index >= 0) abilities[index] = ability else abilities.add(ability)
        abilityStorage.save(abilities.toList())
    }

    fun isAbilityInUse(id: String): Boolean = roleTemplates.any { it.abilityIds.contains(id) }

    fun deleteAbility(id: String) {
        if (isAbilityInUse(id)) return
        abilities.removeAll { it.id == id }
        abilityStorage.save(abilities.toList())
    }

    // ---------- کتابخونه‌ی نقش‌ها ----------
    fun roleTemplateFor(id: String): RoleTemplate? = roleTemplates.find { it.id == id }

    fun saveRoleTemplate(template: RoleTemplate) {
        val index = roleTemplates.indexOfFirst { it.id == template.id }
        if (index >= 0) roleTemplates[index] = template else roleTemplates.add(template)
        roleTemplateStorage.save(roleTemplates.toList())
    }

    fun isRoleTemplateInUse(id: String): Boolean = presets.any { preset -> preset.roleSlots.any { it.roleTemplateId == id } }

    fun deleteRoleTemplate(id: String) {
        if (isRoleTemplateInUse(id)) return
        roleTemplates.removeAll { it.id == id }
        roleTemplateStorage.save(roleTemplates.toList())
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
        preset.specialSlots().forEach { slot -> roleCounts[slot.id] = slot.defaultCount }
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
        challengeQueue.remove(id)
    }

    // ---------- ترتیب بازیکن‌ها ----------
    fun movePlayerUp(id: Int) {
        val index = players.indexOfFirst { it.id == id }
        if (index > 0) {
            val temp = players[index - 1]
            players[index - 1] = players[index]
            players[index] = temp
        }
    }

    fun movePlayerDown(id: Int) {
        val index = players.indexOfFirst { it.id == id }
        if (index in 0 until players.size - 1) {
            val temp = players[index + 1]
            players[index + 1] = players[index]
            players[index] = temp
        }
    }

    // ---------- تقسیم نقش ----------
    fun totalConfiguredRoles(): Int = roleCounts.values.sum()

    fun canAssignRoles(): Boolean {
        val preset = selectedPreset ?: return false
        return players.size >= 3 &&
            preset.fillerSlot() != null &&
            totalConfiguredRoles() <= players.size
    }

    private fun resolveAssignedRole(slot: ScenarioRole): AssignedRole {
        val template = roleTemplateFor(slot.roleTemplateId)
        val abilityNames = template?.abilityIds?.mapNotNull { abilityFor(it)?.name } ?: emptyList()
        return AssignedRole(
            roleTemplateId = slot.roleTemplateId,
            teamId = slot.teamId,
            name = template?.name ?: "-",
            description = template?.description ?: "",
            abilityNames = abilityNames
        )
    }

    fun assignRoles() {
        val preset = selectedPreset ?: return
        val fillerSlot = preset.fillerSlot() ?: return
        val pool = mutableListOf<ScenarioRole>()
        preset.specialSlots().forEach { slot ->
            val count = roleCounts[slot.id] ?: 0
            repeat(count) { pool.add(slot) }
        }
        while (pool.size < players.size) pool.add(fillerSlot)
        pool.shuffle()

        players.forEachIndexed { index, player ->
            player.role = resolveAssignedRole(pool[index])
            player.isAlive = true
            player.votes = 0
        }
        revealIndex = 0
        roundNumber = 1
        phase = GamePhase.NIGHT
        timerDurationDay = settings.defaultDayTimerSeconds
        timerSeconds = 0
        timerRunning = false
        votingActive = false
        challengeQueue.clear()
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
        timerSeconds = if (phase == GamePhase.DAY) timerDurationDay else 0
        timerRunning = false
        votingActive = false
        challengeQueue.clear()
    }

    fun resetTimer() {
        timerSeconds = timerDurationDay
        timerRunning = false
    }

    // ---------- صف چالش ----------
    fun addToChallenge(id: Int) {
        if (!challengeQueue.contains(id)) challengeQueue.add(id)
    }

    fun removeFromChallenge(id: Int) {
        challengeQueue.remove(id)
    }

    fun clearChallengeQueue() {
        challengeQueue.clear()
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
        challengeQueue.clear()
    }

    // ---------- بازنشانی کامل ----------
    fun resetAllData() {
        abilities.clear()
        abilities.addAll(com.hiddenrole.app.data.defaultAbilities())
        abilityStorage.save(abilities.toList())

        roleTemplates.clear()
        roleTemplates.addAll(com.hiddenrole.app.data.defaultRoleTemplates())
        roleTemplateStorage.save(roleTemplates.toList())

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
