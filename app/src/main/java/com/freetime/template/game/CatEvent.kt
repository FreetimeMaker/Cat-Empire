package com.freetime.template.game

enum class CatEventType {
    MYSTERY_BOX,
    CATNIP,
    STRAY,
    TOILET_PAPER,
    ZOOMIES,
}

data class CatEvent(
    val type: CatEventType,
    val emoji: String,
    val title: String,
    val description: String,
    val action: String,
)

val catEvents = listOf(
    CatEvent(CatEventType.MYSTERY_BOX, "Box", "Mystery Box", "A mysterious cardboard box appeared.", "Open"),
    CatEvent(CatEventType.CATNIP, "Catnip", "Catnip Time", "The cats found the catnip stash.", "Use catnip"),
    CatEvent(CatEventType.STRAY, "Cat", "A Stray Appears", "A friendly stray is waiting outside.", "Adopt"),
    CatEvent(CatEventType.TOILET_PAPER, "Roll", "The Toilet Paper Incident", "There is toilet paper everywhere.", "Clean up"),
    CatEvent(CatEventType.ZOOMIES, "Zoom", "Zoomies", "Every cat suddenly wants to run.", "Go"),
)
