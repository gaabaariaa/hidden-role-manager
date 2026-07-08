package com.hiddenrole.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.hiddenrole.app.model.RolePreset
import com.hiddenrole.app.state.GameStateHolder
import com.hiddenrole.app.util.parseHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenariosScreen(
    state: GameStateHolder,
    onBack: () -> Unit,
    onCreatePreset: () -> Unit,
    onEditPreset: (RolePreset) -> Unit,
    onPlayPreset: (RolePreset) -> Unit,
    onOpenRoleTemplates: () -> Unit,
    onOpenAbilities: () -> Unit
) {
    var presetPendingDelete by remember { mutableStateOf<RolePreset?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سناریوها") },
                navigationIcon = { TextButton(onClick = onBack) { Text("بازگشت") } },
                actions = {
                    IconButton(onClick = onOpenAbilities) {
                        Icon(Icons.Default.Bolt, contentDescription = "قابلیت‌ها")
                    }
                    IconButton(onClick = onOpenRoleTemplates) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "نقش‌های کتابخونه")
                    }
                    IconButton(onClick = onCreatePreset) {
                        Icon(Icons.Default.Add, contentDescription = "سناریوی جدید")
                    }
                }
            )
        }
    ) { padding ->
        if (state.presets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("هنوز سناریویی نساختی. با دکمه‌ی + یکی بساز.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
            ) {
                items(state.presets, key = { it.id }) { preset ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                preset.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                preset.teams.forEach { team ->
                                    Box(
                                        modifier = Modifier
                                            .background(parseHexColor(team.colorHex).copy(alpha = 0.2f), CircleShape)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(team.name, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${preset.roleSlots.size} نقش",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(12.dp))
                            Row {
                                Button(onClick = { onPlayPreset(preset) }, modifier = Modifier.weight(1f)) {
                                    Text("شروع بازی")
                                }
                                Spacer(Modifier.width(8.dp))
                                OutlinedButton(onClick = { onEditPreset(preset) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "ویرایش")
                                }
                                Spacer(Modifier.width(8.dp))
                                OutlinedButton(onClick = { presetPendingDelete = preset }) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    presetPendingDelete?.let { preset ->
        AlertDialog(
            onDismissRequest = { presetPendingDelete = null },
            title = { Text("حذف سناریو") },
            text = { Text("سناریوی «${preset.name}» حذف بشه؟") },
            confirmButton = {
                Button(onClick = {
                    state.deletePreset(preset.id)
                    presetPendingDelete = null
                }) { Text("حذف") }
            },
            dismissButton = {
                OutlinedButton(onClick = { presetPendingDelete = null }) { Text("انصراف") }
            }
        )
    }
}
