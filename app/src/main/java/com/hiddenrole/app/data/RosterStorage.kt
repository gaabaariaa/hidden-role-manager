package com.hiddenrole.app.data

import android.content.Context
import com.hiddenrole.app.model.SavedPlayer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class RosterStorage(private val context: Context) {
    private val file: File get() = File(context.filesDir, "roster.json")
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun load(): List<SavedPlayer> {
        return try {
            if (!file.exists()) return emptyList()
            val text = file.readText()
            if (text.isBlank()) emptyList() else json.decodeFromString<List<SavedPlayer>>(text)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(players: List<SavedPlayer>) {
        try {
            file.writeText(json.encodeToString(players))
        } catch (e: Exception) {
            // نادیده گرفتن خطای ذخیره‌سازی
        }
    }
}
