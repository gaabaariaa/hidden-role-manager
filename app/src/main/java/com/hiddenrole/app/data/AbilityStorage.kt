package com.hiddenrole.app.data

import android.content.Context
import com.hiddenrole.app.model.Ability
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class AbilityStorage(private val context: Context) {
    private val file: File get() = File(context.filesDir, "abilities.json")
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun load(): List<Ability> {
        return try {
            if (!file.exists()) {
                val seed = defaultAbilities() + sarkoobAbilities()
                save(seed)
                return seed
            }
            val text = file.readText()
            if (text.isBlank()) emptyList() else json.decodeFromString<List<Ability>>(text)
        } catch (e: Exception) {
            defaultAbilities() + sarkoobAbilities()
        }
    }

    fun save(abilities: List<Ability>) {
        try {
            file.writeText(json.encodeToString(abilities))
        } catch (e: Exception) {
            // نادیده گرفتن خطای ذخیره‌سازی
        }
    }
}
