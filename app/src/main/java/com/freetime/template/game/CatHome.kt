package com.freetime.template.game

data class CatHome(
    val level: Int,
    val name: String,
    val emoji: String,
    val description: String,
    val upgradeCost: Double,
    val productionMultiplier: Double,
    val rooms: List<String>,
)

val catHomes = listOf(
    CatHome(0, "Small Apartment", "🏢", "A tiny start for a growing cat family.", 5_000.0, 1.0, listOf("Living Room")),
    CatHome(1, "Cozy Home", "🏠", "More space, softer beds and happier cats.", 25_000.0, 1.15, listOf("Living Room", "Bedroom")),
    CatHome(2, "House + Garden", "🌳", "Fresh air and windows full of birds.", 150_000.0, 1.35, listOf("Living Room", "Bedroom", "Kitchen", "Garden")),
    CatHome(3, "Cat House", "🐾", "A home designed around cats, not humans.", 1_000_000.0, 1.65, listOf("Living Room", "Bedroom", "Kitchen", "Balcony", "Garden", "Cat Room")),
    CatHome(4, "Cat Cafe", "☕", "Visitors come for coffee and stay for the cats.", 10_000_000.0, 2.1, listOf("Cafe", "Kitchen", "Cat Room", "Garden")),
    CatHome(5, "Cat Sanctuary", "🏰", "The ultimate home for every cat in the empire.", 0.0, 3.0, listOf("Sanctuary", "Cat Room", "Garden", "Playground", "Nap Hall")),
)
