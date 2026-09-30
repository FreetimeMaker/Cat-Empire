package com.freetime.catempire.game

data class Pawchievement(
    val id: String,
    val emoji: String,
    val name: String,
    val description: String,
    val reward: Double,
    val unlocked: (GameState) -> Boolean,
)

val pawchievements = listOf(
    Pawchievement("first_paw", "🐾", "First Paw", "Pet a cat for the first time.", 25.0) { it.totalPets >= 1 },
    Pawchievement("pet_pro", "😺", "Pet Professional", "Pet your cats 100 times.", 500.0) { it.totalPets >= 100 },
    Pawchievement("cat_person", "🐈", "Cat Person", "Adopt your first cat.", 100.0) { it.totalCats >= 1 },
    Pawchievement("crazy_cat_person", "🐈‍⬛", "Crazy Cat Person", "Have 10 cats in your empire.", 5_000.0) { it.totalCats >= 10 },
    Pawchievement("full_house", "🏠", "Full House", "Discover every cat type.", 10_000.0) { state ->
        state.cats.all { type -> type.owned > 0 || state.ownedCats.any { it.typeId == type.id } }
    },
    Pawchievement("best_friends", "❤️", "Best Friends", "Reach Bond Level 100 with a cat.", 50_000.0) {
        it.ownedCats.any { cat -> cat.bondLevel >= 100 }
    },
    Pawchievement("million_purrs", "✨", "Purr Millionaire", "Earn 1,000,000 Purrs in total.", 100_000.0) {
        it.totalPurrsEarned >= 1_000_000.0
    },
    Pawchievement("legendary", "👑", "Legendary Floof", "Adopt a Legendary cat.", 25_000.0) {
        it.ownedCats.any { cat -> cat.rarity == CatRarity.LEGENDARY }
    },
)
