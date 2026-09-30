package com.freetime.template.game

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
        startAutoSave()
    }

    private fun restoreGame(): GameState {
        val saved = saves.load() ?: return GameState()
        val now = System.currentTimeMillis()
        val secondsAway = ((now - saved.savedAt) / 1000L).coerceAtLeast(0L)
        val cappedSeconds = secondsAway.coerceAtMost(MAX_OFFLINE_SECONDS)
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
            selectedCatId = selectedId,
            ownedCats = current.ownedCats.map { cat ->
                if (cat.id == selectedId) cat.copy(bondXp = cat.bondXp + 1L) else cat
            },
        )
    }

    fun selectCat(catId: Long) {
        if (_state.value.ownedCats.none { it.id == catId }) return
        update { it.copy(selectedCatId = catId) }
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

    private fun update(block: (GameState) -> GameState) {
        _state.value = block(_state.value)
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
                    )
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
                        cat.copy(activity = list[((cat.id + cycle) % list.size).toInt()])
                    })
                }
            }
        }
    }

    fun resolveEvent() {
        val event = _state.value.activeEvent ?: return
        update { current ->
            when (event.type) {
                CatEventType.MYSTERY_BOX -> {
                    val reward = (current.purrsPerSecond * 60.0).coerceAtLeast(100.0)
                    current.copy(purrs = current.purrs + reward, totalPurrsEarned = current.totalPurrsEarned + reward, activeEvent = null)
                }
                CatEventType.CATNIP -> current.copy(activeEvent = null, eventMultiplier = 2.0, eventSeconds = 60)
                CatEventType.ZOOMIES -> current.copy(activeEvent = null, eventMultiplier = 3.0, eventSeconds = 15)
                CatEventType.TOILET_PAPER -> current.copy(activeEvent = null)
                CatEventType.STRAY -> {
                    val type = current.cats.first()
                    val id = (current.ownedCats.maxOfOrNull(OwnedCat::id) ?: 0L) + 1L
                    current.copy(
                        activeEvent = null,
                        cats = current.cats.map { if (it.id == type.id) it.copy(owned = it.owned + 1) else it },
                        ownedCats = current.ownedCats + createAdoptedCat(type.id, id),
                        selectedCatId = current.selectedCatId ?: id,
                    )
                }
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
                        current.copy(activeEvent = catEvents[(cycle / 45) % catEvents.size])
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
        const val MAX_OFFLINE_SECONDS = 8L * 60L * 60L
    }
}
