package cn.jzl.sect.ecs.healing

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.sect.ecs.inventory.InventoryService
import cn.jzl.sect.ecs.item.ItemService
import cn.jzl.sect.ecs.attribute.AttributeService
import cn.jzl.sect.ecs.attribute.AttributeValue
import cn.jzl.sect.ecs.attribute.attributeAddon
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.inventory.inventoryAddon
import cn.jzl.sect.ecs.item.itemAddon

/**
 * 治疗系统包，包含治疗组件、服务和addon配置
 *
 * 主要功能：
 * 1. 定义治疗量组件
 * 2. 提供生命值管理服务
 * 3. 支持伤害和治疗机制
 * 4. 实现治疗物品使用功能
 */

/**
 * 治疗量组件
 * 用于表示物品或技能的治疗效果量
 *
 * @param value 治疗量值
 */
@JvmInline
value class HealingAmount(val value: Long)

/**
 * 生命值服务
 * 管理实体的生命值系统
 *
 * @param world ECS世界实例
 */
class HealthService(world: World) : EntityRelationContext(world) {

    private val attributeService by world.di.instance<AttributeService>()

    /**
     * 当前生命值属性
     * 懒加载，确保在需要时才创建
     */
    val attributeCurrentHealth by lazy { attributeService.attribute(ATTRIBUTE_CURRENT_HEALTH) }

    /**
     * 最大生命值属性
     * 懒加载，确保在需要时才创建
     */
    val attributeMaxHealth by lazy { attributeService.attribute(ATTRIBUTE_MAX_HEALTH) }

    /**
     * 伴生对象，包含生命值相关的常量定义
     */
    companion object {
        /**
         * 当前生命值属性名称
         */
        val ATTRIBUTE_CURRENT_HEALTH = Named("currentHealth")

        /**
         * 最大生命值属性名称
         */
        val ATTRIBUTE_MAX_HEALTH = Named("maxHealth")
    }

    /**
     * 初始化实体的生命值
     *
     * @param context 实体创建上下文
     * @param entity 目标实体
     * @param maxHealth 最大生命值，默认100
     * @throws IllegalArgumentException 如果最大生命值小于等于0
     */
    fun initializeHealth(
        context: EntityCreateContext,
        entity: Entity,
        maxHealth: Long = 100
    ) {
        require(maxHealth > 0) { "最大生命值必须大于0" }
        context.run {
            attributeService.setAttributeValue(this, entity, attributeMaxHealth, AttributeValue(maxHealth))
            attributeService.setAttributeValue(this, entity, attributeCurrentHealth, AttributeValue(maxHealth))
        }
    }

    /**
     * 获取实体的当前生命值
     *
     * @param entity 目标实体
     * @return 当前生命值，默认为0
     */
    fun getCurrentHealth(entity: Entity): Long {
        return attributeService.getAttributeValue(entity, attributeCurrentHealth)?.value ?: 0
    }

    /**
     * 获取实体的最大生命值
     *
     * @param entity 目标实体
     * @return 最大生命值，默认为0
     */
    fun getMaxHealth(entity: Entity): Long {
        return attributeService.getAttributeValue(entity, attributeMaxHealth)?.value ?: 0
    }

    /**
     * 对实体造成伤害
     *
     * @param entity 目标实体
     * @param amount 伤害量
     * @throws IllegalArgumentException 如果伤害量小于等于0
     */
    fun damage(entity: Entity, amount: Long) {
        require(amount > 0) { "伤害值必须大于0" }
        val currentHealth = getCurrentHealth(entity)
        val newHealth = maxOf(0, currentHealth - amount)
        world.entity(entity) {
            attributeService.setAttributeValue(this, it, attributeCurrentHealth, AttributeValue(newHealth))
        }
    }

    /**
     * 治疗实体
     *
     * @param entity 目标实体
     * @param amount 治疗量
     * @throws IllegalArgumentException 如果治疗量小于等于0
     */
    private fun heal(entity: Entity, amount: Long) {
        require(amount > 0) { "恢复值必须大于0" }
        val currentHealth = getCurrentHealth(entity)
        val maxHealth = getMaxHealth(entity)
        val newHealth = minOf(maxHealth, currentHealth + amount)
        world.entity(entity) {
            attributeService.setAttributeValue(this, it, attributeCurrentHealth, AttributeValue(newHealth))
        }
    }

    /**
     * 使用治疗物品
     *
     * @param entity 使用物品的实体
     * @param itemPrefab 治疗物品预制体
     * @return 是否成功使用物品
     */
    fun useHealingItem(entity: Entity, itemPrefab: Entity): Boolean {
        val inventoryService by world.di.instance<InventoryService>()
        val itemService by world.di.instance<ItemService>()

        // 验证角色拥有至少1个道具
        if (!inventoryService.hasEnoughItems(entity, itemPrefab, 1)) {
            return false
        }

        // 验证道具有 HealingAmount 组件
        val healingAmount = itemPrefab.getComponent<HealingAmount?>() ?: return false

        // 验证道具有 Usable 标签
        if (!itemService.isUsable(itemPrefab)) {
            return false
        }

        // 恢复生命值
        heal(entity, healingAmount.value)

        // 消耗道具
        inventoryService.removeItem(entity, itemPrefab, 1)

        return true
    }
}

/**
 * 健康系统addon
 * 注册治疗相关组件和服务
 */
val healthAddon = createAddon("health") {
    install(attributeAddon)
    install(itemAddon)
    install(inventoryAddon)

    injects {
        this bind singleton { new(::HealthService) }
    }

    components {
        world.componentId<HealingAmount>()
    }
}
