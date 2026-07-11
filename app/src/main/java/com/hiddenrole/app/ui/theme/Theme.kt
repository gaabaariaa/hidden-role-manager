package com.hiddenrole.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val AccentPurple = Color(0xFF7C4DFF)
val AccentDeep = Color(0xFF4A2FB8)
val AccentTeal = Color(0xFF26C6DA)
val AccentAmber = Color(0xFFFFB74D)

private val DarkBackground = Color(0xFF0F0F14)
private val DarkSurface = Color(0xFF1A1A22)
private val DarkSurfaceVariant = Color(0xFF262631)
private val DarkErrorContainer = Color(0xFF5A1B1B)
private val DarkSecondaryContainer = Color(0xFF163B3E)
private val DarkTertiaryContainer = Color(0xFF4A3B12)

private val HiddenRoleDarkColors = darkColorScheme(
    primary = AccentPurple,
    onPrimary = Color.White,
    primaryContainer = AccentDeep,
    onPrimaryContainer = Color.White,
    secondary = AccentTeal,
    onSecondary = Color.Black,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = Color.White,
    tertiary = AccentAmber,
    onTertiary = Color.Black,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = Color.White,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFD8D8E0),
    errorContainer = DarkErrorContainer,
    outline = Color(0xFF48485A)
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
