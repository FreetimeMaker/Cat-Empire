package com.freetime.catempire.game

import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

enum class BuyAmount(val label: String) {
    ONE("1"),
    TEN("10"),
    HUNDRED("100"),
    MAX("Max"),
}

private const val GENERATOR_COST_GROWTH = 1.15

fun PurrGenerator.bulkCost(amount: Int): Double {
    if (amount <= 0) return 0.0
    val first = baseCost * GENERATOR_COST_GROWTH.pow(owned)
    return first * (GENERATOR_COST_GROWTH.pow(amount) - 1.0) / (GENERATOR_COST_GROWTH - 1.0)
}

fun PurrGenerator.maxAffordable(purrs: Double): Int {
    if (purrs < nextCost) return 0
    val first = baseCost * GENERATOR_COST_GROWTH.pow(owned)
    val value = 1.0 + purrs * (GENERATOR_COST_GROWTH - 1.0) / first
    return floor(ln(value) / ln(GENERATOR_COST_GROWTH)).toInt().coerceAtLeast(1)
}

fun PurrGenerator.purchaseAmount(mode: BuyAmount, purrs: Double): Int = when (mode) {
    BuyAmount.ONE -> 1
    BuyAmount.TEN -> 10
    BuyAmount.HUNDRED -> 100
    BuyAmount.MAX -> maxAffordable(purrs)
}
