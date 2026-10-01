package com.freetime.catempire.game

import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

data class LifeUpgrade(
    val id: String,
    val name: String,
    val description: String,
    val baseCost: Int,
    val maxLevel: Int,
) {
    fun cost(level: Int): Int = baseCost * (level + 1)
}

val lifeUpgrades = listOf(
    LifeUpgrade("eternal_purr", "Eternal Purr", "+10% all Purr production per level.", 1, 20),
    LifeUpgrade("eternal_touch", "Eternal Touch", "+25% petting power per level.", 1, 20),
    LifeUpgrade("deep_naps", "Deep Cat Naps", "+30 minutes maximum offline earnings per level.", 2, 16),
    LifeUpgrade("golden_instinct", "Golden Instinct", "Golden Cats appear 5% sooner per level.", 3, 10),
)

fun prestigePointsFor(totalPurrs: Double): Int {
    if (totalPurrs < 1_000_000.0) return 0
    return floor((log10(totalPurrs) - 5.0).pow(2.0)).toInt().coerceAtLeast(1)
}
