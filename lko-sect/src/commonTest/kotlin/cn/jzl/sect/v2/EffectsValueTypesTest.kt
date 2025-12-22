package cn.jzl.sect.v2

import cn.jzl.sect.ecs.AttributeModifier
import cn.jzl.sect.ecs.EffectStack
import cn.jzl.sect.ecs.ModifierType
import cn.jzl.sect.ecs.TickInterval
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.seconds

class EffectsValueTypesTest {

    @Test
    fun effectStack_validatesBounds() {
        // valid
        EffectStack(maxStacks = 2, currentStacks = 1)

        assertFailsWith<IllegalArgumentException> {
            EffectStack(maxStacks = 0, currentStacks = 1)
        }

        assertFailsWith<IllegalArgumentException> {
            EffectStack(maxStacks = 2, currentStacks = 3)
        }
    }

    @Test
    fun attributeModifier_validatesMultiplier() {
        val dummyAttr = cn.jzl.ecs.Entity(id = 1, version = 0)

        AttributeModifier(attribute = dummyAttr, modifierType = ModifierType.ADD, value = 1, multiplier = 1.0f)

        assertFailsWith<IllegalArgumentException> {
            AttributeModifier(attribute = dummyAttr, modifierType = ModifierType.ADD, value = 1, multiplier = 0.0f)
        }
    }

    @Test
    fun tickIntervalReportsSeconds() {
        val interval = TickInterval.seconds(2)
        assertEquals(2, interval.inWholeSeconds)
        assertEquals(2.seconds.inWholeSeconds, interval.duration.inWholeSeconds)
    }
}

