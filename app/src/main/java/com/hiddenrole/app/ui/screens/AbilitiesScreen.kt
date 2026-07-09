package com.hiddenrole.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hiddenrole.app.model.Ability
import com.hiddenrole.app.state.GameStateHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbilitiesScreen(
    state: GameStateHolder,
    onBack: () -> Unit
) {
    var editingAbility by remember { mutableStateOf<Ability?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isNew by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("قابلیت‌ها") },
                navigationIcon = { TextButton(onClick = onBack) { Text("بازگشت") } },
                actions = {
                    IconButton(onClick = {
                        editingAbility = null
                        isNew = true
                        showDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "قابلیت جدید")
                    }
                }
            )
        }
    ) { padding ->
        if (state.abilities.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("هنوز قابلیتی نساختی. با دکمه‌ی + یکی بساز.")
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                errorMessage?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(state.abilities, key = { it.id }) { ability ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(ability.name, fontWeight = FontWeight.Bold)
                                        if (ability.wakesAtNight) {
                                            Spacer(Modifier.width(6.dp))
                                            Icon(
                                                Icons.Default.NightsStay,
                                                contentDescription = "نیاز به بیدار کردن شب",
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                    if (ability.description.isNotBlank()) {
                                        Text(ability.description, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                IconButton(onClick = {
                                    editingAbility = ability
                                    isNew = false
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "ویرایش")
                                }
                                IconButton(onClick = {
                                    if (state.isAbilityInUse(ability.id)) {
                                        errorMessage = "این قابلیت توی یه نقش استفاده شده؛ اول از اون نقش حذفش کن"
                                    } else {
                                        state.deleteAbility(ability.id)
                                        errorMessage = null
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AbilityEditDialog(
            existing = if (isNew) null else editingAbility,
            onDismiss = { showDialog = false },
            onConfirm = { ability ->
                state.saveAbility(ability)
                showDialog = false
            }
        )
    }
}

@Composable
private fun AbilityEditDialog(
    existing: Ability?,
    onDismiss: () -> Unit,
    onConfirm: (Ability) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var wakesAtNight by remember { mutableStateOf(existing?.wakesAtNight ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "قابلیت جدید" else "ویرایش قابلیت") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم قابلیت") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("توضیح (اختیاری)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("نیاز داره گرداننده شب بیدارش کنه؟")
                    Switch(checked = wakesAtNight, onCheckedChange = { wakesAtNight = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank()) {
                    onConfirm(
                        Ability(
                            id = existing?.id ?: GameStateHolder.newId(),
                            name = name.trim(),
                            description = description.trim(),
                            wakesAtNight = wakesAtNight
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
