@file:Suppress("UNCHECKED_CAST")

package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World

class ItemActionProvider(world: World) : ActionProvider, EntityRelationContext(world) {

    private val inventoryService by world.di.instance<InventoryService>()
    private val itemService by world.di.instance<ItemService>()

    private val actions = mutableMapOf<Entity, UseItemAction>()

    override fun getActions(stateProvider: WorldStateReader, agent: Entity): Sequence<Action> {
        return itemService.itemPrefabs().filter {
            itemService.isUsable(it) && stateProvider.getValue(agent, ItemAmountKey(it)) >= 1
        }.map {
            actions.getOrPut(it) { UseItemAction(world, it, inventoryService) }
        }
    }

    private class UseItemAction(
        world: World,
        private val itemPrefab: Entity,
        private val inventoryService: InventoryService,
    ) : Action, EntityRelationContext(world) {

        override val name: String by lazy {
            val itemName = inventoryService.run { itemPrefab.getComponent<Named>().name }
            "使用 $itemName"
        }

        private val itemAmountKey by lazy { ItemAmountKey(itemPrefab) }
        private val healthService by world.di.instance<HealthService>()

        override val preconditions: Sequence<Precondition> = sequenceOf(
            Precondition { stateProvider, agent ->
                stateProvider.getValue(agent, itemAmountKey) >= 1
            }
        )

        override val effects: Sequence<ActionEffect> = sequenceOf(
            ActionEffect { stateProvider, agent ->
                val healingAmount = itemPrefab.getComponent<HealingAmount?>()
                healingAmount?.let {
                    stateProvider.increase(agent, AttributeKey(healthService.attributeCurrentHealth), it.value)
                }
                stateProvider.decrease(agent, itemAmountKey, 1)
            }
        )

        override val cost: Double = 1.0

        override val task: suspend World.(Entity) -> Unit = { agent ->
            // 验证代理拥有至少1个物品
            if (inventoryService.hasEnoughItems(agent, itemPrefab, 1)) {
                // 从库存移除1个物品
                inventoryService.removeItem(agent, itemPrefab, 1)
            }
            // 如果验证失败，任务执行失败（不抛出异常，只是不执行移除操作）
        }
    }
}

@JvmInline
value class ItemAmountKey(val itemPrefab: Entity) : StateKey<Int>

class ItemAmountStateResolver(world: World) : StateResolver<ItemAmountKey, Int> {
    private val inventoryService by world.di.instance<InventoryService>()
    override fun EntityRelationContext.getWorldState(agent: Entity, key: ItemAmountKey): Int {
        return inventoryService.getItemCount(agent, key.itemPrefab)
    }
}

class ItemStateResolverRegistry(world: World) : StateResolverRegistry {
    private val itemAmountStateHandler = ItemAmountStateResolver(world)

    override fun <K : StateKey<T>, T> getStateHandler(key: K): StateResolver<K, T>? {
        @Suppress("UNCHECKED_CAST")
        if (key is ItemAmountKey) return itemAmountStateHandler as StateResolver<K, T>
        return null
    }
}

@JvmInline
value class AttributeKey(val attribute: Entity) : StateKey<Long>

class AttributeStateResolver(world: World) : StateResolver<AttributeKey, Long> {

    private val attributeService by world.di.instance<AttributeService>()

    override fun EntityRelationContext.getWorldState(agent: Entity, key: AttributeKey): Long {
        return attributeService.getAttributeValue(agent, key.attribute)?.value ?: 0
    }
}

class AttributeStateResolverRegistry(world: World) : StateResolverRegistry {
    private val attributeStateHandler = AttributeStateResolver(world)

    override fun <K : StateKey<T>, T> getStateHandler(key: K): StateResolver<K, T>? {
        return when (key) {
            is AttributeKey -> attributeStateHandler as StateResolver<K, T>
            else -> null
        }
    }
}
