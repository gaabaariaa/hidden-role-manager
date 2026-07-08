package com.hiddenrole.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val AccentPurple = Color(0xFF7C4DFF)
val AccentDeep = Color(0xFF5E35B1)
val AccentTeal = Color(0xFF26A69A)
val AccentAmber = Color(0xFFFFA726)

private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF1C1B1F)
private val DarkSurfaceVariant = Color(0xFF2A2A2E)
private val DarkErrorContainer = Color(0xFF5A1B1B)

private val HiddenRoleDarkColors = darkColorScheme(
    primary = AccentPurple,
    secondary = AccentTeal,
    tertiary = AccentAmber,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    errorContainer = DarkErrorContainer,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color.White
)

private val HiddenRoleShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun HiddenRoleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HiddenRoleDarkColors,
        shapes = HiddenRoleShapes,
        content = content
    )
}
