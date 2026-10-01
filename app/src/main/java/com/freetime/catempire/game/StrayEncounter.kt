package com.freetime.catempire.game

data class StrayEncounter(
    val cat: OwnedCat,
    val typeId: String,
    val secondsLeft: Int,
    val adoptionCost: Double,
) {
    val rarityLabel: String get() = cat.rarity.label
}

fun createStrayEncounter(state: GameState): StrayEncounter {
    val type = state.cats.random()
    val id = (state.ownedCats.maxOfOrNull(OwnedCat::id) ?: 0L) + 1L
    val base = createAdoptedCat(type.id, id)
    val rarity = when ((0..99).random()) {
        in 0..1 -> CatRarity.LEGENDARY
        in 2..9 -> CatRarity.EPIC
        in 10..34 -> CatRarity.RARE
        else -> CatRarity.COMMON
    }
    val stray = base.copy(rarity = rarity)
    val rarityCost = when (rarity) {
        CatRarity.COMMON -> 1.0
        CatRarity.RARE -> 2.0
        CatRarity.EPIC -> 5.0
        CatRarity.LEGENDARY -> 12.0
    }
    val cost = (state.purrsPerSecond * 90.0 * rarityCost).coerceAtLeast(100.0 * rarityCost)
    val duration = when (rarity) {
        CatRarity.COMMON -> 90
        CatRarity.RARE -> 75
        CatRarity.EPIC -> 60
        CatRarity.LEGENDARY -> 45
    }
    return StrayEncounter(stray, type.id, duration, cost)
}
