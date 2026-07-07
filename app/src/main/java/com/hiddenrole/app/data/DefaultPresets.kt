package com.hiddenrole.app.data

import com.hiddenrole.app.model.RoleDef
import com.hiddenrole.app.model.RolePreset
import com.hiddenrole.app.model.TeamDef

fun classicMafiaPreset(): RolePreset {
    val mafiaTeam = TeamDef(id = "team_mafia", name = "مافیا")
    val citizenTeam = TeamDef(id = "team_citizen", name = "شهروندان")
    return RolePreset(
        id = "preset_classic_mafia",
        name = "مافیای کلاسیک",
        teams = listOf(mafiaTeam, citizenTeam),
        roles = listOf(
            RoleDef(
                id = "role_mafia", name = "مافیا", teamId = mafiaTeam.id,
                description = "شب‌ها با هم‌تیمی‌هات یک نفر رو حذف می‌کنی.",
                hasNightAction = true, defaultCount = 2
            ),
            RoleDef(
                id = "role_doctor", name = "دکتر", teamId = citizenTeam.id,
                description = "هر شب می‌تونی یک نفر رو نجات بدی.",
                hasNightAction = true, defaultCount = 1
            ),
            RoleDef(
                id = "role_detective", name = "کارآگاه", teamId = citizenTeam.id,
                description = "هر شب هویت یک نفر رو استعلام می‌گیری.",
                hasNightAction = true, defaultCount = 1
            ),
            RoleDef(
                id = "role_citizen", name = "شهروند", teamId = citizenTeam.id,
                description = "نقش خاصی نداری؛ با رأی روز به مشکوک‌ترین نفر رأی بده.",
                hasNightAction = false, isFiller = true, defaultCount = 0
            )
        )
    )
}
