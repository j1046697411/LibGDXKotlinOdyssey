package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.observers.emit
import cn.jzl.sect.ecs.core.Named

sealed class Upgradeable

data class OnUpgradeEvent(val oldLevel: Long, val newLevel: Long)

fun interface ExperienceFormula {
    fun getExperienceForLevel(level: Long): Long
}

val levelingAddon = createAddon("leveling", {}) {
    attributes {
        register(LevelingService.ATTRIBUTE_LEVEL)
        register(LevelingService.ATTRIBUTE_EXPERIENCE)
    }
    injects { this bind singleton { new(::LevelingService) } }
    components {
        world.componentId<Upgradeable> { it.tag() }
        world.componentId<OnUpgradeEvent>()
        world.componentId<ExperienceFormula>()
    }
}

class LevelingService(world: World) : EntityRelationContext(world) {

    private val attributeService by world.di.instance<AttributeService>()
    private val experienceFormula = ExperienceFormula { 100 }

    private val attributeLevel: Entity by lazy { attributeService.attribute(ATTRIBUTE_LEVEL) }
    private val attributeExperience: Entity by lazy { attributeService.attribute(ATTRIBUTE_EXPERIENCE) }

    fun addExperience(entity: Entity, exp: Long) {
        require(entity.hasTag<Upgradeable>()) { "实体${entity.id}不是可升级对象" }
        val level = entity.getRelation<AttributeValue?>(attributeLevel) ?: AttributeValue.ONE
        val currentExp = entity.getRelation<AttributeValue?>(attributeExperience) ?: AttributeValue.ZERO
        val experienceFormula = entity.getComponent<ExperienceFormula?>() ?: experienceFormula
        var remainingExp = currentExp.value + exp
        var currentLevel = level.value
        println("[Leveling] addExperience entity=${entity.id} level=${level.value} currentExp=${currentExp.value} add=${exp}")
        while (true) {
            val upgradeExperience = experienceFormula.getExperienceForLevel(currentLevel + 1)
            println("[Leveling] checking level=${currentLevel + 1} need=$upgradeExperience remaining=$remainingExp")
            if (upgradeExperience > remainingExp) break
            currentLevel++
            remainingExp -= upgradeExperience
        }
        println("[Leveling] result newLevel=$currentLevel remaining=$remainingExp")
        val upgrade = currentLevel != level.value
        world.entity(entity) {
            if (upgrade) it.addRelation(attributeLevel, AttributeValue(currentLevel))
            it.addRelation(attributeExperience, AttributeValue(remainingExp))
        }
        if (upgrade) {
            world.emit(entity, OnUpgradeEvent(level.value, currentLevel))
        }
    }

    fun upgradeable(context: EntityCreateContext, entity: Entity, formula: ExperienceFormula? = null): Unit = context.run {
        entity.addTag<Upgradeable>()
        entity.addRelation(attributeLevel, AttributeValue.ONE)
        entity.addRelation(attributeExperience, AttributeValue.ZERO)
        entity.addComponent(formula ?: experienceFormula)
    }

    fun getLevel(entity: Entity): Long {
        require(entity.hasTag<Upgradeable>()) { "实体${entity.id}不是可升级对象" }
        return entity.getRelation<AttributeValue?>(attributeLevel)?.value ?: 0
    }

    fun getExperience(entity: Entity): Long {
        require(entity.hasTag<Upgradeable>()) { "实体${entity.id}不是可升级对象" }
        return entity.getRelation<AttributeValue?>(attributeExperience)?.value ?: 0
    }

    companion object {
        val ATTRIBUTE_LEVEL = Named("Level")
        val ATTRIBUTE_EXPERIENCE = Named("Experience")
    }
}
