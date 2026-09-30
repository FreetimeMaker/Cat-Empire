package com.freetime.catempire.game

enum class CatPersonality(
    val label: String,
    val multiplier: Double,
    val hungerDrain: Int,
    val energyDrain: Int,
    val petHappiness: Int,
    val playHappiness: Int,
    val restRecovery: Int,
) {
    LAZY("Lazy", 1.05, 1, 0, 2, 12, 45),
    PLAYFUL("Playful", 1.10, 1, 2, 2, 30, 25),
    CURIOUS("Curious", 1.12, 1, 2, 2, 22, 25),
    AFFECTIONATE("Affectionate", 1.15, 1, 1, 6, 20, 30),
    SHY("Shy", 1.08, 1, 1, 1, 16, 35),
    CHAOTIC("Chaotic", 1.20, 2, 3, 2, 25, 20),
    HUNGRY("Hungry", 1.10, 3, 1, 2, 18, 30),
}

enum class CatActivity(val label: String, val emoji: String, val multiplier: Double) {
    SLEEPING("Sleeping", "💤", 0.75),
    EATING("Eating", "🍗", 0.90),
    PLAYING("Playing", "🧶", 1.25),
    WATCHING_BIRDS("Watching birds", "🐦", 1.10),
    SITTING_IN_BOX("Sitting in a box", "📦", 1.20),
    EXPLORING("Exploring", "🐾", 1.15),
    ASKING_FOR_PETS("Asking for pets", "❤️", 1.10),
    ZOOMIES("Zoomies", "💨", 3.00),
    CHAOS("Causing chaos", "😼", 1.75),
}

enum class CatRarity(val label: String, val multiplier: Double) {
    COMMON("Common", 1.0),
    RARE("Rare", 1.25),
    EPIC("Epic", 1.6),
    LEGENDARY("Legendary", 2.25),
}

data class OwnedCat(
    val id: Long,
    val typeId: String,
    val name: String,
    val personality: CatPersonality,
    val rarity: CatRarity,
    val bondXp: Long = 0L,
    val activity: CatActivity = CatActivity.SLEEPING,
    val collarId: String = "none",
    val happiness: Int = 80,
    val energy: Int = 80,
    val satiety: Int = 80,
    val assignedRoomId: String? = null,
) {
    val bondLevel: Int
        get() = ((bondXp / 25L).toInt() + 1).coerceIn(1, 100)

    val bondProgress: Int
        get() = (bondXp % 25L).toInt()

    val bondMultiplier: Double
        get() = when {
            bondLevel >= 100 -> 2.0
            bondLevel >= 50 -> 1.5
            bondLevel >= 20 -> 1.25
            bondLevel >= 10 -> 1.15
            bondLevel >= 5 -> 1.10
            else -> 1.0
        }

    val needsMultiplier: Double
        get() = (0.4 + (happiness + energy + satiety) / 500.0).coerceIn(0.4, 1.0)

    fun production(base: Double) =
        base * personality.multiplier * rarity.multiplier * bondMultiplier * activity.multiplier *
            collarById(collarId).productionMultiplier * needsMultiplier
}

private val names = listOf(
    "Milo", "Luna", "Mochi", "Nala", "Simba", "Cleo", "Miso", "Oliver",
    "Willow", "Leo", "Pepper", "Chai", "Maple", "Bean", "Nova", "Pixel",
)

fun createAdoptedCat(typeId: String, sequence: Long): OwnedCat {
    val personality = CatPersonality.entries[(sequence % CatPersonality.entries.size).toInt()]
    val roll = (sequence * 37 % 100).toInt()
    val rarity = when {
        roll >= 97 -> CatRarity.LEGENDARY
        roll >= 87 -> CatRarity.EPIC
        roll >= 65 -> CatRarity.RARE
        else -> CatRarity.COMMON
    }
    return OwnedCat(
        id = sequence,
        typeId = typeId,
        name = names[(sequence % names.size).toInt()],
        personality = personality,
        rarity = rarity,
    )
}
