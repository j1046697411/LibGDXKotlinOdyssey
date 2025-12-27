/**
 * 升级系统模块，负责管理实体的等级提升、经验累积和升级事件触发。
 *
 * 升级系统基于属性系统，通过为实体添加等级和经验属性来实现升级机制。
 * 支持自定义经验计算公式，可以根据不同实体类型设置不同的升级规则。
 *
 * 核心功能：
 * - 经验累积和等级提升
 * - 升级事件触发
 * - 自定义经验计算公式
 * - 等级和经验查询
 * - 强制升级功能
 */
package cn.jzl.sect.ecs.upgradeable

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.observers.emit
import cn.jzl.sect.ecs.attribute.AttributeService
import cn.jzl.sect.ecs.attribute.AttributeValue
import cn.jzl.sect.ecs.attribute.attributeAddon
import cn.jzl.sect.ecs.core.Named

/**
 * 可升级标签组件，用于标识实体可以进行升级。
 *
 * 所有需要支持升级功能的实体都必须添加此标签，以便升级系统进行管理。
 */
sealed class Upgradeable

/**
 * 升级事件，当实体等级提升时触发。
 *
 * 此事件可以被其他系统监听，用于实现升级后的各种效果，如属性提升、技能解锁等。
 *
 * @property oldLevel 升级前的等级
 * @property newLevel 升级后的等级
 */
data class OnUpgradeEvent(val oldLevel: Long, val newLevel: Long)

/**
 * 经验计算公式接口，定义了计算不同等级所需经验的方法。
 *
 * 可以通过实现此接口来自定义不同实体的升级规则，例如线性增长、指数增长等。
 */
interface ExperienceFormula {
    /**
     * 计算指定等级所需的经验值。
     *
     * @param level 目标等级
     * @return 升级到该等级所需的经验值
     */
    fun getExperienceForLevel(level: Long): Long
}

/**
 * 升级系统插件，负责注册升级相关的组件、服务和依赖注入。
 *
 * 该插件负责：
 * - 安装属性系统插件
 * - 注册可升级标签组件、经验计算公式和升级事件
 * - 注入LevelingService服务单例
 */
val levelingAddon = createAddon("level") {
    install(attributeAddon)
    injects { this bind singleton { new(::LevelingService) } }

    components {
        world.componentId<Upgradeable> { it.tag() }
        world.componentId<ExperienceFormula>()
        world.componentId<OnUpgradeEvent>()
    }
}

/**
 * 升级服务类，负责管理实体的等级提升、经验累积和升级事件触发。
 *
 * 该服务是升级系统的核心，提供了完整的升级功能API，包括：
 * - 为实体添加经验
 * - 检查和处理升级
 * - 设置实体为可升级
 * - 查询实体等级和经验
 * - 强制升级功能
 *
 * @property world ECS世界实例
 */
class LevelingService(world: World) : EntityRelationContext(world) {
    private val attributeService by world.di.instance<AttributeService>()
    private val attributeLevel by lazy { attributeService.attribute(ATTRIBUTE_LEVEL) }
    private val attributeExperience by lazy { attributeService.attribute(ATTRIBUTE_EXPERIENCE) }

    /**
     * 检查实体是否可升级。
     *
     * @param entity 要检查的实体
     * @throws IllegalArgumentException 如果实体不可升级
     */
    private fun checkUpgrade(entity: Entity) {
        require(entity.hasTag<Upgradeable>()) { "实体 $entity 不可升级，需要添加 Upgradeable 标签" }
    }

    /**
     * 获取默认的经验计算公式。
     *
     * 默认公式为固定值100经验/级，适合简单的升级机制。
     *
     * @return 默认经验计算公式
     */
    private fun defaultFormula(): ExperienceFormula = object : ExperienceFormula {
        override fun getExperienceForLevel(level: Long): Long = 100
    }

    /**
     * 为实体添加经验值。
     *
     * 当累积的经验达到升级所需时，实体将自动升级，并触发升级事件。
     *
     * @param entity 要添加经验的实体
     * @param exp 要添加的经验值（必须大于0）
     * @throws IllegalArgumentException 如果经验值小于等于0或实体不可升级
     */
    fun addExperience(entity: Entity, exp: Long) {
        require(exp > 0) { "经验值必须大于0" }
        require(entity.hasTag<Upgradeable>()) { "实体 $entity 不可升级，需要添加 Upgradeable 标签" }

        val level = attributeService.getAttributeValue(entity, attributeLevel) ?: AttributeValue.one
        val remainingExperience = attributeService.getAttributeValue(entity, attributeExperience) ?: AttributeValue.zero
        val experienceFormula = entity.getComponent<ExperienceFormula?>() ?: defaultFormula()

        var remaining = remainingExperience.value + exp
        var currentLevel = level.value

        // 检查是否可以升级
        while (true) {
            val upgradeRequiredExperience = experienceFormula.getExperienceForLevel(currentLevel + 1)
            if (remaining < upgradeRequiredExperience) break
            currentLevel++
            remaining -= upgradeRequiredExperience
        }

        val upgrade = currentLevel != level.value

        // 更新实体的经验和等级
        world.entity(entity) {
            attributeService.setAttributeValue(this, entity, attributeExperience, AttributeValue(remaining))
            if (upgrade) {
                attributeService.setAttributeValue(this, entity, attributeLevel, AttributeValue(currentLevel))
            }
        }

        // 触发升级事件
        if (upgrade) world.emit(entity, OnUpgradeEvent(level.value, currentLevel))
    }

    /**
     * 将实体设置为可升级。
     *
     * 此方法会为实体添加可升级标签，并初始化等级和经验属性。
     *
     * @param entityCreateContext 实体创建上下文
     * @param entity 要设置为可升级的实体
     */
    fun upgradeable(entityCreateContext: EntityCreateContext, entity: Entity) = entityCreateContext.run {
        entity.addTag<Upgradeable>()
        attributeService.setAttributeValue(this, entity, attributeLevel, AttributeValue.one)
        attributeService.setAttributeValue(this, entity, attributeExperience, AttributeValue.zero)
    }

    /**
     * 获取实体的当前等级。
     *
     * @param entity 要查询的实体
     * @return 实体的当前等级
     * @throws IllegalArgumentException 如果实体不可升级
     */
    fun getLevel(entity: Entity): Long {
        checkUpgrade(entity)
        return attributeService.getAttributeValue(entity, attributeLevel)?.value ?: 1
    }

    /**
     * 获取实体的当前经验值。
     *
     * @param entity 要查询的实体
     * @return 实体的当前经验值
     * @throws IllegalArgumentException 如果实体不可升级
     */
    fun getExperience(entity: Entity): Long {
        checkUpgrade(entity)
        return attributeService.getAttributeValue(entity, attributeExperience)?.value ?: 0
    }

    /**
     * 强制实体升级。
     *
     * 此方法会直接将实体等级提升1级，并重置经验值为0，但不会检查是否满足升级条件。
     *
     * @param entity 要强制升级的实体
     * @throws IllegalArgumentException 如果实体不可升级
     */
    fun forcedUpgrade(entity: Entity) {
        checkUpgrade(entity)
        val level = attributeService.getAttributeValue(entity, attributeLevel)?.value ?: 1
        world.entity(entity) {
            attributeService.setAttributeValue(this, entity, attributeExperience, AttributeValue.zero)
            attributeService.setAttributeValue(this, entity, attributeLevel, AttributeValue(level + 1))
        }
    }

    companion object {
        /** 等级属性名称 */
        val ATTRIBUTE_LEVEL = Named("level")

        /** 经验属性名称 */
        val ATTRIBUTE_EXPERIENCE = Named("experience")
    }
}
