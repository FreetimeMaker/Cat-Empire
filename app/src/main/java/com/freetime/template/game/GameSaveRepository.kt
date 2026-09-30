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
            .putLong("selectedCatId", state.selectedCatId ?: -1L)
            .putInt("homeLevel", state.homeLevel)
            .putInt("lives", state.lives)
            .putInt("daily_s", state.dailyStreak)
            .putLong("daily_t", state.lastDailyClaimAt)

        state.cats.forEach { editor.putInt("cat_" + it.id, it.owned) }
        editor.putLong("ownedCatCount", state.ownedCats.size.toLong())
        state.ownedCats.forEachIndexed { index, cat ->
            editor.putString("ownedCat_" + index, listOf(cat.id, cat.typeId, cat.name, cat.personality.name, cat.rarity.name, cat.bondXp, cat.activity.name, cat.collarId).joinToString("|"))
        }
        state.upgrades.forEach { editor.putInt("upgrade_" + it.id, it.level) }
        state.rooms.forEach { editor.putInt("rm_" + it.id, it.level) }
        editor.putString("dex_t", state.discoveredCatTypes.joinToString(";"))
        editor.putString("dex_p", state.discoveredPersonalities.joinToString(";") { it.name })
        editor.putString("dex_r", state.discoveredRarities.joinToString(";") { it.name })
        editor.putString("achievements", state.claimedPawchievements.joinToString(";"))
        editor.apply()
    }

    fun load(): SavedGame? {
        if (!prefs.contains("savedAt")) return null
        val state = GameState(
            purrs = prefs.getString("purrs", "0")?.toDoubleOrNull() ?: 0.0,
            totalPets = prefs.getLong("totalPets", 0L),
            totalPurrsEarned = prefs.getString("totalPurrs", "0")?.toDoubleOrNull() ?: 0.0,
            cats = starterCats().map { it.copy(owned = prefs.getInt("cat_" + it.id, 0)) },
            ownedCats = loadOwnedCats(),
            selectedCatId = prefs.getLong("selectedCatId", -1L).takeIf { it >= 0L },
            discoveredCatTypes = prefs.getString("dex_t", "").orEmpty().split(";").filter(String::isNotBlank).toSet(),
            homeLevel = prefs.getInt("homeLevel", 0),
            lives = prefs.getInt("lives", 0),
            dailyStreak = prefs.getInt("daily_s", 0),
            lastDailyClaimAt = prefs.getLong("daily_t", 0L),
            upgrades = starterUpgrades().map { it.copy(level = prefs.getInt("upgrade_" + it.id, 0)) },
            rooms = starterRooms().map { room -> room.copy(level = prefs.getInt("rm_" + room.id, 0)) },
            claimedPawchievements = prefs.getString("achievements", "").orEmpty().split(";").filter { it.isNotBlank() }.toSet(),
        )
        return SavedGame(state, prefs.getLong("savedAt", System.currentTimeMillis()))
    }

    private fun loadOwnedCats(): List<OwnedCat> {
        val count = prefs.getLong("ownedCatCount", 0L).toInt()
        return (0 until count).mapNotNull { index ->
            val parts = prefs.getString("ownedCat_" + index, null)?.split("|") ?: return@mapNotNull null
            if (parts.size < 5) return@mapNotNull null
            runCatching {
                OwnedCat(
                    id = parts[0].toLong(),
                    typeId = parts[1],
                    name = parts[2],
                    personality = CatPersonality.valueOf(parts[3]),
                    rarity = CatRarity.valueOf(parts[4]),
                    bondXp = parts.getOrNull(5)?.toLongOrNull() ?: 0L,
                    activity = parts.getOrNull(6)?.let { CatActivity.valueOf(it) } ?: CatActivity.SLEEPING,
                    collarId = parts.getOrNull(7) ?: "none",
                )
            }.getOrNull()
        }
    }
}

data class SavedGame(val state: GameState, val savedAt: Long)
