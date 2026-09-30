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
            editor.putString("ownedCat_" + index, listOf(cat.id, cat.typeId, cat.name, cat.personality.name, cat.rarity.name, cat.bondXp, cat.activity.name, cat.collarId, cat.happiness, cat.energy, cat.satiety, cat.assignedRoomId.orEmpty()).joinToString("|"))
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
        val restoredTypes = starterCats().map { it.copy(owned = prefs.getInt("cat_" + it.id, 0)) }
        val storedCats = loadOwnedCats()
        val migratedCats = if (storedCats.isEmpty()) migrateLegacyCats(restoredTypes) else storedCats
        val state = GameState(
            purrs = prefs.getString("purrs", "0")?.toDoubleOrNull() ?: 0.0,
            totalPets = prefs.getLong("totalPets", 0L),
            totalPurrsEarned = prefs.getString("totalPurrs", "0")?.toDoubleOrNull() ?: 0.0,
            cats = restoredTypes,
            ownedCats = migratedCats,
            selectedCatId = prefs.getLong("selectedCatId", -1L).takeIf { it >= 0L },
            discoveredCatTypes = prefs.getString("dex_t", "").orEmpty().split(";").filter(String::isNotBlank).toSet(),
            discoveredPersonalities = parsePersonalities(prefs.getString("dex_p", "").orEmpty()),
            discoveredRarities = parseRarities(prefs.getString("dex_r", "").orEmpty()),
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

    private fun migrateLegacyCats(types: List<CatType>): List<OwnedCat> {
        var id = 1L
        return buildList {
            types.forEach { type ->
                repeat(type.owned) {
                    add(createAdoptedCat(type.id, id))
                    id++
                }
            }
        }
    }

    private fun parsePersonalities(value: String) = value.split(";").mapNotNull { name ->
        CatPersonality.entries.firstOrNull { it.name == name }
    }.toSet()

    private fun parseRarities(value: String) = value.split(";").mapNotNull { name ->
        CatRarity.entries.firstOrNull { it.name == name }
    }.toSet()

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
                    happiness = parts.getOrNull(8)?.toIntOrNull() ?: 80,
                    energy = parts.getOrNull(9)?.toIntOrNull() ?: 80,
                    satiety = parts.getOrNull(10)?.toIntOrNull() ?: 80,
                    assignedRoomId = parts.getOrNull(11)?.takeIf(String::isNotBlank),
                )
            }.getOrNull()
        }
    }
}

data class SavedGame(val state: GameState, val savedAt: Long)
