package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.*
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.core.coreAddon
import cn.jzl.sect.ecs.item.*
import kotlin.math.min

val inventoryAddon = createAddon("inventory") {
    install(coreAddon)
    install(itemAddon)
    injects { this bind singleton { new(::InventoryService) } }
}

class InventoryService(world: World) : EntityRelationContext(world) {

    private val itemService by world.di.instance<ItemService>()

    private val inventories = mutableMapOf<Entity, QueryGroupedBy<Entity, EntityInventoryContext>>()

    private fun getGroupedByItemPrefab(owner: Entity): QueryGroupedBy<Entity, EntityInventoryContext> {
        return inventories.getOrPut(owner) {
            world.query {
                EntityInventoryContext(this, owner)
            }.groupedBy { itemPrefab }
        }
    }

    fun getAllItems(owner: Entity): Sequence<Entity> = getGroupedByItemPrefab(owner).query.entities

    /**
     * 获取玩家拥有的特定物品数量（按预制体统计）
     * @param owner 玩家实体
     * @param itemPrefab 物品预制体
     * @return 该物品的总数量
     */
    fun getItemCount(owner: Entity, itemPrefab: Entity): Int {
        return getGroupedByItemPrefab(owner)[itemPrefab]?.sumBy { amount?.value ?: 1 } ?: 0
    }

    /**
     * 检查玩家是否有足够的物品
     * @param owner 玩家实体
     * @param itemPrefab 物品预制体
     * @param count 需要的数量
     * @return 是否有足够的物品
     */
    fun hasEnoughItems(owner: Entity, itemPrefab: Entity, count: Int): Boolean = getItemCount(owner, itemPrefab) >= count

    /**
     * 添加物品到玩家背包
     * @param owner 玩家实体
     * @param itemPrefab 物品预制体
     * @param count 添加的数量
     */
    fun addItem(owner: Entity, itemPrefab: Entity, count: Int) {
        require(count > 0) { "添加物品数量必须大于0" }
        // 检查是否为可堆叠物品
        val isStackable = itemPrefab.hasTag<Stackable>()
        if (isStackable && count > 1) {
            // 可堆叠物品，创建一个带有数量的物品实例
            itemService.item(itemPrefab) {
                it.addRelation<OwnedBy>(owner)
                it.addComponent(Amount(count))
            }
        } else {
            // 不可堆叠物品或数量为1，创建多个单独的物品实例
            repeat(count) {
                itemService.item(itemPrefab) {
                    it.addRelation<OwnedBy>(owner)
                }
            }
        }
    }

    /**
     * 从玩家背包移除指定数量的物品
     * @param owner 玩家实体
     * @param itemPrefab 物品预制体
     * @param count 移除的数量
     */
    fun removeItem(owner: Entity, itemPrefab: Entity, count: Int) {
        require(count > 0) { "移除物品数量必须大于0" }
        val items = getGroupedByItemPrefab(owner)[itemPrefab]
        require(items != null) { "玩家${owner.id}没有物品预制体${itemPrefab.id}" }
        val itemTotalCount = items.sumBy { amount?.value ?: 1 }
        require(itemTotalCount >= count) { "玩家${owner.id}物品不足，需要: $count, 拥有: $itemTotalCount" }
        var remaining = count
        items.collectWhile {
            val itemAmount = amount?.value ?: 1
            val consumeAmount = min(itemAmount, remaining)
            if (isStackable && itemAmount > consumeAmount) {
                amount = Amount(itemAmount - consumeAmount)
            } else {
                world.destroy(entity)
            }
            remaining -= consumeAmount
            remaining <= 0
        }
    }

    /**
     * 批量消耗物品（用于配方执行等场景）
     * @param owner 玩家实体
     * @param items 物品映射，key为物品预制体，value为需要消耗的数量
     */
    fun consumeItems(owner: Entity, items: Map<Entity, Int>) {
        // 先验证所有物品是否足够
        items.forEach { (itemPrefab, count) ->
            require(hasEnoughItems(owner, itemPrefab, count)) {
                "玩家${owner.id}物品不足，需要: $count, 拥有: ${getItemCount(owner, itemPrefab)}"
            }
        }

        // 然后消耗所有物品
        items.forEach { (itemPrefab, count) ->
            removeItem(owner, itemPrefab, count)
        }
    }

    private class EntityInventoryContext(world: World, private val owner: Entity) : EntityQueryContext(world) {

        var amount by component<Amount?>()
        val isStackable by ifRelationExist(relations.component<Stackable>())
        val itemPrefab by prefab()

        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<Item>()
            relation(relations.relation<OwnedBy>(owner))
        }
    }
}
