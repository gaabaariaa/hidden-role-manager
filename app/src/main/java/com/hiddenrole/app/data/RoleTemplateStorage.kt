package com.hiddenrole.app.data

import android.content.Context
import com.hiddenrole.app.model.RoleTemplate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class RoleTemplateStorage(private val context: Context) {
    private val file: File get() = File(context.filesDir, "role_templates.json")
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun load(): List<RoleTemplate> {
        return try {
            if (!file.exists()) {
                val seed = defaultRoleTemplates()
                save(seed)
                return seed
            }
            val text = file.readText()
            if (text.isBlank()) emptyList() else json.decodeFromString<List<RoleTemplate>>(text)
        } catch (e: Exception) {
            defaultRoleTemplates()
        }
    }

    fun save(templates: List<RoleTemplate>) {
        try {
            file.writeText(json.encodeToString(templates))
        } catch (e: Exception) {
            // نادیده گرفتن خطای ذخیره‌سازی
        }
    }
}
