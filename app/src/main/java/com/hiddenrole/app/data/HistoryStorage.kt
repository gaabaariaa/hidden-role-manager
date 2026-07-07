package com.hiddenrole.app.data

import android.content.Context
import com.hiddenrole.app.model.GameHistoryEntry
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class HistoryStorage(private val context: Context) {
    private val file: File get() = File(context.filesDir, "game_history.json")
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun load(): List<GameHistoryEntry> {
        return try {
            if (!file.exists()) return emptyList()
            val text = file.readText()
            if (text.isBlank()) emptyList() else json.decodeFromString<List<GameHistoryEntry>>(text)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(entries: List<GameHistoryEntry>) {
        try {
            file.writeText(json.encodeToString(entries))
        } catch (e: Exception) {
            // نادیده گرفتن خطای ذخیره‌سازی
        }
    }

    fun addEntry(entry: GameHistoryEntry) {
        val current = load().toMutableList()
        current.add(0, entry)
        save(current)
    }
}
