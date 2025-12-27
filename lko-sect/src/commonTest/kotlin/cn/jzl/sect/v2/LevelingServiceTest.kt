package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.ecs.entity
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.upgradeable.ExperienceFormula
import cn.jzl.sect.ecs.upgradeable.LevelingService
import cn.jzl.sect.ecs.attribute.attributeAddon
import cn.jzl.sect.ecs.upgradeable.levelingAddon
import kotlin.test.Test
import kotlin.test.assertEquals

class LevelingServiceTest {

    private object SimpleFormula : ExperienceFormula {
        override fun getExperienceForLevel(level: Long): Long = 10
    }

    @Test
    fun addExperience_levelsUp_whenEnoughExperience() {
        val world = world {
            install(attributeAddon)
            install(levelingAddon)
        }

        val levelingService by world.di.instance<LevelingService>()

        val entity = world.entity { }
        world.entity(entity) {
            levelingService.upgradeable(this, it)
            // required component for leveling
            it.addComponent(SimpleFormula)
        }

        assertEquals(1, levelingService.getLevel(entity))
        assertEquals(0, levelingService.getExperience(entity))

        levelingService.addExperience(entity, exp = 25)

        // Each level requires 10 exp, so 25 => level 3 with 5 remaining.
        assertEquals(3, levelingService.getLevel(entity))
        assertEquals(5, levelingService.getExperience(entity))
    }

    @Test
    fun forcedUpgrade_incrementsLevelAndClearsExperience() {
        val world = world {
            install(attributeAddon)
            install(levelingAddon)
        }

        val levelingService by world.di.instance<LevelingService>()

        val entity = world.entity { }
        world.entity(entity) {
            levelingService.upgradeable(this, it)
            it.addComponent(SimpleFormula)
        }

        levelingService.addExperience(entity, exp = 7)
        assertEquals(7, levelingService.getExperience(entity))

        levelingService.forcedUpgrade(entity)

        assertEquals(2, levelingService.getLevel(entity))
        assertEquals(0, levelingService.getExperience(entity))
    }
}

