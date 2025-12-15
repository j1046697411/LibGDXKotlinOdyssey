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

    private fun getOwnerItems(owner: Entity): QueryGroupedBy<Entity, EntityInventoryContext> {
        return inventories.getOrPut(owner) {
            world.query {
                EntityInventoryContext(this, owner)
            }.groupedBy { itemPrefab }
        }
    }

    private fun getOwnerItemByItemPrefab(owner: Entity, itemPrefab: Entity): QueryGroupedBy.QueryGroup<Entity, EntityInventoryContext>? {
        return getOwnerItems(owner)[itemPrefab]
    }

    fun getAllItems(owner: Entity): Sequence<Entity> = getOwnerItems(owner).query.entities

    fun getAllQueryItems(owner: Entity): QueryStream<EntityInventoryContext> = getOwnerItems(owner).query

    /**
     * 获取玩家拥有的特定物品数量（按预制体统计）
     * @param owner 玩家实体
     * @param itemPrefab 物品预制体
     * @return 该物品的总数量
     */
    fun getItemCount(owner: Entity, itemPrefab: Entity): Int {
        return getOwnerItemByItemPrefab(owner, itemPrefab)?.sumBy { amount?.value ?: 1 } ?: 0
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
        val items = getOwnerItems(owner)[itemPrefab]
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

    /**
     * 转移物品到另一个玩家（按物品实体转移）
     * @param receiver 接收物品的玩家实体
     * @param item 要转移的物品实体
     * @param count 转移的数量
     */
    @ECSDsl
    fun transferItem(receiver: Entity, item: Entity, count: Int, block: EntityCreateContext.(Entity) -> Unit = {}) {
        val itemPrefab = item.prefab
        require(itemPrefab != null) { "物品${item.id}没有预制体" }
        require(item.getRelationUp<OwnedBy>() == receiver) { "物品${item.id}不是玩家${receiver.id}所有" }
        val amount = item.getComponent<Amount?>()?.value ?: 1
        require(amount >= count) { "玩家${receiver.id}物品不足，需要: $count, 拥有: $amount" }
        if (amount == count) {
            world.entity(item) {
                it.addRelation<OwnedBy>(receiver)
                block(it)
            }
            return
        }
        world.entity(item) { it.addComponent(Amount(amount - count)) }
        itemService.item(itemPrefab) {
            it.addRelation<OwnedBy>(receiver)
            it.addComponent(Amount(count))
            block(it)
        }
    }

    /**
     * 转移物品到另一个玩家（按预制体和数量转移）
     * @param provider 提供物品的玩家实体
     * @param receiver 接收物品的玩家实体
     * @param itemPrefab 物品预制体
     * @param count 转移的数量
     */
    fun transferItem(provider: Entity, receiver: Entity, itemPrefab: Entity, count: Int) {
        val items = getOwnerItemByItemPrefab(provider, itemPrefab)
        require(items != null) { "玩家${provider.id}没有物品预制体${itemPrefab.id}" }
        require(items.sumBy { amount?.value ?: 1 } >= count) { "玩家${provider.id}物品不足，需要: $count, 拥有: ${items.sumBy { amount?.value ?: 1 }}" }
        var remaining = count
        items.collectWhile {
            val itemAmount = amount?.value ?: 1
            val transferAmount = min(itemAmount, remaining)
            if (isStackable && itemAmount > transferAmount) {
                itemService.item(itemPrefab) {
                    it.addRelation<OwnedBy>(receiver)
                    it.addComponent(Amount(transferAmount))
                }
                amount = Amount(itemAmount - transferAmount)
            } else {
                world.entity(entity) {
                    it.addRelation<OwnedBy>(receiver)
                }
            }
            remaining -= transferAmount
            remaining <= 0
        }
    }

    class EntityInventoryContext(world: World, val owner: Entity) : EntityQueryContext(world) {
        var amount by component<Amount?>()
        val isStackable by ifRelationExist(relations.component<Stackable>())
        val itemPrefab by prefab()

        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<Item>()
            relation(relations.relation<OwnedBy>(owner))
        }
    }
}
