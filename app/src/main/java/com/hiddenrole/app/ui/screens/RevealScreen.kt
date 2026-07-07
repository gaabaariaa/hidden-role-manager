package com.hiddenrole.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hiddenrole.app.state.GameStateHolder

@Composable
fun RevealScreen(
    state: GameStateHolder,
    onFinishReveal: () -> Unit
) {
    var revealed by remember(state.revealIndex) { mutableStateOf(false) }
    val player = state.players.getOrNull(state.revealIndex)
    val preset = state.selectedPreset

    if (player == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("خطا: بازیکنی پیدا نشد")
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("گوشی رو بده به:", style = MaterialTheme.typography.bodyLarge)
        Text(player.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(190.dp).clickable { revealed = !revealed },
            colors = CardDefaults.cardColors(
                containerColor = if (revealed) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (revealed) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            player.role?.name ?: "",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            preset?.teamName(player.role?.teamId ?: "") ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        val description = player.role?.description ?: ""
                        if (description.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(description, style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                    }
                } else {
                    Text("برای دیدن نقش لمس کن", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if (state.isRevealDone()) onFinishReveal() else state.nextReveal()
            },
            enabled = revealed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isRevealDone()) "شروع بازی" else "نفر بعدی")
        }
    }
}
