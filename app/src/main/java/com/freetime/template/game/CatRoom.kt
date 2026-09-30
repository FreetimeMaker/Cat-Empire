package com.freetime.template.game

import kotlin.math.pow

enum class RoomEffect {
    PET_POWER,
    CAT_PRODUCTION,
}

data class CatRoom(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val baseCost: Double,
    val effect: RoomEffect,
    val bonusPerLevel: Double,
    val requiredHomeLevel: Int,
    val level: Int = 0,
) {
    val nextCost: Double
        get() = baseCost * 2.0.pow(level)

    val multiplier: Double
        get() = 1.0 + bonusPerLevel * level
}

fun starterRooms() = listOf(
    CatRoom("living", "Living Room", "🛋️", "A cozy social space for cats and humans.", 2_500.0, RoomEffect.PET_POWER, 0.10, 0),
    CatRoom("bedroom", "Bedroom", "🛏️", "Better naps mean stronger idle production.", 10_000.0, RoomEffect.CAT_PRODUCTION, 0.08, 1),
    CatRoom("kitchen", "Kitchen", "🍽️", "Good food keeps every cat productive.", 35_000.0, RoomEffect.CAT_PRODUCTION, 0.10, 2),
    CatRoom("balcony", "Balcony", "🌤️", "Bird watching keeps the colony inspired.", 100_000.0, RoomEffect.CAT_PRODUCTION, 0.12, 3),
    CatRoom("garden", "Garden", "🌿", "More space for exploring and zoomies.", 300_000.0, RoomEffect.CAT_PRODUCTION, 0.15, 2),
    CatRoom("cat_room", "Cat Room", "🐾", "A room built entirely for cats.", 1_000_000.0, RoomEffect.CAT_PRODUCTION, 0.20, 3),
)
