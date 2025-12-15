@file:Suppress("UNCHECKED_CAST")

package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.ecs.*
import cn.jzl.sect.ecs.core.Named

class ItemActionProvider(world: World) : ActionProvider, EntityRelationContext(world) {

    private val inventoryService by world.di.instance<InventoryService>()
    private val goapService by world.di.instance<PlanningService>()
    private val itemService by world.di.instance<ItemService>()

    private val actions = mutableMapOf<Entity, UseItemAction>()

    override fun getActions(stateProvider: WorldStateReader, agent: Entity): Sequence<Action> {
        return itemService.itemPrefabs().filter {
            itemService.isUsable(it) && stateProvider.getValue(agent, ItemAmountKey(it)) >= 1
        }.map {
            actions.getOrPut(it) { UseItemAction(world, it, inventoryService, goapService) }
        }
    }

    private class UseItemAction(
        world: World,
        private val itemPrefab: Entity,
        private val inventoryService: InventoryService,
        private val goapService: PlanningService
    ) : Action , EntityRelationContext(world) {

        override val name: String by lazy {
            val itemName = inventoryService.run { itemPrefab.getComponent<Named>().name }
            "使用 $itemName"
        }

        private val itemAmountKey by lazy { ItemAmountKey(itemPrefab) }
        private val healthService by world.di.instance<HealthService>()

        override val preconditions: WorldState by lazy {
            goapService.createWorldState(mapOf(itemAmountKey to 1))
        }

        override val effects: WorldState by lazy {
            val healingAmount = itemPrefab.getComponent<HealingAmount?>()
            val map = buildMap<StateKey<*>, Any> {
                healingAmount?.let {
                    this[AttributeKey(healthService.attributeCurrentHealth)] = it.value
                }
                this[itemAmountKey] = 1
            }
            goapService.createWorldState(map)
        }

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

    override fun EntityRelationContext.merge(agent: Entity, key: ItemAmountKey, current: Int?, effect: Int): Int {
        val actualCurrent = current ?: getWorldState(agent, key)
        val result = actualCurrent - effect
        return if (result < 0) 0 else result
    }

    override fun EntityRelationContext.satisfies(agent: Entity, key: ItemAmountKey, current: Int?, condition: Int): Boolean {
        val actualCurrent = current ?: getWorldState(agent, key)
        return actualCurrent >= condition
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

    override fun EntityRelationContext.merge(agent: Entity, key: AttributeKey, current: Long?, effect: Long): Long {
        return (current ?: 0) + effect
    }

    override fun EntityRelationContext.satisfies(agent: Entity, key: AttributeKey, current: Long?, condition: Long): Boolean {
        return (current ?: 0) < condition
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
