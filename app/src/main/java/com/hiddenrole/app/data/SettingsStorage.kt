package com.hiddenrole.app.data

import android.content.Context
import com.hiddenrole.app.model.AppSettings
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class SettingsStorage(private val context: Context) {
    private val file: File get() = File(context.filesDir, "settings.json")
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun load(): AppSettings {
        return try {
            if (!file.exists()) return AppSettings()
            val text = file.readText()
            if (text.isBlank()) AppSettings() else json.decodeFromString<AppSettings>(text)
        } catch (e: Exception) {
            AppSettings()
        }
    }

    fun save(settings: AppSettings) {
        try {
            file.writeText(json.encodeToString(settings))
        } catch (e: Exception) {
            // نادیده گرفتن خطای ذخیره‌سازی
        }
    }
}
