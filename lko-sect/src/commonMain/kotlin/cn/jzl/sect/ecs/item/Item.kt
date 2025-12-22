package cn.jzl.sect.ecs.item

import androidx.compose.runtime.mutableStateListOf
import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.observers.exec
import cn.jzl.ecs.observers.observe
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.forEach
import cn.jzl.ecs.query.query
import cn.jzl.sect.ecs.core.Description
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.moneyAddon

/**
 * 物品
 */
sealed class Item

/**
 * 可堆叠物品
 */
sealed class Stackable

@JvmInline
value class Amount(val value: Int)

@JvmInline
value class UnitPrice(val value: Int)

val itemAddon = createAddon("item", {}) {
    install(moneyAddon)
    injects {
        this bind singleton { new(::ItemService) }
    }
    components {
        world.componentId<Item> { it.tag() }
        world.componentId<Stackable> { it.tag() }
        world.componentId<Amount>()
        world.componentId<UnitPrice>()
    }
    entities {
        val itemService by world.di.instance<ItemService>()

        itemService.itemPrefab(Named("亚麻")) {
            it.addComponent(Description("较为耐用的麻布，可以制作一些不错的衣物。"))
            it.addComponent(UnitPrice(60))
            it.addTag<Stackable>()
        }
        itemService.itemPrefab(Named("精铁剑")) {
            it.addComponent(Description("用精铁打造的长剑，坚硬耐用"))
            it.addComponent(UnitPrice(200))
        }
    }
}

class ItemService(world: World) : EntityRelationContext(world) {

    @PublishedApi
    internal val nameToItemPrefabs = mutableMapOf<Named, Entity>()

    val itemPrefabs = mutableStateListOf<Entity>()

    init {
        val query = world.query { ItemQueryContext(this) }
        query.forEach { addItemPrefab(name, entity) }
        world.observe<Components.OnInserted>().involving<Item>().exec(query) {
            addItemPrefab(it.name, entity)
        }
        world.observe<Components.OnRemoved>().involving<Item>().exec(query) {
            removeItemPrefab(it.name)
        }
    }

    private fun addItemPrefab(named: Named, entity: Entity) {
        nameToItemPrefabs[named] = entity
        itemPrefabs.add(entity)
    }

    private fun removeItemPrefab(named: Named) {
        val entity = nameToItemPrefabs.remove(named)
        if (entity != null) {
            itemPrefabs.remove(entity)
        }
    }

    @ECSDsl
    inline fun itemPrefab(named: Named, block: EntityCreateContext.(Entity) -> Unit = {}): Entity = world.prefab {
        block(it)
        it.addTag<Item>()
        it.addComponent(named)
    }

    @ECSDsl
    inline fun itemPrefab(name: String, block: EntityCreateContext.(Entity) -> Unit = {}): Entity = itemPrefab(Named(name), block)

    @ECSDsl
    inline fun item(named: Named, block: EntityCreateContext.(Entity) -> Unit): Entity {
        val itemPrefab = nameToItemPrefabs[named]
        require(itemPrefab != null) { "Item prefab $named not found" }
        return item(itemPrefab, block)
    }

    @ECSDsl
    inline fun item(itemPrefab: Entity, block: EntityCreateContext.(Entity) -> Unit): Entity {
        require(itemPrefab.hasTag<Item>()) { "Entity $itemPrefab is not an item" }
        require(itemPrefab.hasPrefab()) { "Entity $itemPrefab is not a prefab of item" }
        return world.instanceOf(itemPrefab, block)
    }

    @ECSDsl
    inline fun item(name: String, block: EntityCreateContext.(Entity) -> Unit): Entity = item(Named(name), block)

    operator fun get(name: String): Entity? = nameToItemPrefabs[Named(name)]

    /**
     * Returns an existing item prefab by name, or creates it if missing.
     * This prevents duplicate-prefab bugs when multiple callers use the same [Named] key.
     */
    @ECSDsl
    inline fun getOrCreateItemPrefab(named: Named, block: EntityCreateContext.(Entity) -> Unit = {}): Entity {
        val existing = nameToItemPrefabs[named]
        if (existing != null) {
            world.entity(existing) { block(it) }
            return existing
        }
        return itemPrefab(named) { block(it) }
    }

    fun splitItem(item: Entity, count: Int): Entity {
        if (!item.hasTag<Stackable>()) return item
        val amount = item.getComponent<Amount?>() ?: return item
        require(amount.value >= count) { "物品${item.id}数量不足" }
        val owner = item.getRelationUp<OwnedBy>()
        require(owner != null) { "物品${item.id}没有所有者" }
        val itemPrefab = item.prefab
        require(itemPrefab != null) { "物品${item.id}没有预制体" }
        world.entity(item) { it.addComponent(Amount(amount.value - count)) }
        return item(itemPrefab) {
            it.addComponent(Amount(count))
            it.addRelation<OwnedBy>(owner)
        }
    }

    fun transferItems(receiver: Entity, items: Map<Entity, Int>) = items.forEach { transferItem(receiver, it.key, it.value) }

    fun transferItem(receiver: Entity, item: Entity, count: Int) = world.entity(item) {
        val owner = it.getRelationUp<OwnedBy>()
        require(owner != null) { "物品${it.id}没有所有者" }
        if (owner == receiver) return@entity
        val amount = it.getComponent<Amount?>()
        if (it.hasTag<Stackable>() && amount != null && amount.value >= count) {
            val itemPrefab = it.prefab
            require(itemPrefab != null) { "物品${it.id}没有预制体" }
            item(itemPrefab) { newItem ->
                newItem.addRelation<OwnedBy>(receiver)
                newItem.addComponent(Amount(count))
            }
            it.addComponent(Amount(amount.value - count))
        } else {
            it.addRelation<OwnedBy>(receiver)
        }
    }

    private class ItemQueryContext(world: World) : EntityQueryContext(world, true) {
        val name by component<Named>()
        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Item>())
        }
    }
}
