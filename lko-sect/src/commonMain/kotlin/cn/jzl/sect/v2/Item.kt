package cn.jzl.sect.v2

import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityCreateContext
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.FamilyMatcher
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.components
import cn.jzl.ecs.instanceOf
import cn.jzl.ecs.prefab
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.associatedBy
import cn.jzl.ecs.query.query
import cn.jzl.ecs.relation
import cn.jzl.ecs.relations
import cn.jzl.sect.ecs.core.Named


sealed class Item

sealed class Stackable
sealed class Usable

@JvmInline
value class UnitPrice(val price: Int)

val itemAddon = createAddon("item") {
    injects { this bind singleton { new(::ItemService) } }
    components {
        world.componentId<Item> { it.tag() }
        world.componentId<Stackable> { it.tag() }
        world.componentId<Usable> { it.tag() }
        world.componentId<UnitPrice>()
    }
}

class ItemService(world: World) : EntityRelationContext(world) {

    private val itemPrefabs = world.query { EntityItemPrefabContext(this) }.associatedBy { named }

    fun itemPrefabs(): Sequence<Entity> = itemPrefabs.entities

    fun isItemPrefab(itemPrefab: Entity): Boolean {
        return itemPrefab.hasTag<Item>() && itemPrefab.hasPrefab()
    }

    fun isStackable(item: Entity): Boolean = item.hasTag<Stackable>()

    fun isUsable(item: Entity): Boolean = item.hasTag<Usable>()

    @ECSDsl
    fun itemPrefab(named: Named, block: EntityCreateContext.(Entity) -> Unit): Entity {
        require(named !in itemPrefabs) {}
        return world.prefab {
            it.addComponent(named)
            it.addTag<Item>()
            block(it)
        }
    }

    fun item(itemPrefab: Entity, block: EntityCreateContext.(Entity) -> Unit): Entity {
        require(isItemPrefab(itemPrefab))
        return world.instanceOf(itemPrefab, block)
    }

    private class EntityItemPrefabContext(world: World) : EntityQueryContext(world, true) {

        val named by component<Named>()
        val isStackable by ifRelationExist(relations.component<Stackable>())
        val price by component<UnitPrice?>()

        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Item>())
            relation(relations.component(components.prefab))
        }
    }
}
