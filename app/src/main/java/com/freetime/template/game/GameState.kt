package com.freetime.template.game

data class GameState(
    val purrs: Double = 0.0,
    val totalPets: Long = 0,
    val totalPurrsEarned: Double = 0.0,
    val cats: List<CatType> = starterCats(),
    val ownedCats: List<OwnedCat> = emptyList(),
    val upgrades: List<CatUpgrade> = starterUpgrades(),
    val lastCatNapPurrs: Double = 0.0,
    val lastCatNapSeconds: Long = 0L,
) {
    val purrsPerPet: Double
        get() = 1.0 + upgrades.filter { it.effect == UpgradeEffect.PET_POWER }.sumOf(CatUpgrade::totalBonus)

    val catProductionMultiplier: Double
        get() = 1.0 + upgrades.filter { it.effect == UpgradeEffect.CAT_PRODUCTION }.sumOf(CatUpgrade::totalBonus)

    val basePurrsPerSecond: Double
        get() = if (ownedCats.isEmpty()) {
            cats.sumOf(CatType::production)
        } else {
            ownedCats.sumOf { cat ->
                val type = cats.firstOrNull { it.id == cat.typeId }
                cat.production(type?.basePurrsPerSecond ?: 0.0)
            }
        }

    val purrsPerSecond: Double
        get() = basePurrsPerSecond * catProductionMultiplier

    val totalCats: Int
        get() = if (ownedCats.isEmpty()) cats.sumOf(CatType::owned) else ownedCats.size
}
