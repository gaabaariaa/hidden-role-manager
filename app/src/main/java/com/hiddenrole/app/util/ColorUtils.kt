package com.hiddenrole.app.util

import androidx.compose.ui.graphics.Color

private val palette = listOf(
    Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF5C6BC0), Color(0xFF29B6F6),
    Color(0xFF26A69A), Color(0xFF9CCC65), Color(0xFFFFA726), Color(0xFFFF7043),
    Color(0xFF8D6E63), Color(0xFF78909C)
)

/**
 * از روی یه رشته (اسم بازیکن یا شناسه‌ی تیم) یه رنگ ثابت و همیشه یکسان تولید می‌کنه
 * تا هویت بصری هر بازیکن/تیم توی کل اپ یکسان بمونه.
 */
fun colorForId(id: String): Color {
    val hash = id.fold(0) { acc, c -> acc * 31 + c.code }
    val index = ((hash % palette.size) + palette.size) % palette.size
    return palette[index]
}
