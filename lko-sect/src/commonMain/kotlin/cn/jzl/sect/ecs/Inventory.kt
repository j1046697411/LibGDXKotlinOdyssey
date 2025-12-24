package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.*
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

    // QueryGroupedBy is not guaranteed to stay consistent across entity destroy/transfer in this ECS,
    // so avoid caching it per owner.

    private fun getOwnerItems(owner: Entity): QueryGroupedBy<Entity, EntityItemContext> {
        return world.query { EntityItemContext(this, owner) }.groupedBy { itemPrefab }
    }

    private fun getOwnerItemsByItemPrefab(owner: Entity, itemPrefab: Entity): QueryGroupedBy.QueryGroup<Entity, EntityItemContext>? {
        return getOwnerItems(owner)[itemPrefab]
    }

    private fun checkItemPrefab(itemPrefab: Entity) {
        require(itemService.isItemPrefab(itemPrefab))
    }

    fun getAllItems(owner: Entity): Query<EntityItemContext> {
        return getOwnerItems(owner).query
    }

    fun getItemCount(owner: Entity, itemPrefab: Entity): Int {
        checkItemPrefab(itemPrefab)
        return getOwnerItemsByItemPrefab(owner, itemPrefab)?.sumBy { amount?.value ?: 1 } ?: 0
    }

    fun getItem(owner: Entity, itemPrefab: Entity) : Entity? {
        return getOwnerItemsByItemPrefab(owner, itemPrefab)?.map { entity }?.firstOrNull()
    }

    fun getItems(owner: Entity, itemPrefab: Entity): QueryStream<EntityItemContext>? {
        checkItemPrefab(itemPrefab)
        return getOwnerItemsByItemPrefab(owner, itemPrefab)
    }

    fun hasEnoughItems(owner: Entity, itemPrefab: Entity, count: Int): Boolean = getItemCount(owner, itemPrefab) >= count

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
