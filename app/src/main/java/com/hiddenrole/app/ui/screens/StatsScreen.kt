package com.hiddenrole.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hiddenrole.app.state.GameStateHolder
import com.hiddenrole.app.ui.components.PlayerAvatar

private data class PlayerStat(val name: String, val games: Int, val wins: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    state: GameStateHolder,
    onBack: () -> Unit
) {
    val statsList = remember(state.history.size) {
        val map = linkedMapOf<String, PlayerStat>()
        state.history.forEach { entry ->
            entry.results.forEach { result ->
                val current = map[result.name] ?: PlayerStat(result.name, 0, 0)
                map[result.name] = current.copy(
                    games = current.games + 1,
                    wins = current.wins + if (result.won) 1 else 0
                )
            }
        }
        map.values.sortedByDescending { it.wins }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("آمار بازیکنان") },
                navigationIcon = { TextButton(onClick = onBack) { Text("بازگشت") } }
            )
        }
    ) { padding ->
        if (statsList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("هنوز آماری ثبت نشده؛ اول چندتا بازی تموم کن")
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text(
                    "کل بازی‌های ثبت‌شده: ${state.history.size}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                LazyColumn {
                    itemsIndexed(statsList) { index, stat ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "#${index + 1}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    PlayerAvatar(name = stat.name)
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(stat.name, fontWeight = FontWeight.Bold)
                                        Text("${stat.games} بازی", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${stat.wins} برد", fontWeight = FontWeight.Bold)
                                    val rate = if (stat.games > 0) (stat.wins * 100 / stat.games) else 0
                                    Text("$rate% نرخ برد", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
