package com.freetime.template.game

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _state = mutableStateOf(GameState())
    val state: State<GameState> = _state

    init { startIdleLoop() }

    fun petCat() {
        val current = _state.value
        _state.value = current.copy(
            purrs = current.purrs + current.purrsPerPet,
            totalPets = current.totalPets + 1,
            totalPurrsEarned = current.totalPurrsEarned + current.purrsPerPet,
        )
    }

    fun adoptCat(catId: String) {
        val current = _state.value
        val cat = current.cats.firstOrNull { it.id == catId } ?: return
        val cost = cat.nextCost
        if (current.purrs < cost) return

        _state.value = current.copy(
            purrs = current.purrs - cost,
            cats = current.cats.map {
                if (it.id == catId) it.copy(owned = it.owned + 1) else it
            },
        )
    }

    fun buyUpgrade(upgradeId: String) {
        val current = _state.value
        val upgrade = current.upgrades.firstOrNull { it.id == upgradeId } ?: return
        val cost = upgrade.nextCost
        if (current.purrs < cost) return

        _state.value = current.copy(
            purrs = current.purrs - cost,
            upgrades = current.upgrades.map {
                if (it.id == upgradeId) it.copy(level = it.level + 1) else it
            },
        )
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
}
