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
import androidx.compose.material3.AlertDialog
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
import com.freetime.template.game.CatUpgrade
import com.freetime.template.game.UpgradeEffect
import com.freetime.template.game.GameViewModel
import com.freetime.template.game.OwnedCat
import java.text.DecimalFormat

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel(),
) {
    val state by viewModel.state

    if (state.lastCatNapPurrs > 0.0) {
        AlertDialog(
            onDismissRequest = viewModel::dismissCatNap,
            title = { Text("💤 Cat Nap") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your cats kept purring while you were away.")
                    Text("Nap time: " + formatDuration(state.lastCatNapSeconds))
                    Text(
                        "♡ +" + formatPurrs(state.lastCatNapPurrs) + " Purrs",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Offline production is counted for up to 8 hours.")
                }
            },
            confirmButton = {
                Button(onClick = viewModel::dismissCatNap) {
                    Text("Collect")
                }
            },
        )
    }

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

        if (state.ownedCats.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text("My Cats", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Every cat has its own name, personality and rarity.", modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurfaceVariant)
            state.ownedCats.asReversed().forEach { owned ->
                MyCatCard(owned, state.cats.firstOrNull { it.id == owned.typeId })
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            "Cat Toys & Upgrades",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "Spoil your cats with toys and cozy furniture to grow your empire faster.",
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        state.upgrades.forEach { upgrade ->
            UpgradeCard(
                upgrade = upgrade,
                canAfford = state.purrs >= upgrade.nextCost,
                onBuy = { viewModel.buyUpgrade(upgrade.id) },
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun MyCatCard(cat: OwnedCat, type: CatType?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text((type?.emoji ?: "Cat") + " " + cat.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text((type?.name ?: "Cat") + " - " + cat.personality.label)
            Text(cat.rarity.label + " - " + formatPurrs(cat.production(type?.basePurrsPerSecond ?: 0.0)) + " Purrs/s", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun UpgradeCard(
    upgrade: CatUpgrade,
    canAfford: Boolean,
    onBuy: () -> Unit,
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
                    Text(upgrade.emoji, style = MaterialTheme.typography.headlineLarge)
                    Column {
                        Text(upgrade.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Level " + upgrade.level)
                    }
                }
                Text(
                    when (upgrade.effect) {
                        UpgradeEffect.PET_POWER -> "+" + formatPurrs(upgrade.bonusPerLevel) + " / pet"
                        UpgradeEffect.CAT_PRODUCTION -> "+" + (upgrade.bonusPerLevel * 100).toInt() + "% cats"
                    },
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Text(upgrade.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(
                onClick = onBuy,
                enabled = canAfford,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Upgrade for ♡ " + formatPurrs(upgrade.nextCost))
            }
        }
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


private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return when {
        hours > 0 -> hours.toString() + "h " + minutes.toString() + "m"
        minutes > 0 -> minutes.toString() + "m"
        else -> seconds.toString() + "s"
    }
}
