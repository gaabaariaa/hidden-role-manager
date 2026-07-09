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
import androidx.compose.material.icons.filled.Add
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
import com.hiddenrole.app.model.RoleDef
import com.hiddenrole.app.model.RolePreset
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
    val roles = remember { mutableStateListOf<RoleDef>().apply { existing?.roles?.let { addAll(it) } } }

    var teamBeingEdited by remember { mutableStateOf<TeamDef?>(null) }
    var showTeamDialog by remember { mutableStateOf(false) }
    var roleBeingEdited by remember { mutableStateOf<RoleDef?>(null) }
    var showRoleDialog by remember { mutableStateOf(false) }
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
                            val fillerCount = roles.count { it.isFiller }
                            when {
                                presetName.isBlank() -> errorMessage = "اسم سناریو رو وارد کن"
                                teams.isEmpty() -> errorMessage = "حداقل یک تیم لازمه"
                                roles.isEmpty() -> errorMessage = "حداقل یک نقش لازمه"
                                fillerCount != 1 -> errorMessage =
                                    "دقیقاً باید یک نقش «پیش‌فرض بقیه» انتخاب بشه"
                                else -> {
                                    val preset = RolePreset(
                                        id = existing?.id ?: GameStateHolder.newId(),
                                        name = presetName.trim(),
                                        teams = teams.toList(),
                                        roles = roles.toList()
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
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
                            val inUse = roles.any { it.teamId == team.id }
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
                        if (teams.isEmpty()) {
                            errorMessage = "اول یک تیم بساز"
                        } else {
                            roleBeingEdited = null
                            showRoleDialog = true
                        }
                    }
                ) { Text("+ نقش جدید") }
            }
            Spacer(Modifier.height(8.dp))

            // نقش‌ها به تفکیک تیم و با رنگ همون تیم دسته‌بندی می‌شن
            teams.forEach { team ->
                val teamRoles = roles.filter { it.teamId == team.id }
                if (teamRoles.isNotEmpty()) {
                    val teamColor = parseHexColor(team.colorHex)
                    Row(
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(teamColor, CircleShape)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            team.name,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = teamColor
                        )
                    }
                    teamRoles.forEach { role ->
                        RoleRow(
                            role = role,
                            teamColor = teamColor,
                            onEdit = { roleBeingEdited = role; showRoleDialog = true },
                            onDelete = { roles.remove(role) }
                        )
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

    if (showRoleDialog) {
        RoleEditDialog(
            teams = teams,
            existing = roleBeingEdited,
            hasOtherFiller = roles.any { it.isFiller && it.id != roleBeingEdited?.id },
            onDismiss = { showRoleDialog = false },
            onConfirm = { newRole ->
                if (newRole.isFiller) {
                    roles.replaceAll { if (it.id != newRole.id) it.copy(isFiller = false) else it }
                }
                val index = roles.indexOfFirst { it.id == newRole.id }
                if (index >= 0) roles[index] = newRole else roles.add(newRole)
                showRoleDialog = false
            }
        )
    }
}

@Composable
private fun RoleRow(
    role: RoleDef,
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
                Text(role.name, fontWeight = FontWeight.Bold)
                val badges = mutableListOf<String>()
                if (role.isFiller) badges.add("پیش‌فرض بقیه")
                if (role.hasNightAction) badges.add("اقدام شبانه")
                if (!role.isFiller) badges.add("تعداد: ${role.defaultCount}")
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
private fun RoleEditDialog(
    teams: List<TeamDef>,
    existing: RoleDef?,
    hasOtherFiller: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (RoleDef) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var selectedTeamId by remember { mutableStateOf(existing?.teamId ?: teams.first().id) }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var hasNightAction by remember { mutableStateOf(existing?.hasNightAction ?: false) }
    var isFiller by remember { mutableStateOf(existing?.isFiller ?: false) }
    var defaultCount by remember { mutableStateOf(existing?.defaultCount ?: 1) }
    var teamMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "نقش جدید" else "ویرایش نقش") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم نقش") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

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

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("توضیح نقش (اختیاری)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("اقدام شبانه داره؟")
                    Switch(checked = hasNightAction, onCheckedChange = { hasNightAction = it })
                }

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
                if (name.isNotBlank()) {
                    onConfirm(
                        RoleDef(
                            id = existing?.id ?: GameStateHolder.newId(),
                            name = name.trim(),
                            teamId = selectedTeamId,
                            description = description.trim(),
                            hasNightAction = hasNightAction,
                            isFiller = isFiller,
                            defaultCount = if (isFiller) 0 else defaultCount
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
