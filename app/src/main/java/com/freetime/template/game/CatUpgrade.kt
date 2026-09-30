package com.freetime.catempire.game

import kotlin.math.pow

enum class UpgradeEffect {
    PET_POWER,
    CAT_PRODUCTION,
}

data class CatUpgrade(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val baseCost: Double,
    val effect: UpgradeEffect,
    val bonusPerLevel: Double,
    val level: Int = 0,
) {
    val nextCost: Double
        get() = baseCost * 1.6.pow(level)

    val totalBonus: Double
        get() = bonusPerLevel * level
}

fun starterUpgrades() = listOf(
    CatUpgrade("wool_ball", "Wool Ball", "🧶", "More fun makes every pet more rewarding.", 50.0, UpgradeEffect.PET_POWER, 1.0),
    CatUpgrade("feather_wand", "Feather Wand", "🪶", "A favorite toy that boosts every pet.", 300.0, UpgradeEffect.PET_POWER, 3.0),
    CatUpgrade("cardboard_box", "Cardboard Box", "📦", "Every cat knows the box is better than the toy.", 750.0, UpgradeEffect.CAT_PRODUCTION, 0.10),
    CatUpgrade("toy_mouse", "Toy Mouse", "🐭", "Keeps the whole colony active.", 2_500.0, UpgradeEffect.CAT_PRODUCTION, 0.15),
    CatUpgrade("cat_tree", "Cat Tree", "🌳", "More places to climb, nap and purr.", 10_000.0, UpgradeEffect.CAT_PRODUCTION, 0.25),
    CatUpgrade("window_bed", "Window Bed", "☀️", "Sunbeams make every cat purr harder.", 50_000.0, UpgradeEffect.CAT_PRODUCTION, 0.50),
)
