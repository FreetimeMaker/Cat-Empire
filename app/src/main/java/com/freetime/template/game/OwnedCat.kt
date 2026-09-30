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
) {
    val bondLevel: Int
        get() = (bondXp / 25L).toInt().coerceIn(0, 100) + 1

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
        base * personality.multiplier * rarity.multiplier * bondMultiplier
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
