package com.freetime.catempire.game

import java.time.LocalDate

enum class DailyQuestMetric {
    PETS,
    FEEDS,
    PLAYS,
    PURRS,
}

data class DailyQuest(
    val id: String,
    val icon: String,
    val name: String,
    val description: String,
    val metric: DailyQuestMetric,
    val target: Long,
    val reward: Double,
)

val dailyQuests = listOf(
    DailyQuest("daily_pets", "🐾", "Morning Cuddles", "Pet cats 25 times today.", DailyQuestMetric.PETS, 25, 500.0),
    DailyQuest("daily_feeds", "🍽️", "Snack Time", "Feed cats 3 times today.", DailyQuestMetric.FEEDS, 3, 750.0),
    DailyQuest("daily_plays", "🧶", "Play Session", "Play with cats 3 times today.", DailyQuestMetric.PLAYS, 3, 750.0),
    DailyQuest("daily_purrs", "♡", "Purr Shift", "Earn 5,000 Purrs today.", DailyQuestMetric.PURRS, 5_000, 1_500.0),
)

fun currentDayKey(): String = LocalDate.now().toString()
