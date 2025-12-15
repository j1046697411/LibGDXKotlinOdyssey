package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Components
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityCreateContext
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.FamilyMatcher
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.destroy
import cn.jzl.ecs.entity
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.QueryGroupedBy
import cn.jzl.ecs.query.QueryStream
import cn.jzl.ecs.query.collectWhile
import cn.jzl.ecs.query.groupedBy
import cn.jzl.ecs.query.query
import cn.jzl.ecs.query.sumBy
import cn.jzl.ecs.relation
import cn.jzl.ecs.relations
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.core.coreAddon
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.getValue
import kotlin.math.min


@JvmInline
value class Amount(val value: Int) {
    companion object {
        val zero = Amount(0)
        val one = Amount(1)
    }
}

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

class InventoryService(world: World) : EntityRelationContext(world) {

    private val itemService by world.di.instance<ItemService>()

    private val ownerItems = mutableMapOf<Entity, QueryGroupedBy<Entity, EntityItemContext>>()

    private fun getOwnerItems(owner: Entity): QueryGroupedBy<Entity, EntityItemContext> {
        return ownerItems.getOrPut(owner) {
            world.query { EntityItemContext(this, owner) }.groupedBy { itemPrefab }
        }
    }

    private fun getOwnerItemsByItemPrefab(owner: Entity, itemPrefab: Entity): QueryGroupedBy.QueryGroup<Entity, EntityItemContext>? {
        return getOwnerItems(owner)[itemPrefab]
    }

    private fun checkItemPrefab(itemPrefab: Entity) {
        require(itemService.isItemPrefab(itemPrefab))
    }

    fun getAllItems(owner: Entity): QueryStream<EntityItemContext> {
        return getOwnerItems(owner).query
    }

    fun getItemCount(owner: Entity, itemPrefab: Entity): Int {
        checkItemPrefab(itemPrefab)
        return getOwnerItemsByItemPrefab(owner, itemPrefab)?.sumBy { amount?.value ?: 1 } ?: 0
    }

    fun hasEnoughItems(owner: Entity, itemPrefab: Entity, count: Int): Boolean = getItemCount(owner, itemPrefab) >= count

    fun addItem(owner: Entity, itemPrefab: Entity, count: Int) {
        checkItemPrefab(itemPrefab)
        require(count > 0)
        if (itemService.isStackable(itemPrefab)) {
            itemService.item(itemPrefab) {
                it.addRelation<OwnedBy>(owner)
                it.addComponent(Amount(count))
            }
        } else {
            repeat(count) {
                itemService.item(itemPrefab) {
                    it.addRelation<OwnedBy>(owner)
                }
            }
        }
    }

    fun removeItem(owner: Entity, itemPrefab: Entity, count: Int) {
        checkItemPrefab(itemPrefab)
        require(count > 0)
        val query = getOwnerItemsByItemPrefab(owner, itemPrefab)
        requireNotNull(query) {}
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

    fun consumeItems(owner: Entity, items: Map<Entity, Int>) {
        items.forEach { (itemPrefab, count) ->
            checkItemPrefab(itemPrefab)
            require(hasEnoughItems(owner, itemPrefab, count))
        }
        items.forEach { (itemPrefab, count) ->
            removeItem(owner, itemPrefab, count)
        }
    }

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
