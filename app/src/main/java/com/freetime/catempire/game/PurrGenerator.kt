package com.freetime.catempire.game

import kotlin.math.pow

data class PurrGenerator(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val baseCost: Double,
    val basePurrsPerSecond: Double,
    val owned: Int = 0,
) {
    val nextCost: Double
        get() = baseCost * 1.15.pow(owned)

    val milestoneCount: Int
        get() = GENERATOR_MILESTONES.count { owned >= it }

    val milestoneMultiplier: Double
        get() = 1 shl milestoneCount

    val production: Double
        get() = basePurrsPerSecond * owned * milestoneMultiplier

    val nextMilestone: Int?
        get() = GENERATOR_MILESTONES.firstOrNull { owned < it }
}

fun starterGenerators() = listOf(
    PurrGenerator("cat_bed", "Cat Bed", "🛏️", "A cozy nap spot that generates tiny sleepy purrs.", 15.0, 0.1),
    PurrGenerator("cat_tree_generator", "Cat Tree", "🌳", "Climbing cats keep the purr engine running.", 100.0, 1.0),
    PurrGenerator("window_watch", "Window Watch", "🪟", "Birds outside provide endless entertainment.", 1_100.0, 8.0),
    PurrGenerator("cat_room_generator", "Cat Room", "🐾", "A whole room engineered for maximum purring.", 12_000.0, 47.0),
    PurrGenerator("cat_house", "Cat House", "🏠", "An entire house filled with productive cats.", 130_000.0, 260.0),
    PurrGenerator("cat_cafe_generator", "Cat Café", "☕", "Visitors pay tribute to the purring workforce.", 1_400_000.0, 1_400.0),
    PurrGenerator("cat_sanctuary_generator", "Cat Sanctuary", "🏰", "A huge sanctuary humming with happy cats.", 20_000_000.0, 7_800.0),
    PurrGenerator("purr_factory", "Purr Factory", "🏭", "Nobody knows how it works. It just produces Purrs.", 330_000_000.0, 44_000.0),
    PurrGenerator("cat_city", "Cat City", "🌆", "An entire city dedicated to feline productivity.", 5_100_000_000.0, 260_000.0),
    PurrGenerator("cat_planet", "Cat Planet", "🪐", "A whole world where every citizen is a cat.", 75_000_000_000.0, 1_600_000.0),
)


val GENERATOR_MILESTONES = listOf(10, 25, 50, 100)
