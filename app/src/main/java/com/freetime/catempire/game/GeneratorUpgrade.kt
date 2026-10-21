package com.freetime.catempire.game

data class GeneratorUpgrade(
    val id: String,
    val generatorId: String,
    val name: String,
    val description: String,
    val requiredOwned: Int,
    val cost: Double,
    val multiplier: Double,
)

val generatorUpgrades = listOf(
    GeneratorUpgrade("bed_blanket", "cat_bed", "Extra Fluffy Blankets", "Cat Beds produce twice as many Purrs.", 10, 500.0, 2.0),
    GeneratorUpgrade("bed_heated", "cat_bed", "Heated Cat Beds", "Warm naps make Cat Beds five times stronger.", 50, 25_000.0, 5.0),
    GeneratorUpgrade("tree_sisal", "cat_tree_generator", "Premium Sisal", "Cat Trees produce twice as many Purrs.", 10, 5_000.0, 2.0),
    GeneratorUpgrade("tree_ceiling", "cat_tree_generator", "Ceiling-High Trees", "Massive Cat Trees produce five times as many Purrs.", 50, 250_000.0, 5.0),
    GeneratorUpgrade("window_birds", "window_watch", "Premium Bird Channel", "Window Watches produce twice as many Purrs.", 10, 55_000.0, 2.0),
    GeneratorUpgrade("window_panorama", "window_watch", "Panoramic Windows", "Better views make Window Watches five times stronger.", 50, 2_750_000.0, 5.0),
    GeneratorUpgrade("room_toys", "cat_room_generator", "Unlimited Toy Basket", "Cat Rooms produce twice as many Purrs.", 10, 600_000.0, 2.0),
    GeneratorUpgrade("room_deluxe", "cat_room_generator", "Deluxe Cat Rooms", "Luxury makes Cat Rooms five times stronger.", 50, 30_000_000.0, 5.0),
    GeneratorUpgrade("house_staff", "cat_house", "Human Staff", "Cat Houses produce twice as many Purrs.", 10, 6_500_000.0, 2.0),
    GeneratorUpgrade("house_palace", "cat_house", "Cat Palaces", "Cat Houses become five times more productive.", 50, 325_000_000.0, 5.0),
    GeneratorUpgrade("cafe_treats", "cat_cafe_generator", "Bottomless Treats", "Cat Cafés produce twice as many Purrs.", 10, 70_000_000.0, 2.0),
    GeneratorUpgrade("cafe_franchise", "cat_cafe_generator", "Global Cat Café Franchise", "Cat Cafés produce five times as many Purrs.", 50, 3_500_000_000.0, 5.0),
    GeneratorUpgrade("sanctuary_vets", "cat_sanctuary_generator", "World-Class Care", "Cat Sanctuaries produce twice as many Purrs.", 10, 1_000_000_000.0, 2.0),
    GeneratorUpgrade("sanctuary_network", "cat_sanctuary_generator", "Sanctuary Network", "Cat Sanctuaries produce five times as many Purrs.", 50, 50_000_000_000.0, 5.0),
    GeneratorUpgrade("factory_automation", "purr_factory", "Purr Automation", "Purr Factories produce twice as many Purrs.", 10, 16_500_000_000.0, 2.0),
    GeneratorUpgrade("factory_quantum", "purr_factory", "Quantum Purr Engines", "Purr Factories produce five times as many Purrs.", 50, 825_000_000_000.0, 5.0),
    GeneratorUpgrade("city_transit", "cat_city", "Cat Transit Network", "Cat Cities produce twice as many Purrs.", 10, 255_000_000_000.0, 2.0),
    GeneratorUpgrade("city_megacity", "cat_city", "Feline Megacities", "Cat Cities produce five times as many Purrs.", 50, 12_750_000_000_000.0, 5.0),
    GeneratorUpgrade("planet_satellites", "cat_planet", "Orbital Cat Satellites", "Cat Planets produce twice as many Purrs.", 10, 3_750_000_000_000.0, 2.0),
    GeneratorUpgrade("planet_empire", "cat_planet", "Interplanetary Cat Empire", "Cat Planets produce five times as many Purrs.", 50, 187_500_000_000_000.0, 5.0),
    GeneratorUpgrade("corporation_board", "cat_corporation", "Executive Cat Board", "Cat Corporations produce twice as many Purrs.", 10, 5.5e13, 2.0),
    GeneratorUpgrade("corporation_monopoly", "cat_corporation", "Global Purr Monopoly", "Cat Corporations produce five times as many Purrs.", 50, 2.75e15, 5.0),
    GeneratorUpgrade("country_infrastructure", "cat_country", "National Cat Infrastructure", "Cat Countries produce twice as many Purrs.", 10, 8.0e14, 2.0),
    GeneratorUpgrade("country_empire", "cat_country", "United Cat Nations", "Cat Countries produce five times as many Purrs.", 50, 4.0e16, 5.0),
    GeneratorUpgrade("moon_craters", "cat_moon", "Cozy Crater Colonies", "Cat Moons produce twice as many Purrs.", 10, 1.25e16, 2.0),
    GeneratorUpgrade("moon_network", "cat_moon", "Lunar Purr Network", "Cat Moons produce five times as many Purrs.", 50, 6.25e17, 5.0),
    GeneratorUpgrade("galaxy_starways", "cat_galaxy", "Interstellar Catways", "Cat Galaxies produce twice as many Purrs.", 10, 2.0e17, 2.0),
    GeneratorUpgrade("galaxy_core", "cat_galaxy", "Purring Galactic Core", "Cat Galaxies produce five times as many Purrs.", 50, 1.0e19, 5.0),
    GeneratorUpgrade("dimension_portals", "purr_dimension", "Purr Portals", "Purr Dimensions produce twice as many Purrs.", 10, 3.5e18, 2.0),
    GeneratorUpgrade("dimension_resonance", "purr_dimension", "Infinite Resonance", "Purr Dimensions produce five times as many Purrs.", 50, 1.75e20, 5.0),
    GeneratorUpgrade("universe_constants", "cat_universe", "Feline Physical Constants", "Cat Universes produce twice as many Purrs.", 10, 6.5e19, 2.0),
    GeneratorUpgrade("universe_everything", "cat_universe", "Cats Are Everything", "Cat Universes produce five times as many Purrs.", 50, 3.25e21, 5.0),
    GeneratorUpgrade("multiverse_council", "cat_multiverse", "Multiversal Cat Council", "Cat Multiverses produce twice as many Purrs.", 10, 1.25e21, 2.0),
    GeneratorUpgrade("multiverse_convergence", "cat_multiverse", "Purr Convergence", "Cat Multiverses produce five times as many Purrs.", 50, 6.25e22, 5.0),
    GeneratorUpgrade("realm_echo", "infinite_cat_realm", "Echoes Beyond Infinity", "Infinite Cat Realms produce twice as many Purrs.", 10, 2.5e22, 2.0),
    GeneratorUpgrade("realm_ascension", "infinite_cat_realm", "Ultimate Cat Ascension", "Infinite Cat Realms produce five times as many Purrs.", 50, 1.25e24, 5.0),
)

fun generatorUpgradeMultiplier(generatorId: String, purchased: Set<String>): Double =
    generatorUpgrades
        .filter { it.generatorId == generatorId && it.id in purchased }
        .fold(1.0) { total, upgrade -> total * upgrade.multiplier }
