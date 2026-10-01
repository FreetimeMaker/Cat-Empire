package com.freetime.catempire.game

data class GeneratorSynergy(
    val id: String,
    val name: String,
    val description: String,
    val generatorA: String,
    val generatorB: String,
    val requiredA: Int,
    val requiredB: Int,
    val cost: Double,
    val bonusPerPartner: Double,
)

val generatorSynergies = listOf(
    GeneratorSynergy("beds_trees", "Nap & Climb", "Cat Beds and Cat Trees boost each other.", "cat_bed", "cat_tree_generator", 25, 10, 50_000.0, 0.01),
    GeneratorSynergy("trees_windows", "Climb & Watch", "Cat Trees and Window Watches boost each other.", "cat_tree_generator", "window_watch", 25, 10, 500_000.0, 0.01),
    GeneratorSynergy("windows_rooms", "Perfect View", "Window Watches and Cat Rooms boost each other.", "window_watch", "cat_room_generator", 25, 10, 5_000_000.0, 0.01),
    GeneratorSynergy("rooms_houses", "Room to Grow", "Cat Rooms and Cat Houses boost each other.", "cat_room_generator", "cat_house", 25, 10, 50_000_000.0, 0.01),
    GeneratorSynergy("houses_cafes", "Neighborhood Cats", "Cat Houses and Cat Cafés boost each other.", "cat_house", "cat_cafe_generator", 25, 10, 500_000_000.0, 0.01),
    GeneratorSynergy("cafes_sanctuaries", "Coffee & Care", "Cat Cafés and Sanctuaries boost each other.", "cat_cafe_generator", "cat_sanctuary_generator", 25, 10, 5_000_000_000.0, 0.01),
    GeneratorSynergy("sanctuaries_factories", "Care Automation", "Sanctuaries and Purr Factories boost each other.", "cat_sanctuary_generator", "purr_factory", 25, 10, 50_000_000_000.0, 0.01),
    GeneratorSynergy("factories_cities", "Industrial Purring", "Purr Factories and Cat Cities boost each other.", "purr_factory", "cat_city", 25, 10, 500_000_000_000.0, 0.01),
    GeneratorSynergy("cities_planets", "Interplanetary Logistics", "Cat Cities and Cat Planets boost each other.", "cat_city", "cat_planet", 25, 10, 5_000_000_000_000.0, 0.01),
)

fun GameState.generatorSynergyMultiplier(generatorId: String): Double =
    generatorSynergies
        .filter { it.id in purchasedGeneratorSynergies }
        .fold(1.0) { total, synergy ->
            val partnerId = when (generatorId) {
                synergy.generatorA -> synergy.generatorB
                synergy.generatorB -> synergy.generatorA
                else -> return@fold total
            }
            val partnerOwned = generators.firstOrNull { it.id == partnerId }?.owned ?: 0
            total * (1.0 + partnerOwned * synergy.bonusPerPartner)
        }
