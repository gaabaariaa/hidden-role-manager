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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hiddenrole.app.state.GameStateHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameConfigScreen(
    state: GameStateHolder,
    onBack: () -> Unit,
    onAssign: () -> Unit
) {
    val preset = state.selectedPreset

    Scaffold(
        topBar = { TopAppBar(title = { Text("تنظیم نقش‌ها") }) },
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                if (!state.canAssignRoles()) {
                    Text(
                        "جمع تعداد نقش‌ها نباید از تعداد بازیکن‌ها بیشتر بشه",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Row {
                    OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("بازگشت") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onAssign, enabled = state.canAssignRoles(), modifier = Modifier.weight(1f)) {
                        Text("تقسیم نقش‌ها")
                    }
                }
            }
        }
    ) { padding ->
        if (preset == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("قالبی انتخاب نشده")
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text(
                    "تعداد بازیکن‌ها: ${state.players.size}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))

                preset.specialRoles().forEach { role ->
                    val count = state.roleCounts[role.id] ?: 0
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(role.name, style = MaterialTheme.typography.bodyLarge)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                if (count > 0) state.roleCounts[role.id] = count - 1
                            }) { Text("−", style = MaterialTheme.typography.titleLarge) }
                            Text(count.toString(), modifier = Modifier.padding(horizontal = 12.dp))
                            IconButton(onClick = {
                                state.roleCounts[role.id] = count + 1
                            }) { Text("+", style = MaterialTheme.typography.titleLarge) }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                val fillerName = preset.fillerRole()?.name ?: "-"
                val remaining = (state.players.size - state.totalConfiguredRoles()).coerceAtLeast(0)
                Text("بقیه‌ی بازیکن‌ها ($remaining نفر) نقش «$fillerName» می‌گیرن.")
            }
        }
    }
}
