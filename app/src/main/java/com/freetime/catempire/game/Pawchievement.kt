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
    Pawchievement("pet_100", "😺", "Pet Professional", "Pet cats 100 times.", 500.0) { it.totalPets >= 100 },
    Pawchievement("pet_1000", "🐾", "Paw Workout", "Pet cats 1,000 times.", 5_000.0) { it.totalPets >= 1_000 },
    Pawchievement("pet_10000", "💪", "Unstoppable Paws", "Pet cats 10,000 times.", 50_000.0) { it.totalPets >= 10_000 },
    Pawchievement("cat_person", "🐈", "Cat Person", "Adopt your first cat.", 100.0) { it.totalCats >= 1 },
    Pawchievement("crazy_cat_person", "🐈‍⬛", "Crazy Cat Person", "Have 10 cats in one life.", 5_000.0) { it.totalCats >= 10 },
    Pawchievement("full_house", "🏠", "Full House", "Discover every cat type.", 10_000.0) { state ->
        state.cats.all { type -> type.id in state.discoveredCatTypes || state.ownedCats.any { it.typeId == type.id } }
    },
    Pawchievement("best_friends", "❤️", "Best Friends", "Reach Bond Level 100 with a cat.", 50_000.0) { it.ownedCats.any { cat -> cat.bondLevel >= 100 } },
    Pawchievement("purr_million", "✨", "Purr Millionaire", "Earn 1 million lifetime Purrs.", 100_000.0) { it.totalPurrsEarned >= 1e6 },
    Pawchievement("purr_billion", "💎", "Purr Billionaire", "Earn 1 billion lifetime Purrs.", 1_000_000.0) { it.totalPurrsEarned >= 1e9 },
    Pawchievement("purr_trillion", "🌌", "Purr Trillionaire", "Earn 1 trillion lifetime Purrs.", 10_000_000.0) { it.totalPurrsEarned >= 1e12 },
    Pawchievement("legendary", "👑", "Legendary Floof", "Adopt a Legendary cat.", 25_000.0) { it.ownedCats.any { cat -> cat.rarity == CatRarity.LEGENDARY } },
    Pawchievement("generator_1", "⚙️", "Automation Begins", "Buy your first Purr Generator.", 100.0) { it.totalGeneratorsBought >= 1 },
    Pawchievement("generator_100", "🏭", "Purr Industry", "Buy 100 Purr Generators.", 25_000.0) { it.totalGeneratorsBought >= 100 },
    Pawchievement("generator_1000", "🏙️", "Industrial Cat Empire", "Buy 1,000 Purr Generators.", 500_000.0) { it.totalGeneratorsBought >= 1_000 },
    Pawchievement("golden_1", "✨", "Golden Opportunity", "Click your first Golden Cat.", 777.0) { it.goldenCatsClicked >= 1 },
    Pawchievement("golden_7", "🌟", "Lucky Seven", "Click 7 Golden Cats.", 7_777.0) { it.goldenCatsClicked >= 7 },
    Pawchievement("golden_77", "🌠", "Golden Obsession", "Click 77 Golden Cats.", 77_777.0) { it.goldenCatsClicked >= 77 },
    Pawchievement("prestige_1", "♻️", "Another Life", "Start your first new life.", 10_000.0) { it.lives >= 1 },
    Pawchievement("prestige_9", "9️⃣", "Nine Lives", "Reach 9 lives.", 1_000_000.0) { it.lives >= 9 },
    Pawchievement("pps_1000", "📈", "Purr Machine", "Reach 1,000 Purrs per second.", 10_000.0) { it.highestPurrsPerSecond >= 1_000.0 },
    Pawchievement("pps_million", "🚀", "Purr Hyperdrive", "Reach 1 million Purrs per second.", 1_000_000.0) { it.highestPurrsPerSecond >= 1e6 },
    Pawchievement("play_hour", "⏱️", "Cat Hour", "Play for one hour.", 10_000.0) { it.playTimeSeconds >= 3_600L },
    Pawchievement("play_day", "🕰️", "Dedicated Cat", "Play for 24 hours.", 1_000_000.0) { it.playTimeSeconds >= 86_400L },
)
