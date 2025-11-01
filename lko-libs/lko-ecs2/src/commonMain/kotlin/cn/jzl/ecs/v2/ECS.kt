@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ecs.v2

import cn.jzl.di.*
import kotlinx.atomicfu.atomic
import org.kodein.type.TypeToken
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

    @PublishedApi
    internal val scheduleService by instance<ScheduleService>()
}

inline fun World.isActive(entity: Entity): Boolean = entity in entityService
inline fun World.remove(entity: Entity) = entityService.remove(entity)
inline fun World.schedule(
    scheduleName: String = "",
    scheduleTaskPriority: ScheduleTaskPriority = ScheduleTaskPriority.NORMAL,
    noinline block: suspend ScheduleScope.() -> Unit
): ScheduleDescriptor = scheduleService.schedule(scheduleName, scheduleTaskPriority, block)

inline fun World.isActive(scheduleDescriptor: ScheduleDescriptor): Boolean {
    return scheduleService.isActive(scheduleDescriptor.schedule)
}

inline fun World.update(delta: Duration): Unit = scheduleService.update(delta)

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
    this bind singleton { new(::ScheduleService) }
    this bind singleton { new(::ScheduleDispatcherImpl) }
}



