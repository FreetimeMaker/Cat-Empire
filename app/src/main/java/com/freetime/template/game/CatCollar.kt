package com.freetime.template.game

data class CatCollar(
    val id: String,
    val name: String,
    val icon: String,
    val productionMultiplier: Double,
    val requiredBond: Int = 1,
    val requiredLives: Int = 0,
)

val catCollars = listOf(
    CatCollar("none", "No Collar", "○", 1.0),
    CatCollar("red", "Red Collar", "🔴", 1.05, requiredBond = 5),
    CatCollar("blue", "Blue Collar", "🔵", 1.10, requiredBond = 10),
    CatCollar("gold", "Golden Collar", "🟡", 1.20, requiredBond = 25),
    CatCollar("best_friend", "Best Friend Collar", "❤️", 1.35, requiredBond = 50),
    CatCollar("nine_lives", "Nine Lives Collar", "✨", 1.50, requiredBond = 50, requiredLives = 1),
)

fun collarById(id: String): CatCollar = catCollars.firstOrNull { it.id == id } ?: catCollars.first()
