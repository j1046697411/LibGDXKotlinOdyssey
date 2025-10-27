@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ecs.v2

import cn.jzl.di.*
import kotlinx.atomicfu.atomic
import org.kodein.type.TypeToken
import kotlin.coroutines.Continuation
import kotlin.time.Duration

interface Component<C : Component<C>> {
    val type: ComponentType<C>
    fun World.onAttach(entity: Entity): Unit = Unit
    fun World.onDetach(entity: Entity): Unit = Unit
}

class World(override val di: DI) : DIAware by di {

    @PublishedApi
    internal val entityUpdateContext by instance<EntityUpdateContext>()

    @PublishedApi
    internal val componentService by instance<ComponentService>()

    @PublishedApi
    internal val entityService by instance<EntityService>()

    @PublishedApi
    internal val familyService by instance<FamilyService>()
}

inline fun World.isActive(entity: Entity): Boolean = entity in entityService
inline fun World.family(noinline configuration: FamilyDefinition.() -> Unit): Family = familyService.family(configuration)
inline fun World.create(noinline configuration: EntityCreateContext.(Entity) -> Unit): Entity = entityService.create(configuration)
inline fun World.create(entityId: Int, noinline configuration: EntityCreateContext.(Entity) -> Unit): Entity = entityService.create(entityId, configuration)
inline fun World.configure(entity: Entity, noinline configuration: EntityUpdateContext.(Entity) -> Unit) = entityService.configure(entity, configuration)
inline fun World.remove(entity: Entity) = entityService.remove(entity)

internal val idGenerator = atomic(0)

abstract class ComponentType<T> {
    val index: Int = idGenerator.getAndIncrement()
}

abstract class EntityTag : ComponentType<Boolean>()

fun world(configuration: DIMainBuilder.() -> Unit): World {
    val di = DI {
        module(coreModule)
        configuration()
    }
    val world by di.instance<World>()
    return world
}

const val TAG_CAPACITY = "ecs_capacity"

private val coreModule = module(TypeToken.Any) {
    this bind singleton { World(di) }
    this bind singleton { EntityStoreImpl(instanceOrNull(TAG_CAPACITY) ?: 1024) }
    this bind singleton { new(::ComponentService) }
    this bind singleton { new(::EntityUpdateContextImpl) }
    this bind singleton { new(::EntityService) }
    this bind singleton { new(::FamilyService) }
}



