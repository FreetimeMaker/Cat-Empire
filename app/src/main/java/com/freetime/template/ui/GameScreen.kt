package com.freetime.template.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.freetime.template.game.GameViewModel
import java.text.DecimalFormat

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel(),
) {
    val state by viewModel.state

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Cat Empire", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(
            "Build your empire, one purr at a time.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))
        Text(
            "♡ " + formatPurrs(state.purrs) + " Purrs",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            formatPurrs(state.purrsPerSecond) + " Purrs / second",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.weight(1f))
        Text("🐱", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = viewModel::petCat,
            modifier = Modifier.size(190.dp),
            shape = CircleShape,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Pet the cat", style = MaterialTheme.typography.titleLarge)
                Text("+" + formatPurrs(state.purrsPerPet) + " Purr")
            }
        }
        Spacer(Modifier.weight(1f))
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Stat("Pets", state.totalPets.toString())
                Stat("All-time Purrs", formatPurrs(state.totalPurrsEarned))
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private val purrFormat = DecimalFormat("#,##0.##")

private fun formatPurrs(value: Double): String = when {
    value >= 1_000_000_000 -> purrFormat.format(value / 1_000_000_000) + "B"
    value >= 1_000_000 -> purrFormat.format(value / 1_000_000) + "M"
    value >= 1_000 -> purrFormat.format(value / 1_000) + "K"
    else -> purrFormat.format(value)
}
