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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.freetime.template.game.CatType
import com.freetime.template.game.GameViewModel
import java.text.DecimalFormat

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel(),
) {
    val state by viewModel.state

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Cat Empire", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(
            "Build your empire, one purr at a time.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Text(
            "♡ " + formatPurrs(state.purrs) + " Purrs",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            formatPurrs(state.purrsPerSecond) + " Purrs / second",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text("🐱", style = MaterialTheme.typography.displayLarge)
        Button(
            onClick = viewModel::petCat,
            modifier = Modifier.size(180.dp),
            shape = CircleShape,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Pet the cat", style = MaterialTheme.typography.titleLarge)
                Text("+" + formatPurrs(state.purrsPerPet) + " Purr")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Stat("Pets", state.totalPets.toString())
                Stat("Cats", state.totalCats.toString())
                Stat("All-time Purrs", formatPurrs(state.totalPurrsEarned))
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            "Adoption Center",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "Give cats a home. Every adopted cat produces Purrs automatically.",
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        state.cats.forEach { cat ->
            CatAdoptionCard(
                cat = cat,
                canAfford = state.purrs >= cat.nextCost,
                onAdopt = { viewModel.adoptCat(cat.id) },
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun CatAdoptionCard(
    cat: CatType,
    canAfford: Boolean,
    onAdopt: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(cat.emoji, style = MaterialTheme.typography.headlineLarge)
                    Column {
                        Text(cat.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Adopted: " + cat.owned)
                    }
                }
                Text(
                    formatPurrs(cat.production) + "/s",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Text(cat.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "Each cat: +" + formatPurrs(cat.basePurrsPerSecond) + " Purrs/s",
                style = MaterialTheme.typography.bodyMedium,
            )

            Button(
                onClick = onAdopt,
                enabled = canAfford,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Adopt for ♡ " + formatPurrs(cat.nextCost))
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
