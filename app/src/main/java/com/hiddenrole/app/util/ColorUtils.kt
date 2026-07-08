package com.hiddenrole.app.util

import androidx.compose.ui.graphics.Color

private val avatarPalette = listOf(
    Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF5C6BC0), Color(0xFF29B6F6),
    Color(0xFF26A69A), Color(0xFF9CCC65), Color(0xFFFFA726), Color(0xFFFF7043),
    Color(0xFF8D6E63), Color(0xFF78909C)
)

/** پالت رنگ‌های قابل انتخاب برای تیم‌ها توی سازنده‌ی سناریو. */
val teamColorPalette = listOf(
    "#EF5350", "#EC407A", "#AB47BC", "#5C6BC0",
    "#29B6F6", "#26A69A", "#9CCC65", "#FFA726",
    "#8D6E63", "#78909C"
)

/**
 * از روی یه رشته (مثلاً اسم بازیکن) یه رنگ ثابت و همیشه یکسان تولید می‌کنه؛
 * برای وقتی که رنگ مشخصی (مثل رنگ تیم) در دسترس نیست.
 */
fun colorForId(id: String): Color {
    val hash = id.fold(0) { acc, c -> acc * 31 + c.code }
    val index = ((hash % avatarPalette.size) + avatarPalette.size) % avatarPalette.size
    return avatarPalette[index]
}

/** تبدیل کد رنگ هگز (مثل "#EF5350") به Color؛ اگه نامعتبر بود یه رنگ ثابت جایگزین می‌ده. */
fun parseHexColor(hex: String): Color {
    return try {
        val cleaned = hex.trim().removePrefix("#")
        val argbInt = if (cleaned.length == 6) {
            val rgb = cleaned.toLong(16).toInt()
            rgb or (0xFF shl 24)
        } else {
            cleaned.toLong(16).toInt()
        }
        Color(argbInt)
    } catch (e: Exception) {
        colorForId(hex)
    }
}
