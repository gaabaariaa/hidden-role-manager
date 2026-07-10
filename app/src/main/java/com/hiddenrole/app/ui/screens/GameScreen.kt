package com.hiddenrole.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.hiddenrole.app.model.GamePhase
import com.hiddenrole.app.model.NightActionType
import com.hiddenrole.app.model.NightStep
import com.hiddenrole.app.model.Player
import com.hiddenrole.app.model.RolePreset
import com.hiddenrole.app.state.GameStateHolder
import com.hiddenrole.app.ui.components.PlayerAvatar
import com.hiddenrole.app.util.SoundVibrationHelper
import com.hiddenrole.app.util.parseHexColor
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GameScreen(
    state: GameStateHolder,
    onGameFinished: () -> Unit
) {
    val context = LocalContext.current
    val soundHelper = remember { SoundVibrationHelper(context) }
    var showFinishDialog by remember { mutableStateOf(false) }
    var showRolesDialog by remember { mutableStateOf(false) }
    var selectedViewPlayerId by remember { mutableStateOf<Int?>(null) }
    val preset = state.selectedPreset

    LaunchedEffect(state.timerRunning, state.phase) {
        while (state.phase == GamePhase.DAY && state.timerRunning && state.timerSeconds > 0) {
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
        if (preset == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("سناریویی انتخاب نشده")
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

                if (state.phase == GamePhase.NIGHT) {
                    NightActionWizard(state = state)
                    Spacer(Modifier.height(16.dp))
                    Text("نقش‌ها (فقط برای گرداننده)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    RolesOverview(preset = preset, players = state.players)
                    Spacer(Modifier.height(12.dp))
                } else {
                    state.lastNightResult?.let { resultText ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                resultText,
                                modifier = Modifier.padding(12.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                    // تایمر فقط توی فاز روز
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

                    OutlinedButton(
                        onClick = {
                            selectedViewPlayerId = null
                            showRolesDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("دیدن نقش‌ها") }

                    Spacer(Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        preset.teams.forEach { team ->
                            val teamColor = parseHexColor(team.colorHex)
                            Box(
                                modifier = Modifier
                                    .background(teamColor.copy(alpha = 0.18f), MaterialTheme.shapes.small)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("${team.name}: ${state.aliveCountByTeam(team.id)}", color = teamColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    ChallengeSection(state = state)

                    Spacer(Modifier.height(12.dp))
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Icon(
                                            Icons.Filled.KeyboardArrowUp,
                                            contentDescription = "بالاتر",
                                            modifier = Modifier.size(18.dp).clickable { state.movePlayerUp(player.id) }
                                        )
                                        Icon(
                                            Icons.Filled.KeyboardArrowDown,
                                            contentDescription = "پایین‌تر",
                                            modifier = Modifier.size(18.dp).clickable { state.movePlayerDown(player.id) }
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    PlayerAvatar(name = player.name, size = 36.dp)
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(player.name, fontWeight = FontWeight.Bold)
                                        Text(
                                            if (player.isAlive) "زنده" else "حذف شده",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (player.isAlive) Color.Unspecified else MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                if (state.phase == GamePhase.DAY && state.votingActive && player.isAlive) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Filled.KeyboardArrowDown,
                                            contentDescription = "کم کردن رأی",
                                            modifier = Modifier.size(20.dp).clickable { state.clearVote(player.id) }
                                        )
                                        Text(player.votes.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                                        Icon(
                                            Icons.Filled.KeyboardArrowUp,
                                            contentDescription = "افزودن رأی",
                                            modifier = Modifier.size(20.dp).clickable { state.addVote(player.id) }
                                        )
                                    }
                                } else if (state.phase == GamePhase.DAY && !state.votingActive && player.isAlive && player.votes > 0) {
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
    }

    if (showRolesDialog && preset != null) {
        AlertDialog(
            onDismissRequest = { showRolesDialog = false },
            title = { Text("دیدن نقش‌ها") },
            text = {
                Column {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = selectedViewPlayerId == null,
                            onClick = { selectedViewPlayerId = null },
                            label = { Text("همه") }
                        )
                        state.players.forEach { p ->
                            FilterChip(
                                selected = selectedViewPlayerId == p.id,
                                onClick = { selectedViewPlayerId = p.id },
                                label = { Text(p.name) }
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    RolesOverview(preset = preset, players = state.players, filterPlayerId = selectedViewPlayerId)
                }
            },
            confirmButton = {
                Button(onClick = { showRolesDialog = false }) { Text("بستن") }
            }
        )
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NightActionWizard(state: GameStateHolder) {
    if (state.nightSteps.isEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "این سناریو اقدام شبانه‌ی مشخصی نداره.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        return
    }

    if (state.isNightSequenceDone()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("همه‌ی اقدام‌های شب انجام شد ✅", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "برای ادامه، «رفتن به روز» رو از پایین صفحه بزن.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        return
    }

    val step = state.currentNightStep() ?: return
    var selectedTargetId by remember(state.currentNightStepIndex) { mutableStateOf<Int?>(null) }
    val investigationResult = state.nightInvestigationResult

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "مرحله ${state.currentNightStepIndex + 1} از ${state.nightSteps.size}",
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(Modifier.height(4.dp))
            Text(
                nightStepPrompt(step),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            if (step.actionType == NightActionType.INVESTIGATE && investigationResult != null) {
                val targetName = state.players.find { it.id == investigationResult.first }?.name ?: "-"
                Text("نتیجه:", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "«$targetName» عضو تیم «${investigationResult.second}» هست.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Button(onClick = { state.advanceNightStep() }, modifier = Modifier.fillMaxWidth()) {
                    Text("متوجه شدم، بعدی")
                }
            } else if (step.actionType == NightActionType.NONE) {
                Text(
                    "این نقش فقط بیدار می‌شه؛ هدف خاصی لازم نیست.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(12.dp))
                Button(onClick = { state.advanceNightStep() }, modifier = Modifier.fillMaxWidth()) {
                    Text("بعدی")
                }
            } else {
                Text("انتخاب هدف:", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .heightIn(max = 240.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    state.players.filter { it.isAlive }.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTargetId = p.id }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedTargetId == p.id, onClick = { selectedTargetId = p.id })
                            Spacer(Modifier.width(4.dp))
                            PlayerAvatar(name = p.name, size = 28.dp)
                            Spacer(Modifier.width(8.dp))
                            Text(p.name)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row {
                    OutlinedButton(onClick = { state.advanceNightStep() }, modifier = Modifier.weight(1f)) {
                        Text("رد کردن")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            state.submitNightStepTarget(selectedTargetId)
                            if (step.actionType != NightActionType.INVESTIGATE) {
                                state.advanceNightStep()
                            }
                        },
                        enabled = selectedTargetId != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تایید")
                    }
                }
            }
        }
    }
}

private fun nightStepPrompt(step: NightStep): String = when (step.actionType) {
    NightActionType.KILL -> "«${step.label}» بیدار شه و قربانی امشب رو انتخاب کنه"
    NightActionType.SAVE -> "«${step.label}» بیدار شه و نفر مورد نظرش رو نجات بده"
    NightActionType.INVESTIGATE -> "«${step.label}» بیدار شه و هویت یک نفر رو استعلام بگیره"
    NightActionType.CUSTOM -> "«${step.label}» بیدار شه و اقدامش رو انجام بده"
    NightActionType.NONE -> "«${step.label}» بیدار شه"
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChallengeSection(state: GameStateHolder) {
    val queued = state.challengeQueue
    val availablePlayers = state.players.filter { it.isAlive && !queued.contains(it.id) }

    Text("صف چالش (اجازه‌ی صحبت بیشتر)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))

    if (availablePlayers.isNotEmpty()) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            availablePlayers.forEach { p ->
                AssistChip(
                    onClick = { state.addToChallenge(p.id) },
                    label = { Text(p.name) },
                    leadingIcon = {
                        Icon(Icons.Filled.PanTool, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }

    if (queued.isEmpty()) {
        Text("صف چالش خالیه", style = MaterialTheme.typography.bodySmall)
    } else {
        Column {
            queued.forEachIndexed { index, id ->
                val p = state.players.find { it.id == id }
                if (p != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${index + 1}. ${p.name}", fontWeight = FontWeight.Bold)
                        TextButton(onClick = { state.removeFromChallenge(id) }) { Text("صحبت کرد") }
                    }
                }
            }
            TextButton(onClick = { state.clearChallengeQueue() }) { Text("پاک‌کردن صف") }
        }
    }
}

@Composable
private fun RolesOverview(
    preset: RolePreset,
    players: List<Player>,
    filterPlayerId: Int? = null
) {
    if (filterPlayerId != null) {
        val player = players.find { it.id == filterPlayerId }
        if (player != null) {
            SinglePlayerRoleCard(player = player, preset = preset)
        }
        return
    }

    Column {
        preset.teams.forEach { team ->
            val teamPlayers = players.filter { it.role?.teamId == team.id }
            if (teamPlayers.isNotEmpty()) {
                val teamColor = parseHexColor(team.colorHex)
                Row(
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(10.dp).background(teamColor, CircleShape))
                    Spacer(Modifier.width(6.dp))
                    Text(team.name, fontWeight = FontWeight.Bold, color = teamColor)
                }
                teamPlayers.forEach { p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(teamColor.copy(alpha = 0.12f), MaterialTheme.shapes.small)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PlayerAvatar(name = p.name, size = 32.dp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                p.name,
                                textDecoration = if (!p.isAlive) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                        Text(p.role?.name ?: "-", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SinglePlayerRoleCard(player: Player, preset: RolePreset) {
    val teamDef = preset.team(player.role?.teamId ?: "")
    val teamColor = teamDef?.colorHex?.let { parseHexColor(it) } ?: MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayerAvatar(name = player.name, size = 56.dp)
        Spacer(Modifier.height(8.dp))
        Text(player.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .background(teamColor, MaterialTheme.shapes.small)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(player.role?.name ?: "-", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Text(teamDef?.name ?: "", style = MaterialTheme.typography.bodySmall)
    }
}
