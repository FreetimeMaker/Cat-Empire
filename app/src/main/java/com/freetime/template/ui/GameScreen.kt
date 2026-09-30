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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.freetime.template.game.CatType
import com.freetime.template.game.CatUpgrade
import com.freetime.template.game.UpgradeEffect
import com.freetime.template.game.GameViewModel
import com.freetime.template.game.OwnedCat
import com.freetime.template.game.pawchievements
import com.freetime.template.game.catCollars
import com.freetime.template.game.collarById
import java.text.DecimalFormat
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel(),
) {
    val state by viewModel.state
    var selectedTab by remember { mutableStateOf(GameTab.HOME) }
    var petFeedback by remember { mutableStateOf(false) }
    var petPulse by remember { mutableStateOf(false) }
    var confirmNewLife by remember { mutableStateOf(false) }

    LaunchedEffect(petFeedback) {
        if (petFeedback) {
            delay(450L)
            petFeedback = false
            petPulse = false
        }
    }

    when {
        confirmNewLife -> {
            AlertDialog(
                onDismissRequest = { confirmNewLife = false },
                title = { Text("Start a new life?") },
                text = { Text("Your current cats, Purrs, home, rooms and upgrades reset. Catdex discoveries, achievements and all-time stats stay. You gain a permanent +25% production bonus.") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.startNewLife()
                        confirmNewLife = false
                    }) { Text("Start new life") }
                },
                dismissButton = { Button(onClick = { confirmNewLife = false }) { Text("Cancel") } },
            )
        }
        state.lastCatNapPurrs > 0.0 -> {
            AlertDialog(
                onDismissRequest = viewModel::dismissCatNap,
                title = { Text("Cat Nap") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Your cats kept purring while you were away.")
                        Text("Nap time: " + formatDuration(state.lastCatNapSeconds))
                        Text("+" + formatPurrs(state.lastCatNapPurrs) + " Purrs", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Offline production is counted for up to 8 hours.")
                    }
                },
                confirmButton = { Button(onClick = viewModel::dismissCatNap) { Text("Collect") } },
            )
        }
        state.lastMysteryReward != null -> {
            AlertDialog(
                onDismissRequest = viewModel::dismissMysteryReward,
                title = { Text("Mystery Box") },
                text = { Text(state.lastMysteryReward.orEmpty()) },
                confirmButton = { Button(onClick = viewModel::dismissMysteryReward) { Text("Collect") } },
            )
        }
        state.lastEventResult != null -> {
            AlertDialog(
                onDismissRequest = viewModel::dismissEventResult,
                title = { Text("Cat Event") },
                text = { Text(state.lastEventResult.orEmpty()) },
                confirmButton = { Button(onClick = viewModel::dismissEventResult) { Text("Continue") } },
            )
        }
        state.activeEvent != null -> {
            val event = state.activeEvent
            AlertDialog(
                onDismissRequest = {},
                title = { Text(event.emoji + " " + event.title) },
                text = { Text(event.description) },
                confirmButton = { Button(onClick = viewModel::resolveEvent) { Text(event.action) } },
            )
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    "♡ " + formatPurrs(state.purrs) + " Purrs",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    formatPurrs(state.purrsPerSecond) + " Purrs / second",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        if (state.eventSeconds > 0) {
            ZoomiesAnimation(active = state.eventMultiplier >= 3.0)
            Text(
                "Event boost: x" + purrFormat.format(state.eventMultiplier) + " - " + state.eventSeconds + "s",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }

        if (selectedTab.showsHome()) {
        val activeCat = state.ownedCats.firstOrNull { it.id == state.selectedCatId }
        if (activeCat != null) {
            val activeType = state.cats.firstOrNull { it.id == activeCat.typeId }
            CatActivityScene(cat = activeCat, typeEmoji = activeType?.emoji ?: "🐱")
        }
        Text(
            activeCat?.let { state.cats.firstOrNull { type -> type.id == it.typeId }?.emoji } ?: "🐱",
            modifier = Modifier.graphicsLayer {
                scaleX = if (petPulse) 1.18f else 1f
                scaleY = if (petPulse) 1.18f else 1f
            },
            style = MaterialTheme.typography.displayLarge,
        )
        PetFeedback(visible = petFeedback, amount = formatPurrs(state.purrsPerPet))
        Button(
            onClick = {
                viewModel.petCat()
                petFeedback = true
                petPulse = true
            },
            modifier = Modifier.size(190.dp),
            shape = CircleShape,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    state.ownedCats.firstOrNull { it.id == state.selectedCatId }?.let { "Pet " + it.name } ?: "Pet the cat",
                    style = MaterialTheme.typography.titleLarge,
                )
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
        Text("Morning Meow", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val now = System.currentTimeMillis()
                val elapsed = now - state.lastDailyClaimAt
                val canClaim = state.lastDailyClaimAt == 0L || elapsed >= 24L * 60L * 60L * 1000L
                val nextStreak = if (state.lastDailyClaimAt > 0L && elapsed <= 48L * 60L * 60L * 1000L) {
                    (state.dailyStreak + 1).coerceAtMost(7)
                } else {
                    1
                }
                val reward = (state.purrsPerSecond * (120.0 + nextStreak * 60.0)).coerceAtLeast(250.0 * nextStreak)
                Text("Current streak: " + state.dailyStreak + " / 7 days")
                Text("Next reward: " + formatPurrs(reward) + " Purrs")
                Button(onClick = viewModel::claimDailyReward, enabled = canClaim, modifier = Modifier.fillMaxWidth()) {
                    Text(if (canClaim) "Claim Morning Meow" else "Come back tomorrow")
                }
            }
        }

        }

        if (selectedTab.showsMore()) {
        Spacer(Modifier.height(4.dp))
        Text("Nine Lives", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Lives: " + state.lives, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Permanent production: x" + purrFormat.format(state.prestigeMultiplier))
                Text("Each new life permanently adds 25% production.")
                Button(onClick = { confirmNewLife = true }, enabled = state.homeLevel >= 5, modifier = Modifier.fillMaxWidth()) {
                    Text(if (state.homeLevel >= 5) "Start a new life" else "Reach Cat Sanctuary to unlock")
                }
            }
        }

        }

        if (selectedTab.showsHome()) {
        Spacer(Modifier.height(4.dp))
                Text("Cat Home", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(state.home.emoji + " " + state.home.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(state.home.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Empire production: x" + purrFormat.format(state.home.productionMultiplier))
                Text("Rooms: " + state.home.rooms.joinToString(" · "))
                if (state.homeLevel < com.freetime.template.game.catHomes.lastIndex) {
                    Button(
                        onClick = viewModel::upgradeHome,
                        enabled = state.purrs >= state.home.upgradeCost,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Expand home for " + formatPurrs(state.home.upgradeCost) + " Purrs")
                    }
                } else {
                    Text("Maximum empire level reached", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        Text("Rooms", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        state.rooms.forEach { room ->
            val unlocked = state.homeLevel >= room.requiredHomeLevel
            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(room.icon + " " + room.name + " · Level " + room.level, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(room.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        when (room.effect) {
                            com.freetime.template.game.RoomEffect.PET_POWER -> "Pet power: x" + purrFormat.format(room.multiplier)
                            com.freetime.template.game.RoomEffect.CAT_PRODUCTION -> "Cat production: x" + purrFormat.format(room.multiplier)
                        }
                    )
                    Button(
                        onClick = { viewModel.upgradeRoom(room.id) },
                        enabled = unlocked && state.purrs >= room.nextCost,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (unlocked) "Upgrade for " + formatPurrs(room.nextCost) + " Purrs" else "Unlock with home level " + room.requiredHomeLevel)
                    }
                }
            }
        }

        }

        if (selectedTab.showsCats()) {
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

        }

        if (selectedTab.showsCatdex()) {
        Spacer(Modifier.height(4.dp))
        Text("Catdex", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        val discoveredTypes = state.discoveredCatTypes + state.ownedCats.map { it.typeId }
        val discoveredPersonalities = state.discoveredPersonalities + state.ownedCats.map { it.personality }
        val discoveredRarities = state.discoveredRarities + state.ownedCats.map { it.rarity }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Discoveries", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Cat types: " + discoveredTypes.size + " / " + state.cats.size)
                Text("Personalities: " + discoveredPersonalities.size + " / 7")
                Text("Rarities: " + discoveredRarities.size + " / 4")
                state.cats.forEach { type ->
                    val discovered = type.id in discoveredTypes || type.owned > 0
                    Text(if (discovered) type.emoji + " " + type.name else "? Undiscovered cat")
                }
            }
        }

        }

        if (selectedTab.showsCats() && state.ownedCats.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text("My Cats", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Every cat has its own name, personality and rarity.", modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurfaceVariant)
            state.ownedCats.asReversed().forEach { owned ->
                MyCatCard(
                    cat = owned,
                    type = state.cats.firstOrNull { it.id == owned.typeId },
                    selected = owned.id == state.selectedCatId,
                    onSelect = { viewModel.selectCat(owned.id) },
                    lives = state.lives,
                    onCollar = { collarId -> viewModel.equipCollar(owned.id, collarId) },
                )
            }
        }

        if (selectedTab.showsMore()) {
        Spacer(Modifier.height(4.dp))
        Text("Pawchievements", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        pawchievements.forEach { achievement ->
            val unlocked = achievement.unlocked(state)
            val claimed = achievement.id in state.claimedPawchievements
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(achievement.emoji + " " + achievement.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(achievement.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Reward: " + formatPurrs(achievement.reward) + " Purrs")
                    Button(
                        onClick = { viewModel.claimPawchievement(achievement.id) },
                        enabled = unlocked && !claimed,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (claimed) "Claimed" else if (unlocked) "Claim reward" else "Locked")
                    }
                }
            }
        }

        }

        if (selectedTab.showsUpgrades()) {
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

        }

        Spacer(Modifier.height(24.dp))
    }
    CatEmpireBottomBar(selected = selectedTab, onSelect = { selectedTab = it })
    }
}

@Composable
private fun MyCatCard(
    cat: OwnedCat,
    type: CatType?,
    selected: Boolean,
    onSelect: () -> Unit,
    lives: Int,
    onCollar: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text((type?.emoji ?: "Cat") + " " + cat.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text((type?.name ?: "Cat") + " - " + cat.personality.label)
            Text(cat.rarity.label + " - " + formatPurrs(cat.production(type?.basePurrsPerSecond ?: 0.0)) + " Purrs/s", color = MaterialTheme.colorScheme.primary)
            Text(cat.activity.emoji + " " + cat.activity.label + " - x" + purrFormat.format(cat.activity.multiplier))
            Text("Bond Level " + cat.bondLevel + " - " + cat.bondProgress + " / 25 XP")
            Text("Bond production bonus: x" + purrFormat.format(cat.bondMultiplier))
            val equipped = collarById(cat.collarId)
            Text("Collar: " + equipped.icon + " " + equipped.name + " - x" + purrFormat.format(equipped.productionMultiplier))
            catCollars.filter { it.id != cat.collarId }.forEach { collar ->
                val unlocked = cat.bondLevel >= collar.requiredBond && lives >= collar.requiredLives
                Button(onClick = { onCollar(collar.id) }, enabled = unlocked, modifier = Modifier.fillMaxWidth()) {
                    Text(if (unlocked) "Equip " + collar.icon + " " + collar.name else collar.name + " - Bond " + collar.requiredBond)
                }
            }
            Button(onClick = onSelect, enabled = !selected, modifier = Modifier.fillMaxWidth()) {
                Text(if (selected) "Selected for pets" else "Choose for pets")
            }
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
