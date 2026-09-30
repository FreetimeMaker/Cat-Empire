package com.freetime.template.game

import kotlin.math.pow

data class CatType(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val baseCost: Double,
    val basePurrsPerSecond: Double,
    val owned: Int = 0,
) {
    val nextCost: Double
        get() = baseCost * COST_GROWTH.pow(owned)

    val production: Double
        get() = basePurrsPerSecond * owned

    companion object {
        private const val COST_GROWTH = 1.15
    }
}

fun starterCats() = listOf(
    CatType("street", "Street Cat", "🐈", "A friendly stray looking for a home.", 15.0, 0.5),
    CatType("house", "House Cat", "🐱", "Professional napper and purr machine.", 100.0, 3.0),
    CatType("black", "Black Cat", "🐈‍⬛", "Quiet, curious and especially active at night.", 750.0, 15.0),
    CatType("maine_coon", "Maine Coon", "😺", "A giant floof with an equally giant purr.", 5_000.0, 75.0),
)
