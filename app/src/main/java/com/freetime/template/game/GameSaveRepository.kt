package com.freetime.template.game

import android.content.Context

class GameSaveRepository(context: Context) {
    private val prefs = context.getSharedPreferences("cat_empire", Context.MODE_PRIVATE)

    fun save(state: GameState) {
        val editor = prefs.edit()
            .putString("purrs", state.purrs.toString())
            .putLong("totalPets", state.totalPets)
            .putString("totalPurrs", state.totalPurrsEarned.toString())
            .putLong("savedAt", System.currentTimeMillis())

        state.cats.forEach { editor.putInt("cat_" + it.id, it.owned) }
        state.upgrades.forEach { editor.putInt("upgrade_" + it.id, it.level) }
        editor.apply()
    }

    fun load(): SavedGame? {
        if (!prefs.contains("savedAt")) return null
        val state = GameState(
            purrs = prefs.getString("purrs", "0")?.toDoubleOrNull() ?: 0.0,
            totalPets = prefs.getLong("totalPets", 0L),
            totalPurrsEarned = prefs.getString("totalPurrs", "0")?.toDoubleOrNull() ?: 0.0,
            cats = starterCats().map { it.copy(owned = prefs.getInt("cat_" + it.id, 0)) },
            upgrades = starterUpgrades().map { it.copy(level = prefs.getInt("upgrade_" + it.id, 0)) },
        )
        return SavedGame(state, prefs.getLong("savedAt", System.currentTimeMillis()))
    }
}

data class SavedGame(val state: GameState, val savedAt: Long)
