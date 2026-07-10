package com.hiddenrole.app.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.Serializable

/** نوع اثر یه قابلیت روی بازی؛ برای هدایت گرداننده هنگام اجرای شب استفاده می‌شه. */
@Serializable
enum class NightActionType {
    NONE,        // فقط بیدار می‌شه، بدون انتخاب هدف خاص (مثلاً هماهنگی تیمی بدون اثر خودکار)
    KILL,        // یک نفر رو انتخاب و حذف می‌کنه
    SAVE,        // یک نفر رو از حذف نجات می‌ده (جلوی KILL همون شب رو می‌گیره)
    INVESTIGATE, // یک نفر رو انتخاب می‌کنه و گرداننده تیمش رو به‌عنوان نتیجه می‌بینه
    CUSTOM       // فقط یک نفر انتخاب می‌شه؛ اثرش دست خود گرداننده‌ست
}

/** یه قابلیت/توانایی که می‌تونه به یک یا چند نقش اضافه بشه (مثل «کشتن شبانه»، «نجات دادن»). */
@Serializable
data class Ability(
    val id: String,
    val name: String,
    val description: String = "",
    val wakesAtNight: Boolean = false,
    val actionType: NightActionType = NightActionType.NONE
)

/** یه نقش قابل استفاده‌ی مجدد، شامل چند قابلیت انتخابی؛ مستقل از هر سناریوی خاص. */
@Serializable
data class RoleTemplate(
    val id: String,
    val name: String,
    val description: String = "",
    val abilityIds: List<String> = emptyList()
)

@Serializable
data class TeamDef(
    val id: String,
    val name: String,
    val colorHex: String = "#7C4DFF"
)

/** استفاده از یه نقش (از کتابخونه‌ی نقش‌ها) در یه سناریوی خاص، وصل‌شده به یه تیم. */
@Serializable
data class ScenarioRole(
    val id: String,
    val roleTemplateId: String,
    val teamId: String,
    val isFiller: Boolean = false,
    val defaultCount: Int = 1
)

@Serializable
data class RolePreset(
    val id: String,
    val name: String,
    val teams: List<TeamDef>,
    val roleSlots: List<ScenarioRole>,
    val nightOrder: List<String> = emptyList()
) {
    fun fillerSlot(): ScenarioRole? = roleSlots.find { it.isFiller }
    fun specialSlots(): List<ScenarioRole> = roleSlots.filterNot { it.isFiller }
    fun teamName(teamId: String): String = teams.find { it.id == teamId }?.name ?: "بدون تیم"
    fun team(teamId: String): TeamDef? = teams.find { it.id == teamId }
}

/** نقش نهایی که موقع تقسیم، به یه بازیکن اختصاص داده شده (اطلاعات نقش در همون لحظه ذخیره می‌شه). */
data class AssignedRole(
    val roleTemplateId: String,
    val teamId: String,
    val name: String,
    val description: String,
    val abilityNames: List<String>
)

class Player(
    val id: Int,
    val name: String
) {
    var role: AssignedRole? by mutableStateOf(null)
    var isAlive: Boolean by mutableStateOf(true)
    var votes: Int by mutableStateOf(0)
}

enum class GamePhase { DAY, NIGHT }

/** یک مرحله از اجرای شب: کی بیدار شه، چه اقدامی انجام بده. فقط در حافظه‌ست، ذخیره نمی‌شه. */
data class NightStep(
    val teamId: String,
    val abilityId: String,
    val actionType: NightActionType,
    val label: String,
    val actingPlayerIds: List<Int>
)

@Serializable
data class SavedPlayer(
    val id: Int,
    val name: String
)

@Serializable
data class AppSettings(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val defaultDayTimerSeconds: Int = 120
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
