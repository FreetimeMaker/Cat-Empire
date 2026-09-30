package com.freetime.template.game

data class GameState(
    val purrs: Double = 0.0,
    val purrsPerPet: Double = 1.0,
    val purrsPerSecond: Double = 0.0,
    val totalPets: Long = 0,
    val totalPurrsEarned: Double = 0.0,
)
