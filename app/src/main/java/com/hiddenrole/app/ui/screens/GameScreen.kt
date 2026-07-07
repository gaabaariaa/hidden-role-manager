package com.hiddenrole.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hiddenrole.app.model.GamePhase
import com.hiddenrole.app.state.GameStateHolder
import com.hiddenrole.app.util.SoundVibrationHelper
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    state: GameStateHolder,
    onGameFinished: () -> Unit
) {
    val context = LocalContext.current
    val soundHelper = remember { SoundVibrationHelper(context) }
    var showFinishDialog by remember { mutableStateOf(false) }
    val preset = state.selectedPreset

    LaunchedEffect(state.timerRunning, state.phase) {
        while (state.timerRunning && state.timerSeconds > 0) {
            delay(1000)
            state.timerSeconds -= 1
            if (state.timerSeconds <= 0) {
                state.timerRunning = false
                soundHelper.playPhaseEndAlert(
                    soundEnabled = state.settings.soundEnabled,
                    vibrationEnabled = state.settings.vibrationEnabled
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.phase == GamePhase.DAY) {
                            "روز ${state.roundNumber} ☀️"
                        } else {
                            "شب ${state.roundNumber} 🌙"
                        }
                    )
                }
            )
        },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                OutlinedButton(onClick = { showFinishDialog = true }, modifier = Modifier.weight(1f)) {
                    Text("پایان بازی")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { state.switchPhase() }, modifier = Modifier.weight(1f)) {
                    Text(if (state.phase == GamePhase.DAY) "رفتن به شب" else "رفتن به روز")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val minutes = state.timerSeconds / 60
                    val seconds = state.timerSeconds % 60
                    Text(
                        "%d:%02d".format(minutes, seconds),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Row {
                        Button(onClick = { state.timerRunning = !state.timerRunning }) {
                            Text(if (state.timerRunning) "توقف" else "شروع")
                        }
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(onClick = { state.resetTimer() }) { Text("ریست") }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (preset != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    preset.teams.forEach { team ->
                        Text("${team.name}: ${state.aliveCountByTeam(team.id)}")
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            if (state.phase == GamePhase.DAY) {
                if (!state.votingActive) {
                    Button(onClick = { state.startVoting() }, modifier = Modifier.fillMaxWidth()) {
                        Text("شروع رأی‌گیری")
                    }
                } else {
                    OutlinedButton(onClick = { state.endVoting() }, modifier = Modifier.fillMaxWidth()) {
                        Text("پایان رأی‌گیری")
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Text("بازیکن‌ها", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.players, key = { it.id }) { player ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (player.isAlive) {
                                MaterialTheme.colorScheme.surfaceVariant
                            } else {
                                MaterialTheme.colorScheme.errorContainer
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(player.name, fontWeight = FontWeight.Bold)
                                Text(
                                    if (player.isAlive) "زنده" else "حذف شده",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (player.isAlive) Color.Unspecified else MaterialTheme.colorScheme.error
                                )
                            }

                            if (state.votingActive && player.isAlive) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { state.clearVote(player.id) }) { Text("−") }
                                    Text(player.votes.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                                    IconButton(onClick = { state.addVote(player.id) }) { Text("+") }
                                }
                            } else if (!state.votingActive && state.phase == GamePhase.DAY && player.isAlive && player.votes > 0) {
                                AssistChip(
                                    onClick = { state.eliminate(player.id) },
                                    label = { Text("${player.votes} رأی — حذف") }
                                )
                            } else {
                                Switch(
                                    checked = player.isAlive,
                                    onCheckedChange = { state.toggleAlive(player.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFinishDialog && preset != null) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("بازی چطور تموم شد؟") },
            text = { Text("تیم برنده رو انتخاب کن") },
            confirmButton = {
                Column {
                    preset.teams.forEach { team ->
                        Button(
                            onClick = {
                                state.finishGame(team.id)
                                showFinishDialog = false
                                onGameFinished()
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) { Text("برد ${team.name}") }
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showFinishDialog = false }) { Text("انصراف") }
            }
        )
    }
}
