package com.hiddenrole.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hiddenrole.app.navigation.Routes
import com.hiddenrole.app.state.GameStateHolder
import com.hiddenrole.app.ui.theme.AccentAmber
import com.hiddenrole.app.ui.theme.AccentDeep
import com.hiddenrole.app.ui.theme.AccentPurple
import com.hiddenrole.app.ui.theme.AccentTeal

private data class HomeMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

@Composable
fun HomeScreen(
    state: GameStateHolder,
    onNavigate: (String) -> Unit
) {
    val menuItems = listOf(
        HomeMenuItem("شروع بازی", "یه سناریو انتخاب کن و شروع کن", Icons.Filled.PlayArrow, AccentPurple, Routes.START_GAME_PICK),
        HomeMenuItem("بازیکنان", "لیست دائمی دوستات", Icons.Filled.Groups, AccentTeal, Routes.ROSTER),
        HomeMenuItem("آمار", "برد و باخت هر بازیکن", Icons.Filled.BarChart, AccentAmber, Routes.STATS),
        HomeMenuItem("قوانین", "سناریو، نقش و قابلیت‌ها", Icons.Filled.Gavel, Color(0xFFEF5350), Routes.RULES_HOME),
        HomeMenuItem("تاریخچه بازی‌ها", "مرور بازی‌های قبلی", Icons.Filled.History, AccentDeep, Routes.HISTORY),
        HomeMenuItem("تنظیمات", "صدا، ویبره و زمان‌بندی", Icons.Filled.Settings, Color(0xFF78909C), Routes.SETTINGS)
    )

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(AccentDeep, MaterialTheme.colorScheme.background))
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        "نقش پنهان 🎭",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "دستیار گرداننده‌ی بازی‌های نقش مخفی",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(menuItems, key = { it.title }) { item ->
                    Card(
                        onClick = { onNavigate(item.route) },
                        modifier = Modifier.aspectRatio(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(item.color.copy(alpha = 0.28f), Color.Transparent)
                                    )
                                )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(item.color, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(item.icon, contentDescription = item.title, tint = Color.White)
                                }
                                Column {
                                    Text(
                                        item.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(item.subtitle, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
