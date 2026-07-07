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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hiddenrole.app.model.RoleDef
import com.hiddenrole.app.model.RolePreset
import com.hiddenrole.app.model.TeamDef
import com.hiddenrole.app.state.GameStateHolder

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

    var showTeamDialog by remember { mutableStateOf(false) }
    var roleBeingEdited by remember { mutableStateOf<RoleDef?>(null) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (existing == null) "قالب جدید" else "ویرایش قالب") })
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
                                presetName.isBlank() -> errorMessage = "اسم قالب رو وارد کن"
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
                        Text("ذخیره‌ی قالب")
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
        ) {
            OutlinedTextField(
                value = presetName,
                onValueChange = { presetName = it },
                label = { Text("اسم قالب") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("تیم‌ها", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = { showTeamDialog = true }) { Text("+ تیم جدید") }
            }
            teams.forEach { team ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(team.name)
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

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(roles, key = { it.id }) { role ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(role.name, fontWeight = FontWeight.Bold)
                                Text(
                                    teams.find { it.id == role.teamId }?.name ?: "-",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                val badges = mutableListOf<String>()
                                if (role.isFiller) badges.add("پیش‌فرض بقیه")
                                if (role.hasNightAction) badges.add("اقدام شبانه")
                                if (!role.isFiller) badges.add("تعداد: ${role.defaultCount}")
                                if (badges.isNotEmpty()) {
                                    Text(badges.joinToString(" • "), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            IconButton(onClick = {
                                roleBeingEdited = role
                                showRoleDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "ویرایش نقش")
                            }
                            IconButton(onClick = { roles.remove(role) }) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف نقش")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTeamDialog) {
        AddTeamDialog(
            onDismiss = { showTeamDialog = false },
            onConfirm = { name ->
                teams.add(TeamDef(id = GameStateHolder.newId(), name = name))
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
private fun AddTeamDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تیم جدید") },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم تیم") })
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onConfirm(name.trim()) }) { Text("افزودن") }
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
