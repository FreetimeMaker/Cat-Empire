package com.freetime.template.game

data class GameState(
    val purrs: Double = 0.0,
    val totalPets: Long = 0,
    val totalPurrsEarned: Double = 0.0,
    val cats: List<CatType> = starterCats(),
    val ownedCats: List<OwnedCat> = emptyList(),
    val selectedCatId: Long? = null,
    val upgrades: List<CatUpgrade> = starterUpgrades(),
    val homeLevel: Int = 0,
    val lives: Int = 0,
    val claimedPawchievements: Set<String> = emptySet(),
    val activeEvent: CatEvent? = null,
    val eventMultiplier: Double = 1.0,
    val eventSeconds: Int = 0,
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

    val home: CatHome
        get() = catHomes[homeLevel.coerceIn(0, catHomes.lastIndex)]

    val prestigeMultiplier: Double
        get() = 1.0 + lives * 0.25

    val purrsPerSecond: Double
        get() = basePurrsPerSecond * catProductionMultiplier * eventMultiplier * home.productionMultiplier * prestigeMultiplier

    val totalCats: Int
        get() = if (ownedCats.isEmpty()) cats.sumOf(CatType::owned) else ownedCats.size
}
