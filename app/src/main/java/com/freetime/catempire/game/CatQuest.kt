package com.freetime.catempire.game

enum class QuestMetric {
    PETS,
    CATS,
    PURRS,
    ROOMS,
    JOBS,
    HAPPY_CATS,
}

data class CatQuest(
    val id: String,
    val icon: String,
    val name: String,
    val description: String,
    val metric: QuestMetric,
    val target: Long,
    val reward: Double,
) {
    fun progress(state: GameState): Long = when (metric) {
        QuestMetric.PETS -> state.totalPets
        QuestMetric.CATS -> state.totalCats.toLong()
        QuestMetric.PURRS -> state.totalPurrsEarned.toLong()
        QuestMetric.ROOMS -> state.ownedCats.count { it.assignedRoomId != null }.toLong()
        QuestMetric.JOBS -> state.ownedCats.count { it.jobId != null }.toLong()
        QuestMetric.HAPPY_CATS -> state.ownedCats.count { it.happiness >= 90 }.toLong()
    }.coerceAtMost(target)

    fun completed(state: GameState): Boolean = progress(state) >= target
}

val catQuests = listOf(
    CatQuest("warm_paws", "🐾", "Warm Paws", "Pet your cats 100 times.", QuestMetric.PETS, 100, 750.0),
    CatQuest("growing_family", "🐱", "Growing Family", "Have 5 cats in your empire.", QuestMetric.CATS, 5, 2_500.0),
    CatQuest("purr_factory", "♡", "Purr Factory", "Earn 25,000 Purrs in total.", QuestMetric.PURRS, 25_000, 5_000.0),
    CatQuest("roommates", "🏠", "Roommates", "Assign 3 cats to rooms.", QuestMetric.ROOMS, 3, 4_000.0),
    CatQuest("working_cats", "💼", "Working Cats", "Give 3 cats a job.", QuestMetric.JOBS, 3, 6_000.0),
    CatQuest("happy_home", "😺", "Happy Home", "Have 3 cats at 90+ happiness.", QuestMetric.HAPPY_CATS, 3, 7_500.0),
    CatQuest("cat_crowd", "🐈", "Cat Crowd", "Have 10 cats in your empire.", QuestMetric.CATS, 10, 15_000.0),
    CatQuest("purr_mogul", "👑", "Purr Mogul", "Earn 1,000,000 Purrs in total.", QuestMetric.PURRS, 1_000_000, 100_000.0),
)
