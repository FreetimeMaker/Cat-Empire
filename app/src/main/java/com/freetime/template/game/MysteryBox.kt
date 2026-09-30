package com.freetime.template.game

enum class MysteryRewardType {
    PURRS,
    CATNIP,
    TOY_UPGRADE,
    ROOM_UPGRADE,
    COLLAR,
    RARE_CAT,
}

data class MysteryReward(
    val type: MysteryRewardType,
    val title: String,
    val description: String,
)

fun mysteryRewardFor(seed: Long): MysteryReward {
    val roll = (seed * 41L % 100L).toInt()
    return when {
        roll < 35 -> MysteryReward(MysteryRewardType.PURRS, "Purr Jackpot", "The box was full of Purrs.")
        roll < 55 -> MysteryReward(MysteryRewardType.CATNIP, "Catnip", "A hidden catnip stash gives a temporary boost.")
        roll < 70 -> MysteryReward(MysteryRewardType.TOY_UPGRADE, "Free Toy Upgrade", "One of your toys improves for free.")
        roll < 82 -> MysteryReward(MysteryRewardType.ROOM_UPGRADE, "Room Surprise", "One unlocked room improves for free.")
        roll < 92 -> MysteryReward(MysteryRewardType.COLLAR, "Special Collar", "Your selected cat found a new collar.")
        else -> MysteryReward(MysteryRewardType.RARE_CAT, "Rare Visitor", "A special cat was hiding inside the box.")
    }
}
