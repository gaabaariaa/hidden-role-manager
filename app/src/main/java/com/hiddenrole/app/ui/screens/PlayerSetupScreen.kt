package com.hiddenrole.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hiddenrole.app.state.GameStateHolder
import com.hiddenrole.app.ui.components.PlayerAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSetupScreen(
    state: GameStateHolder,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var alsoSaveToRoster by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("بازیکن‌های این بازی") },
                navigationIcon = { TextButton(onClick = onBack) { Text("بازگشت") } }
            )
        },
        bottomBar = {
            Button(
                onClick = onNext,
                enabled = state.players.size >= 3,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(if (state.players.size < 3) "حداقل ۳ بازیکن لازمه" else "ادامه به تنظیم نقش‌ها")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(
                "بازیکن‌ها (${state.players.size} نفر) — سناریو: ${state.selectedPreset?.name ?: "-"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            if (state.roster.isNotEmpty()) {
                Text("انتخاب سریع از لیست همیشگی", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.roster.forEach { saved ->
                        val existing = state.players.find { it.name == saved.name }
                        FilterChip(
                            selected = existing != null,
                            onClick = {
                                if (existing != null) {
                                    state.removePlayer(existing.id)
                                } else {
                                    state.addPlayer(saved.name)
                                }
                            },
                            label = { Text(saved.name) }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("اسم بازیکن جدید") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val trimmed = nameInput.trim()
                    state.addPlayer(trimmed)
                    if (alsoSaveToRoster && trimmed.isNotEmpty()) {
                        state.addRosterPlayer(trimmed)
                    }
                    nameInput = ""
                }) {
                    Text("افزودن")
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = alsoSaveToRoster, onCheckedChange = { alsoSaveToRoster = it })
                Text("به لیست همیشگی هم اضافه شه", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(12.dp))
            Text("بازیکن‌های این بازی", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.players, key = { it.id }) { player ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PlayerAvatar(name = player.name)
                                Spacer(Modifier.width(12.dp))
                                Text(player.name, style = MaterialTheme.typography.bodyLarge)
                            }
                            IconButton(onClick = { state.removePlayer(player.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف")
                            }
                        }
                    }
                }
            }
        }
    }
}
