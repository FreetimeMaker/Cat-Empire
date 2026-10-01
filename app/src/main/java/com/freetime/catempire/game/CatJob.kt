package com.freetime.catempire.game

data class CatJob(
    val id: String,
    val roomId: String,
    val name: String,
    val icon: String,
    val description: String,
    val productionMultiplier: Double,
    val preferredPersonalities: Set<CatPersonality>,
)

val catJobs = listOf(
    CatJob("cuddle_host", "living", "Cuddle Host", "❤️", "Keeps the living room cozy and welcoming.", 1.12, setOf(CatPersonality.AFFECTIONATE, CatPersonality.SHY)),
    CatJob("toy_tester", "living", "Toy Tester", "🧶", "Tests every toy before the humans see it.", 1.10, setOf(CatPersonality.PLAYFUL, CatPersonality.CURIOUS)),
    CatJob("professional_napper", "bedroom", "Professional Napper", "💤", "Turns sleeping into serious empire business.", 1.15, setOf(CatPersonality.LAZY, CatPersonality.SHY)),
    CatJob("blanket_inspector", "bedroom", "Blanket Inspector", "🛏️", "Checks every blanket for maximum softness.", 1.10, setOf(CatPersonality.AFFECTIONATE, CatPersonality.LAZY)),
    CatJob("taste_tester", "kitchen", "Taste Tester", "🍗", "Makes sure every snack meets cat standards.", 1.15, setOf(CatPersonality.HUNGRY, CatPersonality.CURIOUS)),
    CatJob("snack_guard", "kitchen", "Snack Guard", "🍽️", "Guards the food with suspicious dedication.", 1.10, setOf(CatPersonality.HUNGRY, CatPersonality.CHAOTIC)),
    CatJob("bird_watcher", "balcony", "Bird Watcher", "🐦", "Studies local birds from a safe distance.", 1.14, setOf(CatPersonality.CURIOUS, CatPersonality.SHY)),
    CatJob("sunbeam_hunter", "balcony", "Sunbeam Hunter", "☀️", "Finds the best warm spot in the empire.", 1.10, setOf(CatPersonality.LAZY, CatPersonality.CURIOUS)),
    CatJob("garden_explorer", "garden", "Garden Explorer", "🌿", "Patrols every leaf, bug and mysterious noise.", 1.15, setOf(CatPersonality.CURIOUS, CatPersonality.PLAYFUL)),
    CatJob("zoomies_coach", "garden", "Zoomies Coach", "💨", "Turns garden laps into elite training.", 1.13, setOf(CatPersonality.PLAYFUL, CatPersonality.CHAOTIC)),
    CatJob("box_engineer", "cat_room", "Box Engineer", "📦", "Designs increasingly unnecessary cardboard forts.", 1.16, setOf(CatPersonality.CHAOTIC, CatPersonality.CURIOUS)),
    CatJob("chief_play_officer", "cat_room", "Chief Play Officer", "🐾", "Makes sure nobody takes the empire too seriously.", 1.14, setOf(CatPersonality.PLAYFUL, CatPersonality.CHAOTIC)),
)

fun jobsForRoom(roomId: String?): List<CatJob> =
    if (roomId == null) emptyList() else catJobs.filter { it.roomId == roomId }

fun jobById(id: String?): CatJob? = catJobs.firstOrNull { it.id == id }
