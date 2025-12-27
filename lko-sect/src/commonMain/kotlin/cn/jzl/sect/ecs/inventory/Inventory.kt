package cn.jzl.sect.ecs.inventory

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.*
import cn.jzl.sect.ecs.item.Item
import cn.jzl.sect.ecs.ItemActionProvider
import cn.jzl.sect.ecs.item.ItemService
import cn.jzl.sect.ecs.ItemStateResolverRegistry
import cn.jzl.sect.ecs.item.Stackable
import cn.jzl.sect.ecs.item.Usable
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.core.coreAddon
import cn.jzl.sect.ecs.item.itemAddon
import cn.jzl.sect.ecs.planning.planning
import kotlin.math.min

/**
 * 库存系统包，包含库存组件、服务和addon配置
 * 
 * 主要功能：
 * 1. 定义物品数量组件
 * 2. 提供物品管理服务
 * 3. 支持物品添加、移除和转移
 * 4. 实现物品堆叠和消耗机制
 * 5. 支持物品查询和计数
 */

/**
 * 物品数量组件
 * 用于表示物品的数量
 * 
 * @param value 物品数量值
 */
@JvmInline
value class Amount(val value: Int) {
    /**
     * 伴生对象，提供常量定义
     */
    companion object {
        /** 零数量 */
        val zero = Amount(0)
        /** 一个数量 */
        val one = Amount(1)
    }
}

/**
 * 库存系统addon
 * 注册库存相关组件和服务
 */
val inventoryAddon = createAddon("inventory") {
    install(coreAddon)
    install(itemAddon)
    injects { this bind singleton { new(::InventoryService) } }
    components { 
        world.componentId<Amount>()
    }
    planning {
        register(ItemActionProvider(world))
        register(ItemStateResolverRegistry(world))
    }
}

/**
 * 库存服务
 * 管理实体的物品库存
 * 
 * @param world ECS世界实例
 */
class InventoryService(world: World) : EntityRelationContext(world) {

    private val itemService by world.di.instance<ItemService>()
    private val ownerItemQueries = mutableMapOf<Entity, Query<EntityItemContext>>()

    // QueryGroupedBy is not guaranteed to stay consistent across entity destroy/transfer in this ECS,
    // so avoid caching it per owner.

    /**
     * 获取所有者的物品查询
     * 
     * @param owner 所有者实体
     * @return 物品查询对象
     */
    private fun getOwnerItems(owner: Entity): Query<EntityItemContext> {
        return ownerItemQueries.getOrPut(owner) { world.query { EntityItemContext(this, owner) } }
    }

    /**
     * 根据物品预制体获取所有者的物品
     * 
     * @param owner 所有者实体
     * @param itemPrefab 物品预制体
     * @return 物品查询流
     */
    private fun getOwnerItemsByItemPrefab(owner: Entity, itemPrefab: Entity): QueryStream<EntityItemContext> {
        return getOwnerItems(owner).filter { this.itemPrefab == itemPrefab }
    }

    /**
     * 检查实体是否为物品预制体
     * 
     * @param itemPrefab 要检查的实体
     * @throws IllegalArgumentException 如果不是物品预制体
     */
    private fun checkItemPrefab(itemPrefab: Entity) {
        require(itemService.isItemPrefab(itemPrefab))
    }

    /**
     * 获取所有者的所有物品
     * 
     * @param owner 所有者实体
     * @return 物品查询对象
     */
    fun getAllItems(owner: Entity): Query<EntityItemContext> = getOwnerItems(owner)

    /**
     * 获取所有者的特定物品数量
     * 
     * @param owner 所有者实体
     * @param itemPrefab 物品预制体
     * @return 物品数量
     */
    fun getItemCount(owner: Entity, itemPrefab: Entity): Int {
        checkItemPrefab(itemPrefab)
        return getOwnerItemsByItemPrefab(owner, itemPrefab).sumBy { amount?.value ?: 1 }
    }

    /**
     * 获取所有者的特定物品实例
     * 
     * @param owner 所有者实体
     * @param itemPrefab 物品预制体
     * @return 物品实体，可为空
     */
    fun getItem(owner: Entity, itemPrefab: Entity) : Entity? {
        return getOwnerItemsByItemPrefab(owner, itemPrefab).map { entity }.firstOrNull()
    }

    /**
     * 获取所有者的特定物品集合
     * 
     * @param owner 所有者实体
     * @param itemPrefab 物品预制体
     * @return 物品查询流
     */
    fun getItems(owner: Entity, itemPrefab: Entity): QueryStream<EntityItemContext> {
        checkItemPrefab(itemPrefab)
        return getOwnerItemsByItemPrefab(owner, itemPrefab)
    }

    /**
     * 检查所有者是否拥有足够数量的物品
     * 
     * @param owner 所有者实体
     * @param itemPrefab 物品预制体
     * @param count 所需数量
     * @return 是否拥有足够数量
     */
    fun hasEnoughItems(owner: Entity, itemPrefab: Entity, count: Int): Boolean = getItemCount(owner, itemPrefab) >= count

    /**
     * 向所有者添加物品
     * 
     * @param owner 所有者实体
     * @param itemPrefab 物品预制体
     * @param count 添加数量
     * @return 添加的物品实体序列
     */
    fun addItem(owner: Entity, itemPrefab: Entity, count: Int): Sequence<Entity> {
        checkItemPrefab(itemPrefab)
        require(count > 0)
        return if (itemService.isStackable(itemPrefab)) {
            val item = getItem(owner, itemPrefab)
            sequenceOf(
                if (item != null) {
                world.entity(item) {
                    it.addComponent(Amount(it.getComponent<Amount>().value + count))
                }
                item
            } else {
                itemService.item(itemPrefab) {
                    it.addRelation<OwnedBy>(owner)
                    it.addComponent(Amount(count))
                }
            })
        } else {
            val entities = mutableListOf<Entity>()
            repeat(count) {
                entities += itemService.item(itemPrefab) {
                    it.addRelation<OwnedBy>(owner)
                }
            }
            entities.asSequence()
        }
    }

    /**
     * 从所有者移除物品
     * 
     * @param owner 所有者实体
     * @param itemPrefab 物品预制体
     * @param count 移除数量
     * @throws IllegalArgumentException 如果没有足够的物品移除
     */
    fun removeItem(owner: Entity, itemPrefab: Entity, count: Int) {
        checkItemPrefab(itemPrefab)
        require(count > 0)
        val query = getOwnerItemsByItemPrefab(owner, itemPrefab)
        requireNotNull(query) {}

        // For non-stackable items, destroying while iterating can mutate the group and skip entities.
        if (!itemService.isStackable(itemPrefab)) {
            val toDestroy = mutableListOf<Entity>()
            query.collectWhile {
                toDestroy += entity
                toDestroy.size >= count
            }
            require(toDestroy.size >= count) { "Not enough items to remove" }
            toDestroy.forEach { world.destroy(it) }
            return
        }

        var remaining = count
        query.collectWhile {
            val itemAmount = amount?.value ?: 1
            val consumeAmount = min(itemAmount, remaining)
            if (isStackable && itemAmount > consumeAmount) {
                amount = Amount(itemAmount - consumeAmount)
            } else {
                amount = Amount.zero
                world.destroy(entity)
            }
            remaining -= consumeAmount
            remaining <= 0
        }
    }

    /**
     * 消耗所有者的多种物品
     * 
     * @param owner 所有者实体
     * @param items 物品及其数量的映射
     * @throws IllegalArgumentException 如果没有足够的物品消耗
     */
    fun consumeItems(owner: Entity, items: Map<Entity, Int>) {
        items.forEach { (itemPrefab, count) ->
            checkItemPrefab(itemPrefab)
            require(hasEnoughItems(owner, itemPrefab, count))
        }
        items.forEach { (itemPrefab, count) ->
            removeItem(owner, itemPrefab, count)
        }
    }

    /**
     * 转移物品从提供者到接收者
     * 
     * @param provider 物品提供者
     * @param receiver 物品接收者
     * @param item 要转移的物品
     * @param count 转移数量，可为空表示全部转移
     * @param block 物品配置块，默认空
     */
    @ECSDsl
    fun transferItem(provider: Entity, receiver: Entity, item: Entity, count: Int? = null, block: EntityCreateContext.(Entity) -> Unit = {}) {
        require(provider == item.getRelationUp<OwnedBy>())
        val amount = item.getComponent<Amount?>() ?: Amount.one
        val transferCount = count ?: amount.value
        val itemPrefab = item.prefab
        requireNotNull(itemPrefab)
        require(amount.value >= transferCount)
        if (itemService.isStackable(item) && amount.value > transferCount) {
            itemService.item(itemPrefab) {
                it.addComponent(Amount(transferCount))
                it.addRelation<OwnedBy>(receiver)
                block(it)
            }
            world.entity(item) {
                it.addComponent(Amount(amount.value - transferCount))
            }
        } else {
            world.entity(item) {
                it.addRelation<OwnedBy>(receiver)
                block(it)
            }
        }
    }

    /**
     * 实体物品查询上下文
     * 用于查询实体拥有的物品及其属性
     * 
     * @param world ECS世界实例
     * @param owner 所有者实体
     */
    class EntityItemContext(world: World, val owner: Entity) : EntityQueryContext(world) {
        var amount by component<Amount?>()
        val isStackable by ifRelationExist(relations.component<Stackable>())
        val isUsable by ifRelationExist(relations.component<Usable>())
        val itemPrefab by relationUp<Components.InstanceOf>()

        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.relation<OwnedBy>(owner))
            relation(relations.component<Item>())
        }
    }
}
