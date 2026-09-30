package com.freetime.template.game

enum class CatPersonality(val label: String, val multiplier: Double) {
    LAZY("Lazy", 1.05),
    PLAYFUL("Playful", 1.10),
    CURIOUS("Curious", 1.12),
    AFFECTIONATE("Affectionate", 1.15),
    SHY("Shy", 1.08),
    CHAOTIC("Chaotic", 1.20),
    HUNGRY("Hungry", 1.10),
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

    fun production(base: Double) =
        base * personality.multiplier * rarity.multiplier * bondMultiplier * activity.multiplier * collarById(collarId).productionMultiplier
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
