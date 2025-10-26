package cn.jzl.ecs.v2

import cn.jzl.datastructure.signal.Signal
import cn.jzl.di.instance

class EntityService(private val world: World) {
    private val entityStore by world.instance<EntityStore>()
    private val entityUpdateContext by world.instance<EntityUpdateContext>()
    private val componentService by world.instance<ComponentService>()

    val entities: Sequence<Entity> get() = entityStore.entities
    val size: Int get() = entityStore.size

    val onEntityCreate = Signal<Entity>()
    val onEntityUpdate = Signal<Entity>()
    val onEntityDestroy = Signal<Entity>()

    operator fun contains(entity: Entity): Boolean = entity in entityStore

    fun create(configuration: EntityCreateContext.(Entity) -> Unit): Entity {
        return postCreate(entityStore.create(), configuration)
    }

    fun create(entityId: Int, configuration: EntityCreateContext.(Entity) -> Unit): Entity {
        return postCreate(entityStore.create(entityId), configuration)
    }

    private inline fun postCreate(entity: Entity, configuration: EntityCreateContext.(Entity) -> Unit): Entity {
        entityUpdateContext.configuration(entity)
        onEntityCreate(entity)
        return entity
    }

    fun configure(entity: Entity, configuration: EntityUpdateContext.(Entity) -> Unit) {
        require(entity in entityStore) { "Entity $entity is not in entityStore" }
        entityUpdateContext.configuration(entity)
        onEntityUpdate(entity)
    }

    operator fun get(entityId: Int): Entity = entityStore[entityId]

    fun remove(entity: Entity): Boolean {
        if (entity !in entityStore) return false
        val componentBits = componentService.componentBits(entity)
        componentBits.forEach { componentIndex ->
            world.componentService.holderOrNull<Any>(componentIndex)?.remove(entity)
        }
        componentBits.clear()
        onEntityDestroy(entity)
        entityStore -= entity
        return true
    }
}