package com.freetime.catempire

import com.freetime.catempire.game.GameState
import com.freetime.catempire.game.generatorSynergies
import com.freetime.catempire.game.generatorUpgrades
import com.freetime.catempire.game.starterGenerators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneratorProgressionTest {
    @Test
    fun generatorCatalogHasUniqueIncreasingTiersAndTwoUpgradesEach() {
        val generators = starterGenerators()

        assertEquals(18, generators.size)
        assertEquals(generators.size, generators.map { it.id }.toSet().size)
        assertTrue(generators.zipWithNext().all { (current, next) ->
            next.baseCost > current.baseCost && next.basePurrsPerSecond > current.basePurrsPerSecond
        })
        assertTrue(generatorUpgrades.all { upgrade -> generators.any { it.id == upgrade.generatorId } })
        generators.forEach { generator ->
            assertEquals(2, generatorUpgrades.count { it.generatorId == generator.id })
        }
    }

    @Test
    fun synergiesConnectEveryPairOfNeighboringGeneratorTiers() {
        val generators = starterGenerators()

        assertEquals(generators.size - 1, generatorSynergies.size)
        generators.zipWithNext().forEachIndexed { index, (current, next) ->
            val synergy = generatorSynergies[index]
            assertEquals(current.id, synergy.generatorA)
            assertEquals(next.id, synergy.generatorB)
        }
    }

    @Test
    fun lateGameUpgradesAndSynergiesContributeToProduction() {
        val generators = starterGenerators().filter { it.id == "cat_planet" || it.id == "cat_corporation" }.map {
            when (it.id) {
                "cat_planet" -> it.copy(owned = 25)
                else -> it.copy(owned = 10)
            }
        }
        val state = GameState(
            generators = generators,
            purchasedGeneratorUpgrades = setOf("corporation_board"),
            purchasedGeneratorSynergies = setOf("planets_corporations"),
        )
        val expectedPlanetProduction = 1_600_000.0 * 25 * 4 * 1.10
        val expectedCorporationProduction = 10_000_000.0 * 10 * 2 * 2 * 1.25

        assertEquals(expectedPlanetProduction + expectedCorporationProduction, state.generatorPurrsPerSecond, 0.001)
    }
}
