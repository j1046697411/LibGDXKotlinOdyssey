package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Components
import cn.jzl.ecs.Entities
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.FamilyMatcher
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.destroy
import cn.jzl.ecs.entity
import cn.jzl.ecs.isActive
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.forEach
import cn.jzl.ecs.query.query
import cn.jzl.ecs.query.singleQuery
import cn.jzl.ecs.query.sumBy
import cn.jzl.ecs.system.update
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.core.coreAddon
import cn.jzl.sect.ecs.item.Amount
import cn.jzl.sect.ecs.item.Item
import cn.jzl.sect.ecs.item.ItemService
import cn.jzl.sect.ecs.item.Stackable
import cn.jzl.sect.ecs.item.itemAddon
import kotlin.time.Duration.Companion.seconds

val inventoryAddon = createAddon("inventory") {
    install(coreAddon)
    install(itemAddon)
    injects { this bind singleton { new(::InventoryService) } }
}

class InventoryService(world: World) : EntityRelationContext(world) {

    private val itemService by world.di.instance<ItemService>()

    /**
     * 获取玩家拥有的所有物品实体
     * @param player 玩家实体
     * @return 玩家拥有的所有物品实体序列
     */
    fun getAllItems(player: Entity): Sequence<Entity> {
        return world.query {
            object : EntityQueryContext(this) {
                override fun FamilyMatcher.FamilyBuilder.configure() {
                    component<Item>()
                    relation<OwnedBy>(player)
                }
            }
        }.entities
    }

    /**
     * 获取玩家拥有的特定物品数量（按预制体统计）
     * @param player 玩家实体
     * @param itemPrefab 物品预制体
     * @return 该物品的总数量
     */
    fun getItemCount(player: Entity, itemPrefab: Entity): Int {
        return world.singleQuery {
            object : EntityQueryContext(this) {
                val amount by component<Amount?>()
                override fun FamilyMatcher.FamilyBuilder.configure() {
                    component<Item>()
                    relation<OwnedBy>(player)
                    relation<Components.InstanceOf>(itemPrefab)
                }
            }
        }.sumBy { amount?.value ?: 1 }
    }

    /**
     * 检查玩家是否有足够的物品
     * @param player 玩家实体
     * @param itemPrefab 物品预制体
     * @param count 需要的数量
     * @return 是否有足够的物品
     */
    fun hasEnoughItems(player: Entity, itemPrefab: Entity, count: Int): Boolean = getItemCount(player, itemPrefab) >= count

    /**
     * 添加物品到玩家背包
     * @param player 玩家实体
     * @param itemPrefab 物品预制体
     * @param count 添加的数量
     */
    fun addItem(player: Entity, itemPrefab: Entity, count: Int) {
        require(count > 0) { "添加物品数量必须大于0" }

        // 检查是否为可堆叠物品
        val isStackable = itemPrefab.hasTag<Stackable>()

        if (isStackable && count > 1) {
            // 可堆叠物品，创建一个带有数量的物品实例
            itemService.item(itemPrefab) {
                it.addRelation<OwnedBy>(player)
                it.addComponent(Amount(count))
            }
        } else {
            // 不可堆叠物品或数量为1，创建多个单独的物品实例
            repeat(count) {
                itemService.item(itemPrefab) {
                    it.addRelation<OwnedBy>(player)
                }
            }
        }
    }

    /**
     * 从玩家背包移除指定数量的物品
     * @param player 玩家实体
     * @param itemPrefab 物品预制体
     * @param count 移除的数量
     */
    fun removeItem(player: Entity, itemPrefab: Entity, count: Int) {
        require(count > 0) { "移除物品数量必须大于0" }

        val query = world.query {
            object : EntityQueryContext(this) {
                val owner: Entity? get() = getRelationUp<OwnedBy>()
                val amount by component<Amount?>()
                override fun FamilyMatcher.FamilyBuilder.configure() {
                    component<Item>()
                    relation<OwnedBy>(player)
                }
            }
        }

        // 查找玩家拥有的该物品
        val itemsToConsume = mutableListOf<Pair<Entity, Int>>()
        query.forEach {
            val currentItemPrefab = entity.prefab
            if (currentItemPrefab == itemPrefab) {
                val itemAmount = amount?.value ?: 1
                itemsToConsume.add(entity to itemAmount)
            }
        }

        // 计算总数量
        val totalAmount = itemsToConsume.sumOf { it.second }
        require(totalAmount >= count) {
            "玩家${player.id}物品不足，需要: $count, 拥有: $totalAmount"
        }

        // 消耗物品
        var remainingAmount = count
        for ((itemEntity, itemAmount) in itemsToConsume) {
            if (remainingAmount <= 0) break
            val consumeAmount = minOf(remainingAmount, itemAmount)

            if (itemEntity.hasTag<Stackable>() && itemAmount > consumeAmount) {
                // 部分消耗（可堆叠物品）
                world.entity(itemEntity) {
                    it.addComponent(Amount(itemAmount - consumeAmount))
                }
            } else {
                // 完全消耗
                world.destroy(itemEntity)
            }
            remainingAmount -= consumeAmount
        }
    }

    /**
     * 批量消耗物品（用于配方执行等场景）
     * @param player 玩家实体
     * @param items 物品映射，key为物品预制体，value为需要消耗的数量
     */
    fun consumeItems(player: Entity, items: Map<Entity, Int>) {
        // 先验证所有物品是否足够
        items.forEach { (itemPrefab, count) ->
            require(hasEnoughItems(player, itemPrefab, count)) {
                "玩家${player.id}物品不足，需要: $count, 拥有: ${getItemCount(player, itemPrefab)}"
            }
        }

        // 然后消耗所有物品
        items.forEach { (itemPrefab, count) ->
            removeItem(player, itemPrefab, count)
        }
    }

    /**
     * 一键整理背包，合并相同预制体的可堆叠物品
     * @param player 玩家实体
     */
    fun organizeInventory(player: Entity) {
        val query = world.singleQuery {
            object : EntityQueryContext(this) {
                val amount by component<Amount?>()
                override fun FamilyMatcher.FamilyBuilder.configure() {
                    component<Item>()
                    relation<OwnedBy>(player)
                }
            }
        }

        // 按预制体分组收集物品信息（先收集所有信息，避免在删除过程中查询变化）
        val itemsByPrefab = mutableMapOf<Entity, MutableList<Pair<Entity, Int>>>()
        query.forEach {
            val prefab = entity.prefab ?: return@forEach
            val itemAmount = amount?.value ?: 1
            itemsByPrefab.getOrPut(prefab) { mutableListOf() }.add(entity to itemAmount)
        }

        // 收集需要删除的实体和需要创建的物品信息
        val entitiesToDestroy = mutableListOf<Entity>()
        val itemsToCreate = mutableListOf<Pair<Entity, Int>>()

        // 处理每个预制体的物品
        itemsByPrefab.forEach { (prefab, items) ->
            // 只处理可堆叠物品
            if (!prefab.hasTag<Stackable>()) {
                return@forEach
            }

            // 如果只有一个实例，无需整理
            if (items.size <= 1) {
                return@forEach
            }

            // 计算总数量
            val totalAmount = items.sumOf { it.second }
            if (totalAmount <= 0) {
                return@forEach
            }

            // 收集需要删除的实体
            items.forEach { (itemEntity, _) ->
                entitiesToDestroy.add(itemEntity)
            }

            // 收集需要创建的物品信息
            itemsToCreate.add(prefab to totalAmount)
        }

        // 先删除所有旧实例（使用批量删除）
        if (entitiesToDestroy.isNotEmpty()) {
            val entitiesToDelete = Entities()
            entitiesToDestroy.forEach { entity ->
                if (world.isActive(entity)) {
                    entitiesToDelete.add(entity)
                }
            }
            if (entitiesToDelete.size > 0) {
                world.destroy(entitiesToDelete)
                // 执行删除任务，确保删除操作完成
                world.update(0.seconds)
                // 验证删除是否完成
                entitiesToDelete.forEach { entity ->
                    require(!world.isActive(entity)) {
                        "实体${entity.id}应该已被删除，但删除操作可能未完成"
                    }
                }
            }
        }

        // 然后创建合并后的新实例
        itemsToCreate.forEach { (prefab, totalAmount) ->
            itemService.item(prefab) {
                it.addRelation<OwnedBy>(player)
                it.addComponent(Amount(totalAmount))
            }
        }
    }
}