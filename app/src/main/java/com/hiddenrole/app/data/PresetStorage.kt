package com.hiddenrole.app.data

import android.content.Context
import com.hiddenrole.app.model.RolePreset
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class PresetStorage(private val context: Context) {
    private val file: File get() = File(context.filesDir, "presets.json")
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun load(): List<RolePreset> {
        return try {
            if (!file.exists()) {
                val seed = listOf(classicMafiaPreset())
                save(seed)
                return seed
            }
            val text = file.readText()
            if (text.isBlank()) emptyList() else json.decodeFromString<List<RolePreset>>(text)
        } catch (e: Exception) {
            listOf(classicMafiaPreset())
        }
    }

    fun save(presets: List<RolePreset>) {
        try {
            file.writeText(json.encodeToString(presets))
        } catch (e: Exception) {
            // نادیده گرفتن خطای ذخیره‌سازی
        }
    }
}
