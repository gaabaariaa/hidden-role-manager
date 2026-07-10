package com.hiddenrole.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hiddenrole.app.model.RolePreset
import com.hiddenrole.app.model.RoleTemplate
import com.hiddenrole.app.model.ScenarioRole
import com.hiddenrole.app.model.TeamDef
import com.hiddenrole.app.state.GameStateHolder
import com.hiddenrole.app.util.parseHexColor
import com.hiddenrole.app.util.teamColorPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetBuilderScreen(
    state: GameStateHolder,
    presetId: String,
    onDone: () -> Unit
) {
    val existing = remember(presetId) { state.presets.find { it.id == presetId } }

    var presetName by remember { mutableStateOf(existing?.name ?: "") }
    val teams = remember { mutableStateListOf<TeamDef>().apply { existing?.teams?.let { addAll(it) } } }
    val slots = remember { mutableStateListOf<ScenarioRole>().apply { existing?.roleSlots?.let { addAll(it) } } }
    val nightOrderSlotIds = remember { mutableStateListOf<String>().apply { existing?.nightOrder?.let { addAll(it) } } }

    fun qualifyingNightSlots(): List<ScenarioRole> {
        val qualifying = slots.filter { slot ->
            state.roleTemplateFor(slot.roleTemplateId)?.abilityIds
                ?.any { abilityId -> state.abilityFor(abilityId)?.wakesAtNight == true } == true
        }
        return qualifying.sortedBy { slot ->
            val idx = nightOrderSlotIds.indexOf(slot.id)
            if (idx >= 0) idx else Int.MAX_VALUE
        }
    }

    fun moveNightOrderUp(slotId: String) {
        val current = qualifyingNightSlots().map { it.id }.toMutableList()
        val idx = current.indexOf(slotId)
        if (idx > 0) {
            val temp = current[idx - 1]
            current[idx - 1] = current[idx]
            current[idx] = temp
            nightOrderSlotIds.clear()
            nightOrderSlotIds.addAll(current)
        }
    }

    fun moveNightOrderDown(slotId: String) {
        val current = qualifyingNightSlots().map { it.id }.toMutableList()
        val idx = current.indexOf(slotId)
        if (idx in 0 until current.size - 1) {
            val temp = current[idx + 1]
            current[idx + 1] = current[idx]
            current[idx] = temp
            nightOrderSlotIds.clear()
            nightOrderSlotIds.addAll(current)
        }
    }

    var teamBeingEdited by remember { mutableStateOf<TeamDef?>(null) }
    var showTeamDialog by remember { mutableStateOf(false) }
    var slotBeingEdited by remember { mutableStateOf<ScenarioRole?>(null) }
    var showSlotDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (existing == null) "سناریوی جدید" else "ویرایش سناریو") })
        },
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                errorMessage?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Row {
                    OutlinedButton(onClick = onDone, modifier = Modifier.weight(1f)) {
                        Text("انصراف")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val fillerCount = slots.count { it.isFiller }
                            when {
                                presetName.isBlank() -> errorMessage = "اسم سناریو رو وارد کن"
                                teams.isEmpty() -> errorMessage = "حداقل یک تیم لازمه"
                                slots.isEmpty() -> errorMessage = "حداقل یک نقش لازمه"
                                fillerCount != 1 -> errorMessage =
                                    "دقیقاً باید یک نقش «پیش‌فرض بقیه» انتخاب بشه"
                                else -> {
                                    val preset = RolePreset(
                                        id = existing?.id ?: GameStateHolder.newId(),
                                        name = presetName.trim(),
                                        teams = teams.toList(),
                                        roleSlots = slots.toList(),
                                        nightOrder = qualifyingNightSlots().map { it.id }
                                    )
                                    state.savePreset(preset)
                                    onDone()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("ذخیره‌ی سناریو")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = presetName,
                onValueChange = { presetName = it },
                label = { Text("اسم سناریو") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("تیم‌ها", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = {
                    teamBeingEdited = null
                    showTeamDialog = true
                }) { Text("+ تیم جدید") }
            }
            teams.forEach { team ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(parseHexColor(team.colorHex), CircleShape)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(team.name)
                    }
                    Row {
                        IconButton(onClick = {
                            teamBeingEdited = team
                            showTeamDialog = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "ویرایش تیم")
                        }
                        IconButton(onClick = {
                            val inUse = slots.any { it.teamId == team.id }
                            if (!inUse) {
                                teams.remove(team)
                            } else {
                                errorMessage = "این تیم توسط یک نقش استفاده شده؛ اول نقش رو حذف کن"
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف تیم")
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("نقش‌ها", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(
                    onClick = {
                        when {
                            teams.isEmpty() -> errorMessage = "اول یک تیم بساز"
                            state.roleTemplates.isEmpty() -> errorMessage =
                                "اول از منوی اصلی، بخش «نقش‌ها» یه نقش بساز"
                            else -> {
                                slotBeingEdited = null
                                showSlotDialog = true
                            }
                        }
                    }
                ) { Text("+ نقش جدید") }
            }
            Spacer(Modifier.height(8.dp))

            teams.forEach { team ->
                val teamSlots = slots.filter { it.teamId == team.id }
                if (teamSlots.isNotEmpty()) {
                    val teamColor = parseHexColor(team.colorHex)
                    Row(
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(10.dp).background(teamColor, CircleShape))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            team.name,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = teamColor
                        )
                    }
                    teamSlots.forEach { slot ->
                        val template = state.roleTemplateFor(slot.roleTemplateId)
                        SlotRow(
                            slot = slot,
                            templateName = template?.name ?: "نقش حذف‌شده",
                            abilityNames = template?.abilityIds?.mapNotNull { state.abilityFor(it)?.name } ?: emptyList(),
                            teamColor = teamColor,
                            onEdit = { slotBeingEdited = slot; showSlotDialog = true },
                            onDelete = { slots.remove(slot) }
                        )
                    }
                }
            }

            val nightCandidates = qualifyingNightSlots()
            if (nightCandidates.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    "ترتیب بیدار شدن شب",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "مشخص کن گرداننده هر شب اول کدوم نقش رو بیدار کنه",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
                nightCandidates.forEachIndexed { index, slot ->
                    val template = state.roleTemplateFor(slot.roleTemplateId)
                    val teamColor = teams.find { it.id == slot.teamId }
                        ?.let { parseHexColor(it.colorHex) }
                        ?: MaterialTheme.colorScheme.primary
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(teamColor, CircleShape))
                            Spacer(Modifier.width(8.dp))
                            Text("${index + 1}. ${template?.name ?: "نقش حذف‌شده"}")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { moveNightOrderUp(slot.id) }) { Text("▲") }
                            IconButton(onClick = { moveNightOrderDown(slot.id) }) { Text("▼") }
                        }
                    }
                }
            }
        }
    }

    if (showTeamDialog) {
        TeamEditDialog(
            existing = teamBeingEdited,
            onDismiss = { showTeamDialog = false },
            onConfirm = { newTeam ->
                val index = teams.indexOfFirst { it.id == newTeam.id }
                if (index >= 0) teams[index] = newTeam else teams.add(newTeam)
                showTeamDialog = false
            }
        )
    }

    if (showSlotDialog) {
        SlotEditDialog(
            teams = teams,
            roleTemplates = state.roleTemplates,
            existing = slotBeingEdited,
            hasOtherFiller = slots.any { it.isFiller && it.id != slotBeingEdited?.id },
            onDismiss = { showSlotDialog = false },
            onConfirm = { newSlot ->
                if (newSlot.isFiller) {
                    slots.replaceAll { if (it.id != newSlot.id) it.copy(isFiller = false) else it }
                }
                val index = slots.indexOfFirst { it.id == newSlot.id }
                if (index >= 0) slots[index] = newSlot else slots.add(newSlot)
                showSlotDialog = false
            }
        )
    }
}

@Composable
private fun SlotRow(
    slot: ScenarioRole,
    templateName: String,
    abilityNames: List<String>,
    teamColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(width = 1.dp, color = teamColor.copy(alpha = 0.5f), shape = MaterialTheme.shapes.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(templateName, fontWeight = FontWeight.Bold)
                val badges = mutableListOf<String>()
                if (slot.isFiller) badges.add("پیش‌فرض بقیه")
                if (!slot.isFiller) badges.add("تعداد: ${slot.defaultCount}")
                if (abilityNames.isNotEmpty()) badges.add(abilityNames.joinToString(" و "))
                if (badges.isNotEmpty()) {
                    Text(badges.joinToString(" • "), style = MaterialTheme.typography.labelSmall)
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "ویرایش نقش")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "حذف نقش")
            }
        }
    }
}

@Composable
private fun TeamEditDialog(
    existing: TeamDef?,
    onDismiss: () -> Unit,
    onConfirm: (TeamDef) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var colorHex by remember { mutableStateOf(existing?.colorHex ?: teamColorPalette.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "تیم جدید" else "ویرایش تیم") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم تیم") })
                Spacer(Modifier.height(12.dp))
                Text("رنگ تیم", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    teamColorPalette.forEach { hex ->
                        val color = parseHexColor(hex)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = if (colorHex == hex) 3.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = { colorHex = hex }, modifier = Modifier.size(32.dp)) {
                                if (colorHex == hex) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank()) {
                    onConfirm(
                        TeamDef(
                            id = existing?.id ?: GameStateHolder.newId(),
                            name = name.trim(),
                            colorHex = colorHex
                        )
                    )
                }
            }) { Text("ذخیره") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@Composable
private fun SlotEditDialog(
    teams: List<TeamDef>,
    roleTemplates: List<RoleTemplate>,
    existing: ScenarioRole?,
    hasOtherFiller: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (ScenarioRole) -> Unit
) {
    var selectedTeamId by remember { mutableStateOf(existing?.teamId ?: teams.first().id) }
    var selectedTemplateId by remember {
        mutableStateOf(existing?.roleTemplateId ?: roleTemplates.first().id)
    }
    var isFiller by remember { mutableStateOf(existing?.isFiller ?: false) }
    var defaultCount by remember { mutableStateOf(existing?.defaultCount ?: 1) }
    var teamMenuExpanded by remember { mutableStateOf(false) }
    var templateMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "نقش جدید" else "ویرایش نقش") },
        text = {
            Column {
                Text("نقش (از کتابخونه)", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Box {
                    OutlinedButton(onClick = { templateMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(roleTemplates.find { it.id == selectedTemplateId }?.name ?: "انتخاب نقش")
                    }
                    DropdownMenu(expanded = templateMenuExpanded, onDismissRequest = { templateMenuExpanded = false }) {
                        roleTemplates.forEach { template ->
                            DropdownMenuItem(
                                text = { Text(template.name) },
                                onClick = {
                                    selectedTemplateId = template.id
                                    templateMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("تیم", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Box {
                    OutlinedButton(onClick = { teamMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(teams.find { it.id == selectedTeamId }?.name ?: "انتخاب تیم")
                    }
                    DropdownMenu(expanded = teamMenuExpanded, onDismissRequest = { teamMenuExpanded = false }) {
                        teams.forEach { team ->
                            DropdownMenuItem(
                                text = { Text(team.name) },
                                onClick = {
                                    selectedTeamId = team.id
                                    teamMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("نقش پیش‌فرض بقیه‌ی بازیکن‌ها؟")
                    Switch(checked = isFiller, onCheckedChange = { isFiller = it })
                }
                if (isFiller && hasOtherFiller) {
                    Text(
                        "با انتخاب این گزینه، نقش پیش‌فرض قبلی غیرفعال می‌شه.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (!isFiller) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("تعداد پیش‌فرض")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (defaultCount > 0) defaultCount-- }) { Text("−") }
                            Text(defaultCount.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                            IconButton(onClick = { defaultCount++ }) { Text("+") }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    ScenarioRole(
                        id = existing?.id ?: GameStateHolder.newId(),
                        roleTemplateId = selectedTemplateId,
                        teamId = selectedTeamId,
                        isFiller = isFiller,
                        defaultCount = if (isFiller) 0 else defaultCount
                    )
                )
            }) { Text("ذخیره") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}
