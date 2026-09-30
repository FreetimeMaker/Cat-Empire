package com.freetime.template.game

data class GameState(
    val purrs: Double = 0.0,
    val purrsPerPet: Double = 1.0,
    val totalPets: Long = 0,
    val totalPurrsEarned: Double = 0.0,
    val cats: List<CatType> = starterCats(),
) {
    val purrsPerSecond: Double
        get() = cats.sumOf(CatType::production)

    val totalCats: Int
        get() = cats.sumOf(CatType::owned)
}
