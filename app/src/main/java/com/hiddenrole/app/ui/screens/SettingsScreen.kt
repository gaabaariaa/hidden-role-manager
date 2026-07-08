package com.hiddenrole.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import com.hiddenrole.app.model.AppSettings
import com.hiddenrole.app.state.GameStateHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: GameStateHolder,
    onBack: () -> Unit
) {
    var soundEnabled by remember { mutableStateOf(state.settings.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(state.settings.vibrationEnabled) }
    var dayTimer by remember { mutableStateOf(state.settings.defaultDayTimerSeconds) }
    var showResetDialog by remember { mutableStateOf(false) }

    fun persist() {
        state.updateSettings(AppSettings(soundEnabled, vibrationEnabled, dayTimer))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیمات") },
                navigationIcon = { TextButton(onClick = onBack) { Text("بازگشت") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            SettingRow(label = "صدا هنگام پایان تایمر") {
                Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it; persist() })
            }
            SettingRow(label = "ویبره هنگام پایان تایمر") {
                Switch(checked = vibrationEnabled, onCheckedChange = { vibrationEnabled = it; persist() })
            }

            Spacer(Modifier.height(20.dp))
            Text("مدت زمان تایمر بحث روز", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            TimerStepper(seconds = dayTimer, onChange = { dayTimer = it; persist() })

            Spacer(Modifier.height(32.dp))
            OutlinedButton(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) { Text("بازنشانی کامل داده‌ها") }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("بازنشانی کامل") },
            text = { Text("همه‌ی سناریوها، بازیکنان دائمی و تاریخچه‌ی بازی‌ها پاک می‌شن. مطمئنی؟") },
            confirmButton = {
                Button(onClick = {
                    state.resetAllData()
                    soundEnabled = state.settings.soundEnabled
                    vibrationEnabled = state.settings.vibrationEnabled
                    dayTimer = state.settings.defaultDayTimerSeconds
                    showResetDialog = false
                }) { Text("پاک کن") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) { Text("انصراف") }
            }
        )
    }
}

@Composable
private fun SettingRow(label: String, control: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        control()
    }
}

@Composable
private fun TimerStepper(seconds: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { if (seconds > 10) onChange(seconds - 10) }) { Text("−") }
        val m = seconds / 60
        val s = seconds % 60
        Text("%d:%02d".format(m, s), modifier = Modifier.padding(horizontal = 12.dp))
        IconButton(onClick = { onChange(seconds + 10) }) { Text("+") }
    }
}
