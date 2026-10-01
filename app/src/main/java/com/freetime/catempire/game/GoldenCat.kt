package com.freetime.catempire.game

enum class GoldenCatReward {
    FRENZY,
    PET_FRENZY,
    LUCKY_PURRS,
}

data class GoldenCat(
    val secondsLeft: Int = 13,
)

data class GoldenCatBuff(
    val name: String,
    val secondsLeft: Int,
    val productionMultiplier: Double = 1.0,
    val petMultiplier: Double = 1.0,
)
