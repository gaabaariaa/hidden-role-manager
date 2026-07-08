package com.hiddenrole.app.data

import com.hiddenrole.app.model.Ability
import com.hiddenrole.app.model.RolePreset
import com.hiddenrole.app.model.RoleTemplate
import com.hiddenrole.app.model.ScenarioRole
import com.hiddenrole.app.model.TeamDef

fun defaultAbilities(): List<Ability> = listOf(
    Ability(
        id = "ability_night_kill",
        name = "کشتن شبانه",
        description = "این نقش هر شب می‌تونه یک نفر رو حذف کنه.",
        wakesAtNight = true
    ),
    Ability(
        id = "ability_save",
        name = "نجات دادن",
        description = "این نقش هر شب می‌تونه یک نفر رو از حذف نجات بده.",
        wakesAtNight = true
    ),
    Ability(
        id = "ability_inquiry",
        name = "استعلام هویت",
        description = "این نقش هر شب می‌تونه هویت یک نفر رو بفهمه.",
        wakesAtNight = true
    )
)

fun defaultRoleTemplates(): List<RoleTemplate> = listOf(
    RoleTemplate(
        id = "role_tpl_mafia",
        name = "مافیا",
        description = "عضو تیم مافیا؛ شب‌ها با هم‌تیمی‌هاش یک نفر رو حذف می‌کنه.",
        abilityIds = listOf("ability_night_kill")
    ),
    RoleTemplate(
        id = "role_tpl_doctor",
        name = "دکتر",
        description = "هر شب می‌تونه یک نفر رو نجات بده.",
        abilityIds = listOf("ability_save")
    ),
    RoleTemplate(
        id = "role_tpl_detective",
        name = "کارآگاه",
        description = "هر شب هویت یک نفر رو استعلام می‌گیره.",
        abilityIds = listOf("ability_inquiry")
    ),
    RoleTemplate(
        id = "role_tpl_citizen",
        name = "شهروند",
        description = "نقش خاصی نداره؛ با رأی روز به مشکوک‌ترین نفر رأی بده.",
        abilityIds = emptyList()
    )
)

fun classicMafiaPreset(): RolePreset {
    val mafiaTeam = TeamDef(id = "team_mafia", name = "مافیا", colorHex = "#EF5350")
    val citizenTeam = TeamDef(id = "team_citizen", name = "شهروندان", colorHex = "#29B6F6")

    val mafiaSlot = ScenarioRole(
        id = "slot_mafia", roleTemplateId = "role_tpl_mafia", teamId = mafiaTeam.id, defaultCount = 2
    )
    val doctorSlot = ScenarioRole(
        id = "slot_doctor", roleTemplateId = "role_tpl_doctor", teamId = citizenTeam.id, defaultCount = 1
    )
    val detectiveSlot = ScenarioRole(
        id = "slot_detective", roleTemplateId = "role_tpl_detective", teamId = citizenTeam.id, defaultCount = 1
    )
    val citizenSlot = ScenarioRole(
        id = "slot_citizen", roleTemplateId = "role_tpl_citizen", teamId = citizenTeam.id,
        isFiller = true, defaultCount = 0
    )

    return RolePreset(
        id = "preset_classic_mafia",
        name = "مافیای کلاسیک",
        teams = listOf(mafiaTeam, citizenTeam),
        roleSlots = listOf(mafiaSlot, doctorSlot, detectiveSlot, citizenSlot),
        nightOrder = listOf(mafiaSlot.id, doctorSlot.id, detectiveSlot.id)
    )
}
