package com.freetime.catempire.game

data class GameState(
    val purrs: Double = 0.0,
    val totalPets: Long = 0,
    val totalPurrsEarned: Double = 0.0,
    val cats: List<CatType> = starterCats(),
    val generators: List<PurrGenerator> = starterGenerators(),
    val purchasedGeneratorUpgrades: Set<String> = emptySet(),
    val ownedCats: List<OwnedCat> = emptyList(),
    val selectedCatId: Long? = null,
    val discoveredCatTypes: Set<String> = emptySet(),
    val discoveredPersonalities: Set<CatPersonality> = emptySet(),
    val discoveredRarities: Set<CatRarity> = emptySet(),
    val upgrades: List<CatUpgrade> = starterUpgrades(),
    val rooms: List<CatRoom> = starterRooms(),
    val homeLevel: Int = 0,
    val lives: Int = 0,
    val lifePoints: Int = 0,
    val totalLifePoints: Int = 0,
    val lifeUpgradeLevels: Map<String, Int> = emptyMap(),
    val claimedPawchievements: Set<String> = emptySet(),
    val claimedQuests: Set<String> = emptySet(),
    val dailyQuestDay: String = currentDayKey(),
    val dailyPets: Long = 0L,
    val dailyFeeds: Long = 0L,
    val dailyPlays: Long = 0L,
    val dailyPurrs: Double = 0.0,
    val claimedDailyQuests: Set<String> = emptySet(),
    val dailyQuestBonusClaimed: Boolean = false,
    val dailyStreak: Int = 0,
    val lastDailyClaimAt: Long = 0L,
    val activeEvent: CatEvent? = null,
    val eventMultiplier: Double = 1.0,
    val eventSeconds: Int = 0,
    val lastMysteryReward: String? = null,
    val lastEventResult: String? = null,
    val strayEncounter: StrayEncounter? = null,
    val goldenCat: GoldenCat? = null,
    val goldenCatBuff: GoldenCatBuff? = null,
    val goldenCatsClicked: Long = 0L,
    val lastCatNapPurrs: Double = 0.0,
    val lastCatNapSeconds: Long = 0L,
) {
    val purrsPerPet: Double
        get() = (1.0 + upgrades.filter { it.effect == UpgradeEffect.PET_POWER }.sumOf(CatUpgrade::totalBonus)) *
            rooms.filter { it.effect == RoomEffect.PET_POWER }.fold(1.0) { total, room -> total * room.multiplier } *
            (goldenCatBuff?.petMultiplier ?: 1.0) * petPrestigeMultiplier

    val catProductionMultiplier: Double
        get() = (1.0 + upgrades.filter { it.effect == UpgradeEffect.CAT_PRODUCTION }.sumOf(CatUpgrade::totalBonus)) *
            rooms.filter { it.effect == RoomEffect.CAT_PRODUCTION }.fold(1.0) { total, room -> total * room.multiplier }

    val basePurrsPerSecond: Double
        get() = if (ownedCats.isEmpty()) {
            cats.sumOf(CatType::production)
        } else {
            ownedCats.sumOf { cat ->
                val type = cats.firstOrNull { it.id == cat.typeId }
                val room = rooms.firstOrNull { it.id == cat.assignedRoomId && homeLevel >= it.requiredHomeLevel }
                val assignmentBonus = when {
                    room == null -> 1.0
                    cat.personality in room.preferredPersonalities -> 1.35
                    else -> 1.10
                }
                val job = jobById(cat.jobId)?.takeIf { it.roomId == room?.id }
                val jobBonus = when {
                    job == null -> 1.0
                    cat.personality in job.preferredPersonalities -> job.productionMultiplier + 0.10
                    else -> job.productionMultiplier
                }
                cat.production(type?.basePurrsPerSecond ?: 0.0) * assignmentBonus * jobBonus
            }
        }

    val home: CatHome
        get() = catHomes[homeLevel.coerceIn(0, catHomes.lastIndex)]

    val prestigeMultiplier: Double
        get() = 1.0 + (lifeUpgradeLevels["eternal_purr"] ?: 0) * 0.10

    val prestigePointsAvailable: Int
        get() = (prestigePointsFor(totalPurrsEarned) - totalLifePoints).coerceAtLeast(0)

    val petPrestigeMultiplier: Double
        get() = 1.0 + (lifeUpgradeLevels["eternal_touch"] ?: 0) * 0.25

    val generatorPurrsPerSecond: Double
        get() = generators.sumOf { generator ->
            generator.production * generatorUpgradeMultiplier(generator.id, purchasedGeneratorUpgrades)
        }

    val purrsPerSecond: Double
        get() = (basePurrsPerSecond * catProductionMultiplier + generatorPurrsPerSecond) *
            eventMultiplier * (goldenCatBuff?.productionMultiplier ?: 1.0) * home.productionMultiplier * prestigeMultiplier

    val totalCats: Int
        get() = if (ownedCats.isEmpty()) cats.sumOf(CatType::owned) else ownedCats.size
}
