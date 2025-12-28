@file:Suppress("UNCHECKED_CAST")

package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World
import cn.jzl.sect.ecs.attribute.AttributeKey
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.healing.HealingAmount
import cn.jzl.sect.ecs.healing.HealthService
import cn.jzl.sect.ecs.inventory.InventoryService
import cn.jzl.sect.ecs.item.ItemService
import cn.jzl.sect.ecs.planning.*

/**
 * ECS系统核心文件，包含各种行动提供者和状态解析器
 *
 * 主要功能：
 * 1. 提供物品使用行动
 * 2. 实现物品数量和属性的状态解析
 * 3. 管理行动的条件和效果
 */

/**
 * 物品行动提供者
 * 负责为可使用的物品提供行动选项
 *
 * @param world ECS世界实例
 */
class ItemActionProvider(world: World) : ActionProvider, EntityRelationContext(world) {

    private val inventoryService by world.di.instance<InventoryService>()
    private val itemService by world.di.instance<ItemService>()

    private val actions = mutableMapOf<Entity, UseItemAction>()

    /**
     * 获取指定代理可以执行的物品相关行动
     *
     * @param stateReader 世界状态读取器，用于获取当前状态
     * @param agent 执行行动的代理实体
     * @return 可执行的行动序列
     */
    override fun getActions(stateReader: WorldStateReader, agent: Entity): Sequence<GOAPAction> {
        return itemService.itemPrefabs().filter {
            itemService.isUsable(it) && stateReader.getValue(agent, ItemAmountKey(it)) >= 1
        }.map {
            actions.getOrPut(it) { UseItemAction(world, it, inventoryService) }
        }
    }

    /**
     * 物品使用行动
     * 表示使用特定物品的行动
     *
     * @param world ECS世界实例
     * @param itemPrefab 物品预制体
     * @param inventoryService 库存服务
     */
    private class UseItemAction(
        world: World,
        private val itemPrefab: Entity,
        private val inventoryService: InventoryService,
    ) : GOAPAction, EntityRelationContext(world) {

        /**
         * 行动名称
         * 格式："使用 [物品名称]"
         */
        override val name: String by lazy {
            val itemName = inventoryService.run { itemPrefab.getComponent<Named>().name }
            "使用 $itemName"
        }

        private val itemAmountKey by lazy { ItemAmountKey(itemPrefab) }
        private val healthService by world.di.instance<HealthService>()

        /**
         * 行动前置条件
         * 要求代理拥有至少1个目标物品
         */
        override val preconditions: Sequence<Precondition> = sequenceOf(
            Precondition { stateProvider, agent ->
                stateProvider.getValue(agent, itemAmountKey) >= 1
            }
        )

        /**
         * 行动效果
         * 1. 如果物品有治疗效果，增加代理的生命值
         * 2. 减少代理的物品数量
         */
        override val effects: Sequence<ActionEffect> = sequenceOf(
            ActionEffect { stateProvider, agent ->
                val healingAmount = itemPrefab.getComponent<HealingAmount?>()
                healingAmount?.let {
                    stateProvider.increase(agent, AttributeKey(healthService.attributeCurrentHealth), it.value)
                }
                stateProvider.decrease(agent, itemAmountKey, 1)
            }
        )

        /**
         * 行动成本
         */
        override val cost: Double = 1.0

        /**
         * 行动执行任务
         * 从代理的库存中移除1个物品
         *
         * @param agent 执行行动的代理实体
         */
        override val task: ActionTask = ActionTask {
            // 验证代理拥有至少1个物品
            if (inventoryService.hasEnoughItems(agent, itemPrefab, 1)) {
                // 从库存移除1个物品
                inventoryService.removeItem(agent, itemPrefab, 1)
            }
            // 如果验证失败，任务执行失败（不抛出异常，只是不执行移除操作）
        }
    }
}

/**
 * 物品数量状态键
 * 用于标识特定物品的数量状态
 *
 * @param itemPrefab 物品预制体
 */
@JvmInline
value class ItemAmountKey(val itemPrefab: Entity) : StateKey<Int>

/**
 * 物品数量状态解析器
 * 负责解析实体拥有的特定物品数量
 *
 * @param world ECS世界实例
 */
class ItemAmountStateResolver(world: World) : StateResolver<ItemAmountKey, Int> {
    private val inventoryService by world.di.instance<InventoryService>()

    /**
     * 获取指定代理拥有的特定物品数量
     *
     * @param agent 代理实体
     * @param key 物品数量状态键
     * @return 物品数量
     */
    override fun EntityRelationContext.getWorldState(agent: Entity, key: ItemAmountKey): Int {
        return inventoryService.getItemCount(agent, key.itemPrefab)
    }
}

/**
 * 物品状态解析器注册表
 * 管理物品相关的状态解析器
 *
 * @param world ECS世界实例
 */
class ItemStateResolverRegistry(world: World) : StateResolverRegistry {
    private val itemAmountStateHandler = ItemAmountStateResolver(world)

    /**
     * 获取指定状态键的状态解析器
     *
     * @param key 状态键
     * @return 对应的状态解析器，若不存在则返回null
     */
    override fun <K : StateKey<T>, T> getStateHandler(key: K): StateResolver<K, T>? {
        @Suppress("UNCHECKED_CAST")
        if (key is ItemAmountKey) return itemAmountStateHandler as StateResolver<K, T>
        return null
    }
}
