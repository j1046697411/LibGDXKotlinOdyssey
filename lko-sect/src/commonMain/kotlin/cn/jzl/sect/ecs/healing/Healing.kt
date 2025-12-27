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


@JvmInline
value class HealingAmount(val value: Long)

class HealthService(world: World) : EntityRelationContext(world) {

    private val attributeService by world.di.instance<AttributeService>()

    val attributeCurrentHealth by lazy { attributeService.attribute(ATTRIBUTE_CURRENT_HEALTH) }
    val attributeMaxHealth by lazy { attributeService.attribute(ATTRIBUTE_MAX_HEALTH) }

    companion object {
        val ATTRIBUTE_CURRENT_HEALTH = Named("currentHealth")

        val ATTRIBUTE_MAX_HEALTH = Named("maxHealth")
    }

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

    fun getCurrentHealth(entity: Entity): Long {
        return attributeService.getAttributeValue(entity, attributeCurrentHealth)?.value ?: 0
    }

    fun getMaxHealth(entity: Entity): Long {
        return attributeService.getAttributeValue(entity, attributeMaxHealth)?.value ?: 0
    }

    fun damage(entity: Entity, amount: Long) {
        require(amount > 0) { "伤害值必须大于0" }
        val currentHealth = getCurrentHealth(entity)
        val newHealth = maxOf(0, currentHealth - amount)
        world.entity(entity) {
            attributeService.setAttributeValue(this, it, attributeCurrentHealth, AttributeValue(newHealth))
        }
    }

    private fun heal(entity: Entity, amount: Long) {
        require(amount > 0) { "恢复值必须大于0" }
        val currentHealth = getCurrentHealth(entity)
        val maxHealth = getMaxHealth(entity)
        val newHealth = minOf(maxHealth, currentHealth + amount)
        world.entity(entity) {
            attributeService.setAttributeValue(this, it, attributeCurrentHealth, AttributeValue(newHealth))
        }
    }

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
