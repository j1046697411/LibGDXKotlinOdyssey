package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityCreateContext
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.entity
import cn.jzl.ecs.observers.emit
import cn.jzl.sect.ecs.core.Named
import kotlin.getValue


sealed class Upgradeable

data class OnUpgradeEvent(val oldLevel: Long, val newLevel: Long)

interface ExperienceFormula {
    fun getExperienceForLevel(level: Long): Long
}

val levelingAddon = createAddon("level") {
    install(attributeAddon)
    injects { this bind singleton { new(::LevelingService) } }

    components {
        world.componentId<Upgradeable> { it.tag() }
        world.componentId<ExperienceFormula>()
        world.componentId<OnUpgradeEvent>()
    }

}

class LevelingService(world: World) : EntityRelationContext(world) {
    private val attributeService by world.di.instance<AttributeService>()
    private val attributeLevel by lazy { attributeService.attribute(ATTRIBUTE_LEVEL) }
    private val attributeExperience by lazy { attributeService.attribute(ATTRIBUTE_EXPERIENCE) }

    private fun checkUpgrade(entity: Entity) {
        require(entity.hasTag<Upgradeable>())
    }

    fun addExperience(entity: Entity, exp: Long) {
        require(exp > 0) {}
        require(entity.hasTag<Upgradeable>()) {}
        val level = attributeService.getAttributeValue(entity, attributeLevel) ?: AttributeValue.one
        val remainingExperience = attributeService.getAttributeValue(entity, attributeExperience) ?: AttributeValue.zero
        val experienceFormula = entity.getComponent<ExperienceFormula>()
        var remaining = remainingExperience.value + exp
        var currentLevel = level.value
        while (true) {
            val upgradeRequiredExperience = experienceFormula.getExperienceForLevel(currentLevel + 1)
            if (remaining < upgradeRequiredExperience) break
            currentLevel++
            remaining -= upgradeRequiredExperience
        }
        val upgrade = currentLevel != level.value
        world.entity(entity) {
            attributeService.setAttributeValue(this, it, attributeExperience, AttributeValue(remaining))
            if (upgrade) {
                attributeService.setAttributeValue(this, it, attributeLevel, AttributeValue(currentLevel))
            }
        }
        if (upgrade) world.emit(entity, OnUpgradeEvent(level.value, currentLevel))
    }

    fun upgradeable(entityCreateContext: EntityCreateContext, entity: Entity) = entityCreateContext.run {
        entity.addTag<Upgradeable>()
        attributeService.setAttributeValue(this, entity, attributeLevel, AttributeValue.one)
        attributeService.setAttributeValue(this, entity, attributeExperience, AttributeValue.zero)
    }

    fun getLevel(entity: Entity): Long {
        checkUpgrade(entity)
        return attributeService.getAttributeValue(entity, attributeLevel)?.value ?: 1
    }

    fun getExperience(entity: Entity): Long {
        checkUpgrade(entity)
        return attributeService.getAttributeValue(entity, attributeExperience)?.value ?: 0
    }

    fun forcedUpgrade(entity: Entity) {
        checkUpgrade(entity)
        val level = attributeService.getAttributeValue(entity, attributeLevel)?.value ?: 1
        world.entity(entity) {
            attributeService.setAttributeValue(this, it, attributeExperience, AttributeValue.zero)
            attributeService.setAttributeValue(this, it, attributeLevel, AttributeValue(level + 1))
        }
    }

    companion object {
        val ATTRIBUTE_LEVEL = Named("level")
        val ATTRIBUTE_EXPERIENCE = Named("experience")
    }
}
