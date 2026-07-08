package com.hiddenrole.app.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.hiddenrole.app.model.RoleTemplate
import com.hiddenrole.app.state.GameStateHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleTemplatesScreen(
    state: GameStateHolder,
    onBack: () -> Unit
) {
    var editingTemplate by remember { mutableStateOf<RoleTemplate?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isNew by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("نقش‌ها") },
                navigationIcon = { TextButton(onClick = onBack) { Text("بازگشت") } },
                actions = {
                    IconButton(onClick = {
                        editingTemplate = null
                        isNew = true
                        showDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "نقش جدید")
                    }
                }
            )
        }
    ) { padding ->
        if (state.roleTemplates.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("هنوز نقشی نساختی. با دکمه‌ی + یکی بساز.")
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(state.roleTemplates, key = { it.id }) { template ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(template.name, fontWeight = FontWeight.Bold)
                                        if (template.description.isNotBlank()) {
                                            Text(template.description, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                    IconButton(onClick = {
                                        editingTemplate = template
                                        isNew = false
                                        showDialog = true
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "ویرایش")
                                    }
                                    IconButton(onClick = {
                                        if (state.isRoleTemplateInUse(template.id)) {
                                            errorMessage = "این نقش توی یه سناریو استفاده شده؛ اول از اون سناریو حذفش کن"
                                        } else {
                                            state.deleteRoleTemplate(template.id)
                                            errorMessage = null
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "حذف")
                                    }
                                }
                                if (template.abilityIds.isNotEmpty()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        template.abilityIds.mapNotNull { state.abilityFor(it)?.name }.joinToString(" • "),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        RoleTemplateEditDialog(
            state = state,
            existing = if (isNew) null else editingTemplate,
            onDismiss = { showDialog = false },
            onConfirm = { template ->
                state.saveRoleTemplate(template)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoleTemplateEditDialog(
    state: GameStateHolder,
    existing: RoleTemplate?,
    onDismiss: () -> Unit,
    onConfirm: (RoleTemplate) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    val selectedAbilityIds = remember {
        mutableStateListOf<String>().apply { existing?.abilityIds?.let { addAll(it) } }
    }

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
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("توضیح (اختیاری)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text("قابلیت‌های این نقش", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                if (state.abilities.isEmpty()) {
                    Text(
                        "هنوز قابلیتی نساختی؛ اول از بخش «قابلیت‌ها» یکی بساز.",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        state.abilities.forEach { ability ->
                            val selected = selectedAbilityIds.contains(ability.id)
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    if (selected) selectedAbilityIds.remove(ability.id)
                                    else selectedAbilityIds.add(ability.id)
                                },
                                label = { Text(ability.name) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank()) {
                    onConfirm(
                        RoleTemplate(
                            id = existing?.id ?: GameStateHolder.newId(),
                            name = name.trim(),
                            description = description.trim(),
                            abilityIds = selectedAbilityIds.toList()
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
