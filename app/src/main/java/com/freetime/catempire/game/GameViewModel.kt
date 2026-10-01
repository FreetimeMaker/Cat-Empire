package com.freetime.catempire.game

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val saves = GameSaveRepository(application)
    private val _state = mutableStateOf(restoreGame())
    val state: State<GameState> = _state

    init {
        startIdleLoop()
        startActivityLoop()
        startEventLoop()
        startStrayTimer()
        startGoldenCatLoop()
        startAutoSave()
    }

    private fun restoreGame(): GameState {
        val saved = saves.load() ?: return GameState()
        val now = System.currentTimeMillis()
        val secondsAway = ((now - saved.savedAt) / 1000L).coerceAtLeast(0L)
        val cappedSeconds = secondsAway.coerceAtMost(BASE_MAX_OFFLINE_SECONDS + (saved.state.lifeUpgradeLevels["deep_naps"] ?: 0) * 30L * 60L)
        val offlinePurrs = saved.state.purrsPerSecond * cappedSeconds
        return saved.state.copy(
            purrs = saved.state.purrs + offlinePurrs,
            totalPurrsEarned = saved.state.totalPurrsEarned + offlinePurrs,
            lastCatNapPurrs = offlinePurrs,
            lastCatNapSeconds = cappedSeconds,
        )
    }

    fun dismissCatNap() {
        _state.value = _state.value.copy(lastCatNapPurrs = 0.0, lastCatNapSeconds = 0L)
    }

    fun petCat() = update { current ->
        val selectedId = current.selectedCatId ?: current.ownedCats.firstOrNull()?.id
        current.copy(
            purrs = current.purrs + current.purrsPerPet,
            totalPets = current.totalPets + 1,
            totalPurrsEarned = current.totalPurrsEarned + current.purrsPerPet,
            dailyPets = current.dailyPets + 1,
            dailyPurrs = current.dailyPurrs + current.purrsPerPet,
            selectedCatId = selectedId,
            ownedCats = current.ownedCats.map { cat ->
                if (cat.id == selectedId) {
                    val room = current.rooms.firstOrNull { it.id == cat.assignedRoomId }
                    val bonus = if (room?.needEffect == RoomNeedEffect.AFFECTION || room?.needEffect == RoomNeedEffect.ALL_NEEDS) 3 else 0
                    cat.copy(
                        bondXp = cat.bondXp + 1L,
                        happiness = (cat.happiness + cat.personality.petHappiness + bonus).coerceAtMost(100),
                    )
                } else cat
            },
        )
    }

    fun feedCat(catId: Long) {
        val current = _state.value
        val cost = 25.0
        if (current.purrs < cost) return
        update { state ->
            state.copy(
                purrs = state.purrs - cost,
                dailyFeeds = state.dailyFeeds + 1,
                ownedCats = state.ownedCats.map { cat ->
                    if (cat.id == catId) {
                        val room = state.rooms.firstOrNull { it.id == cat.assignedRoomId }
                        val bonus = if (room?.needEffect == RoomNeedEffect.FOOD || room?.needEffect == RoomNeedEffect.ALL_NEEDS) 15 else 0
                        cat.copy(
                        satiety = (cat.satiety + 30 + bonus).coerceAtMost(100),
                        happiness = (cat.happiness + 5).coerceAtMost(100),
                        )
                    } else cat
                },
            )
        }
    }

    fun playWithCat(catId: Long) {
        update { state ->
            val target = state.ownedCats.firstOrNull { it.id == catId }
            val playCost = target?.let { 8 + it.personality.energyDrain } ?: Int.MAX_VALUE
            state.copy(
                dailyPlays = state.dailyPlays + if (target != null && target.energy >= playCost) 1 else 0,
                ownedCats = state.ownedCats.map { cat ->
                if (cat.id == catId && cat.energy >= playCost) cat.copy(
                    happiness = (cat.happiness + cat.personality.playHappiness).coerceAtMost(100),
                    energy = (cat.energy - (8 + cat.personality.energyDrain)).coerceAtLeast(0),
                ) else cat
            })
            )
        }
    }

    fun letCatRest(catId: Long) {
        update { state ->
            state.copy(ownedCats = state.ownedCats.map { cat ->
                if (cat.id == catId) cat.copy(
                    energy = (cat.energy + cat.personality.restRecovery).coerceAtMost(100),
                    activity = CatActivity.SLEEPING,
                ) else cat
            })
        }
    }

    fun assignCatToRoom(catId: Long, roomId: String?) {
        val current = _state.value
        if (current.ownedCats.none { it.id == catId }) return
        if (roomId != null) {
            val room = current.rooms.firstOrNull { it.id == roomId } ?: return
            if (current.homeLevel < room.requiredHomeLevel) return
            val occupants = current.ownedCats.count { it.assignedRoomId == roomId && it.id != catId }
            if (occupants >= room.capacity) return
        }
        update { state ->
            state.copy(ownedCats = state.ownedCats.map { cat ->
                if (cat.id == catId) cat.copy(assignedRoomId = roomId, jobId = null) else cat
            })
        }
    }

    fun assignCatJob(catId: Long, jobId: String?) {
        val current = _state.value
        val cat = current.ownedCats.firstOrNull { it.id == catId } ?: return
        if (jobId != null) {
            val job = jobById(jobId) ?: return
            if (cat.assignedRoomId != job.roomId) return
        }
        update { state ->
            state.copy(ownedCats = state.ownedCats.map { owned ->
                if (owned.id == catId) owned.copy(jobId = jobId) else owned
            })
        }
    }

    fun equipCollar(catId: Long, collarId: String) {
        val current = _state.value
        val cat = current.ownedCats.firstOrNull { it.id == catId } ?: return
        val collar = catCollars.firstOrNull { it.id == collarId } ?: return
        if (cat.bondLevel < collar.requiredBond || current.lives < collar.requiredLives) return
        update { state ->
            state.copy(ownedCats = state.ownedCats.map {
                if (it.id == catId) it.copy(collarId = collarId) else it
            })
        }
    }

    fun selectCat(catId: Long) {
        if (_state.value.ownedCats.none { it.id == catId }) return
        update { it.copy(selectedCatId = catId) }
    }

    private fun withDiscoveries(state: GameState): GameState = state.copy(
        discoveredCatTypes = state.discoveredCatTypes + state.ownedCats.map { it.typeId },
        discoveredPersonalities = state.discoveredPersonalities + state.ownedCats.map { it.personality },
        discoveredRarities = state.discoveredRarities + state.ownedCats.map { it.rarity },
    )

    fun buyGeneratorUpgrade(upgradeId: String) {
        val current = _state.value
        val upgrade = generatorUpgrades.firstOrNull { it.id == upgradeId } ?: return
        if (upgradeId in current.purchasedGeneratorUpgrades) return
        val generator = current.generators.firstOrNull { it.id == upgrade.generatorId } ?: return
        if (generator.owned < upgrade.requiredOwned || current.purrs < upgrade.cost) return
        update {
            it.copy(
                purrs = it.purrs - upgrade.cost,
                purchasedGeneratorUpgrades = it.purchasedGeneratorUpgrades + upgradeId,
            )
        }
    }

    fun buyGenerator(generatorId: String, mode: BuyAmount = BuyAmount.ONE) {
        val current = _state.value
        val generator = current.generators.firstOrNull { it.id == generatorId } ?: return
        val amount = generator.purchaseAmount(mode, current.purrs)
        if (amount <= 0) return
        val cost = generator.bulkCost(amount)
        if (current.purrs < cost) return
        update {
            it.copy(
                purrs = it.purrs - cost,
                generators = it.generators.map { item ->
                    if (item.id == generatorId) item.copy(owned = item.owned + amount) else item
                },
            )
        }
    }

    fun adoptCat(catId: String) {
        val current = _state.value
        val cat = current.cats.firstOrNull { it.id == catId } ?: return
        if (current.purrs < cat.nextCost) return
        update {
            val sequence = (it.ownedCats.maxOfOrNull(OwnedCat::id) ?: 0L) + 1L
            it.copy(
                purrs = it.purrs - cat.nextCost,
                cats = it.cats.map { item ->
                    if (item.id == catId) item.copy(owned = item.owned + 1) else item
                },
                ownedCats = it.ownedCats + createAdoptedCat(catId, sequence),
                selectedCatId = it.selectedCatId ?: sequence,
            )
        }
    }

    fun claimDailyReward() {
        val current = _state.value
        val now = System.currentTimeMillis()
        val elapsed = now - current.lastDailyClaimAt
        if (current.lastDailyClaimAt > 0L && elapsed < DAY_MS) return

        val nextStreak = if (current.lastDailyClaimAt > 0L && elapsed <= STREAK_GRACE_MS) {
            current.dailyStreak + 1
        } else {
            1
        }
        val cappedStreak = nextStreak.coerceAtMost(7)
        val reward = (current.purrsPerSecond * (120.0 + cappedStreak * 60.0)).coerceAtLeast(250.0 * cappedStreak)

        update {
            it.copy(
                purrs = it.purrs + reward,
                totalPurrsEarned = it.totalPurrsEarned + reward,
                dailyStreak = cappedStreak,
                lastDailyClaimAt = now,
            )
        }
        saves.save(_state.value)
    }

    fun claimDailyQuest(id: String) {
        val current = normalizeDaily(_state.value)
        val quest = dailyQuests.firstOrNull { it.id == id } ?: return
        val progress = when (quest.metric) {
            DailyQuestMetric.PETS -> current.dailyPets
            DailyQuestMetric.FEEDS -> current.dailyFeeds
            DailyQuestMetric.PLAYS -> current.dailyPlays
            DailyQuestMetric.PURRS -> current.dailyPurrs.toLong()
        }
        if (id in current.claimedDailyQuests || progress < quest.target) return
        update {
            it.copy(
                purrs = it.purrs + quest.reward,
                totalPurrsEarned = it.totalPurrsEarned + quest.reward,
                claimedDailyQuests = it.claimedDailyQuests + id,
            )
        }
    }

    fun claimDailyQuestBonus() {
        val current = normalizeDaily(_state.value)
        if (current.dailyQuestBonusClaimed || current.claimedDailyQuests.size < dailyQuests.size) return
        val reward = (current.purrsPerSecond * 300.0).coerceAtLeast(5_000.0)
        update {
            it.copy(
                purrs = it.purrs + reward,
                totalPurrsEarned = it.totalPurrsEarned + reward,
                dailyQuestBonusClaimed = true,
            )
        }
    }

    fun claimQuest(id: String) {
        val current = _state.value
        val quest = catQuests.firstOrNull { it.id == id } ?: return
        if (id in current.claimedQuests || !quest.completed(current)) return
        update {
            it.copy(
                purrs = it.purrs + quest.reward,
                totalPurrsEarned = it.totalPurrsEarned + quest.reward,
                claimedQuests = it.claimedQuests + id,
            )
        }
    }

    fun claimPawchievement(id: String) {
        val current = _state.value
        val achievement = pawchievements.firstOrNull { it.id == id } ?: return
        if (id in current.claimedPawchievements || !achievement.unlocked(current)) return
        update {
            it.copy(
                purrs = it.purrs + achievement.reward,
                totalPurrsEarned = it.totalPurrsEarned + achievement.reward,
                claimedPawchievements = it.claimedPawchievements + id,
            )
        }
    }

    fun buyLifeUpgrade(upgradeId: String) {
        val current = _state.value
        val upgrade = lifeUpgrades.firstOrNull { it.id == upgradeId } ?: return
        val level = current.lifeUpgradeLevels[upgradeId] ?: 0
        if (level >= upgrade.maxLevel) return
        val cost = upgrade.cost(level)
        if (current.lifePoints < cost) return
        update {
            it.copy(
                lifePoints = it.lifePoints - cost,
                lifeUpgradeLevels = it.lifeUpgradeLevels + (upgradeId to level + 1),
            )
        }
        saves.save(_state.value)
    }

    fun startNewLife() {
        val current = _state.value
        val gainedPoints = current.prestigePointsAvailable
        if (gainedPoints <= 0) return
        _state.value = GameState(
            lives = current.lives + 1,
            lifePoints = current.lifePoints + gainedPoints,
            totalLifePoints = current.totalLifePoints + gainedPoints,
            lifeUpgradeLevels = current.lifeUpgradeLevels,
            totalPets = current.totalPets,
            totalPurrsEarned = current.totalPurrsEarned,
            goldenCatsClicked = current.goldenCatsClicked,
            claimedPawchievements = current.claimedPawchievements,
            claimedQuests = current.claimedQuests,
            discoveredCatTypes = current.discoveredCatTypes + current.ownedCats.map { it.typeId },
            discoveredPersonalities = current.discoveredPersonalities + current.ownedCats.map { it.personality },
            discoveredRarities = current.discoveredRarities + current.ownedCats.map { it.rarity },
        )
        saves.save(_state.value)
    }

    fun upgradeRoom(roomId: String) {
        val current = _state.value
        val room = current.rooms.firstOrNull { it.id == roomId } ?: return
        if (current.homeLevel < room.requiredHomeLevel || current.purrs < room.nextCost) return
        update {
            it.copy(
                purrs = it.purrs - room.nextCost,
                rooms = it.rooms.map { item ->
                    if (item.id == roomId) item.copy(level = item.level + 1) else item
                },
            )
        }
    }

    fun upgradeHome() {
        val current = _state.value
        if (current.homeLevel >= catHomes.lastIndex) return
        val cost = current.home.upgradeCost
        if (current.purrs < cost) return
        update { it.copy(purrs = it.purrs - cost, homeLevel = it.homeLevel + 1) }
    }

    fun buyUpgrade(upgradeId: String) {
        val current = _state.value
        val upgrade = current.upgrades.firstOrNull { it.id == upgradeId } ?: return
        if (current.purrs < upgrade.nextCost) return
        update {
            it.copy(
                purrs = it.purrs - upgrade.nextCost,
                upgrades = it.upgrades.map { item ->
                    if (item.id == upgradeId) item.copy(level = item.level + 1) else item
                },
            )
        }
    }

    private fun normalizeDaily(state: GameState): GameState {
        val today = currentDayKey()
        return if (state.dailyQuestDay == today) state else state.copy(
            dailyQuestDay = today,
            dailyPets = 0L,
            dailyFeeds = 0L,
            dailyPlays = 0L,
            dailyPurrs = 0.0,
            claimedDailyQuests = emptySet(),
            dailyQuestBonusClaimed = false,
        )
    }

    private fun update(block: (GameState) -> GameState) {
        _state.value = withDiscoveries(block(normalizeDaily(_state.value)))
    }

    private fun startIdleLoop() {
        viewModelScope.launch {
            while (isActive) {
                delay(100L)
                val current = _state.value
                val earned = current.purrsPerSecond * 0.1
                if (earned > 0.0) {
                    _state.value = current.copy(
                        purrs = current.purrs + earned,
                        totalPurrsEarned = current.totalPurrsEarned + earned,
                        dailyPurrs = current.dailyPurrs + earned,
                    )
                }
            }
        }
    }

    private fun startStrayTimer() {
        viewModelScope.launch {
            while (isActive) {
                delay(1_000L)
                val current = _state.value
                val encounter = current.strayEncounter ?: continue
                val nextSeconds = encounter.secondsLeft - 1
                _state.value = if (nextSeconds <= 0) {
                    current.copy(
                        strayEncounter = null,
                        lastEventResult = encounter.cat.name + " wandered away before you could adopt them.",
                    )
                } else {
                    current.copy(strayEncounter = encounter.copy(secondsLeft = nextSeconds))
                }
            }
        }
    }

    private fun startActivityLoop() {
        viewModelScope.launch {
            var cycle = 0L
            while (isActive) {
                delay(15_000L)
                cycle++
                update { current ->
                    current.copy(ownedCats = current.ownedCats.map { cat ->
                        val list = CatActivity.entries
                        cat.copy(
                            activity = list[((cat.id + cycle) % list.size).toInt()],
                            satiety = (cat.satiety - cat.personality.hungerDrain).coerceAtLeast(0),
                            energy = if (cat.activity == CatActivity.SLEEPING) {
                                (cat.energy + (cat.personality.restRecovery / 10).coerceAtLeast(2)).coerceAtMost(100)
                            } else {
                                (cat.energy - cat.personality.energyDrain).coerceAtLeast(0)
                            },
                            happiness = (cat.happiness - if (cat.satiety < 25 || cat.energy < 20) 2 else 0).coerceIn(0, 100),
                        )
                    })
                }
            }
        }
    }

    fun resolveEvent() {
        val event = _state.value.activeEvent ?: return
        update { current ->
            when (event.type) {
                CatEventType.MYSTERY_BOX -> resolveMysteryBox(current)
                CatEventType.CATNIP -> current.copy(activeEvent = null, eventMultiplier = 2.0, eventSeconds = 60)
                CatEventType.ZOOMIES -> current.copy(activeEvent = null, eventMultiplier = 3.0, eventSeconds = 15)
                CatEventType.TOILET_PAPER -> {
                    val cleanup = (current.purrsPerSecond * 30.0).coerceAtLeast(50.0)
                    current.copy(
                        activeEvent = null,
                        purrs = (current.purrs - cleanup).coerceAtLeast(0.0),
                        lastEventResult = "The cats destroyed the toilet paper. Cleanup cost " + cleanup.toLong() + " Purrs.",
                    )
                }
                CatEventType.STRAY -> current.copy(
                    activeEvent = null,
                    strayEncounter = createStrayEncounter(current),
                )
            }
        }
    }

    fun adoptStray() {
        val current = _state.value
        val encounter = current.strayEncounter ?: return
        if (current.purrs < encounter.adoptionCost) return
        val type = current.cats.firstOrNull { it.id == encounter.typeId } ?: return
        update {
            it.copy(
                purrs = it.purrs - encounter.adoptionCost,
                cats = it.cats.map { catType ->
                    if (catType.id == type.id) catType.copy(owned = catType.owned + 1) else catType
                },
                ownedCats = it.ownedCats + encounter.cat,
                selectedCatId = it.selectedCatId ?: encounter.cat.id,
                strayEncounter = null,
                lastEventResult = type.name + " " + encounter.cat.name + " joined your empire as a " + encounter.cat.rarity.label + " stray.",
            )
        }
    }

    fun dismissStray() {
        update { it.copy(strayEncounter = null) }
    }

    fun dismissEventResult() {
        update { it.copy(lastEventResult = null) }
    }

    fun dismissMysteryReward() {
        update { it.copy(lastMysteryReward = null) }
    }

    private fun resolveMysteryBox(current: GameState): GameState {
        val reward = mysteryRewardFor(current.totalPets + current.totalCats + current.homeLevel + current.lives)
        val message = reward.title + ": " + reward.description
        return when (reward.type) {
            MysteryRewardType.PURRS -> {
                val amount = (current.purrsPerSecond * 180.0).coerceAtLeast(500.0)
                current.copy(
                    purrs = current.purrs + amount,
                    totalPurrsEarned = current.totalPurrsEarned + amount,
                    activeEvent = null,
                    lastMysteryReward = message + " +" + amount.toLong() + " Purrs",
                )
            }
            MysteryRewardType.CATNIP -> current.copy(activeEvent = null, eventMultiplier = 2.5, eventSeconds = 90, lastMysteryReward = message)
            MysteryRewardType.TOY_UPGRADE -> {
                val target = current.upgrades.minByOrNull { it.level }
                current.copy(
                    activeEvent = null,
                    upgrades = current.upgrades.map { if (it.id == target?.id) it.copy(level = it.level + 1) else it },
                    lastMysteryReward = message,
                )
            }
            MysteryRewardType.ROOM_UPGRADE -> {
                val target = current.rooms.filter { current.homeLevel >= it.requiredHomeLevel }.minByOrNull { it.level }
                current.copy(
                    activeEvent = null,
                    rooms = current.rooms.map { if (it.id == target?.id) it.copy(level = it.level + 1) else it },
                    lastMysteryReward = message,
                )
            }
            MysteryRewardType.COLLAR -> {
                val selected = current.selectedCatId
                val collar = catCollars.filter { it.id != "none" }.firstOrNull {
                    current.lives >= it.requiredLives && current.ownedCats.firstOrNull { cat -> cat.id == selected }?.bondLevel ?: 0 >= it.requiredBond
                }
                current.copy(
                    activeEvent = null,
                    ownedCats = current.ownedCats.map { if (it.id == selected && collar != null) it.copy(collarId = collar.id) else it },
                    lastMysteryReward = if (collar != null) message + " " + collar.name else "The box contained a collar, but none fit yet.",
                )
            }
            MysteryRewardType.RARE_CAT -> {
                val type = current.cats.last()
                val id = (current.ownedCats.maxOfOrNull(OwnedCat::id) ?: 0L) + 1L
                val cat = createAdoptedCat(type.id, id).copy(rarity = CatRarity.RARE)
                current.copy(
                    activeEvent = null,
                    cats = current.cats.map { if (it.id == type.id) it.copy(owned = it.owned + 1) else it },
                    ownedCats = current.ownedCats + cat,
                    selectedCatId = current.selectedCatId ?: id,
                    lastMysteryReward = message + " " + cat.name + " joined your empire.",
                )
            }
        }
    }

    fun clickGoldenCat() {
        val current = _state.value
        if (current.goldenCat == null) return
        val reward = GoldenCatReward.entries.random()
        update { state ->
            when (reward) {
                GoldenCatReward.FRENZY -> state.copy(
                    goldenCat = null,
                    goldenCatsClicked = state.goldenCatsClicked + 1,
                    goldenCatBuff = GoldenCatBuff("Golden Frenzy", 77, productionMultiplier = 7.0),
                    lastEventResult = "Golden Cat! Purr production x7 for 77 seconds.",
                )
                GoldenCatReward.PET_FRENZY -> state.copy(
                    goldenCat = null,
                    goldenCatsClicked = state.goldenCatsClicked + 1,
                    goldenCatBuff = GoldenCatBuff("Pet Frenzy", 13, petMultiplier = 77.0),
                    lastEventResult = "Golden Cat! Petting power x77 for 13 seconds.",
                )
                GoldenCatReward.LUCKY_PURRS -> {
                    val rewardPurrs = (state.purrsPerSecond * 900.0).coerceAtLeast(777.0)
                    state.copy(
                        goldenCat = null,
                        goldenCatsClicked = state.goldenCatsClicked + 1,
                        purrs = state.purrs + rewardPurrs,
                        totalPurrsEarned = state.totalPurrsEarned + rewardPurrs,
                        lastEventResult = "Lucky Golden Cat! +" + rewardPurrs.toLong() + " Purrs.",
                    )
                }
            }
        }
    }

    private fun startGoldenCatLoop() {
        viewModelScope.launch {
            var secondsUntilSpawn = 90
            while (isActive) {
                delay(1_000L)
                val current = normalizeDaily(_state.value)
                val visible = current.goldenCat
                val buff = current.goldenCatBuff
                val nextVisible = when {
                    visible == null -> null
                    visible.secondsLeft <= 1 -> null
                    else -> visible.copy(secondsLeft = visible.secondsLeft - 1)
                }
                val nextBuff = when {
                    buff == null -> null
                    buff.secondsLeft <= 1 -> null
                    else -> buff.copy(secondsLeft = buff.secondsLeft - 1)
                }
                if (visible == null) secondsUntilSpawn--
                val shouldSpawn = visible == null && secondsUntilSpawn <= 0 && current.totalPets > 0
                _state.value = current.copy(
                    goldenCat = if (shouldSpawn) GoldenCat() else nextVisible,
                    goldenCatBuff = nextBuff,
                )
                if (shouldSpawn) secondsUntilSpawn = (60..180).random()
            }
        }
    }

    private fun startEventLoop() {
        viewModelScope.launch {
            var cycle = 0
            while (isActive) {
                delay(1_000L)
                cycle++
                update { current ->
                    if (current.eventSeconds > 1) current.copy(eventSeconds = current.eventSeconds - 1)
                    else if (current.eventSeconds == 1) current.copy(eventSeconds = 0, eventMultiplier = 1.0)
                    else if (cycle % 45 == 0 && current.activeEvent == null && current.ownedCats.isNotEmpty()) {
                        current.copy(activeEvent = catEvents.random())
                    } else current
                }
            }
        }
    }

    private fun startAutoSave() {
        viewModelScope.launch {
            while (isActive) {
                delay(5_000L)
                saves.save(_state.value)
            }
        }
    }

    override fun onCleared() {
        saves.save(_state.value)
        super.onCleared()
    }

    private companion object {
        const val BASE_MAX_OFFLINE_SECONDS = 8L * 60L * 60L
        const val DAY_MS = 24L * 60L * 60L * 1000L
        const val STREAK_GRACE_MS = 48L * 60L * 60L * 1000L
    }
}
