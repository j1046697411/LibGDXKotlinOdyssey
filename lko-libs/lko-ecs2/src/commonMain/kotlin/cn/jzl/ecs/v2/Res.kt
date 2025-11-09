package cn.jzl.ecs.v2

import cn.jzl.di.TagAll
import cn.jzl.di.instance
import cn.jzl.di.module
import cn.jzl.di.new
import cn.jzl.di.prototype
import cn.jzl.di.singleton
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import org.kodein.type.TypeToken
import kotlin.jvm.JvmInline
import kotlin.reflect.KProperty

@ScheduleDsl
sealed interface Resource<T> {
    val index: Int
    val value: T
}

sealed interface MutableResource<T> : Resource<T> {
    override var value: T
}

@JvmInline
value class ImmutableResource<T>(private val resource: Resource<T>) : Resource<T> by resource

@PublishedApi
internal data class ActualResource<T>(override val index: Int, override var value: T) : Resource<T>, MutableResource<T>

operator fun <T : Any> Resource<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value
operator fun <T : Any> MutableResource<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
}

@PublishedApi
internal class ResourceService(val world: World) {

    @PublishedApi
    internal val lock = ReentrantLock()

    @PublishedApi
    internal val resources = mutableMapOf<Any, ActualResource<*>>()

    @PublishedApi
    internal fun getResource(value: Any): ActualResource<*> {
        return resources[value] ?: lock.withLock {
            resources.getOrPut(value) { ActualResource(resources.size, value) }
        }
    }
}

inline fun <reified T : Any> ScheduleScope.resource(tag: Any? = null): ImmutableResource<T> {
    val resource: Resource<T> by world.instance(tag)
    scheduleDescriptor.addResource(resource)
    return ImmutableResource(resource)
}

inline fun <reified T : Any> ScheduleScope.mutableResource(tag: Any? = null): MutableResource<T> {
    val resource: MutableResource<T> by world.instance(tag)
    scheduleDescriptor.addResource(resource, true)
    return resource
}

suspend inline fun <T : Any, R> ScheduleScope.batch(
    resource: MutableResource<T>,
    schedulePriority: SchedulePriority = SchedulePriority.Auto,
    crossinline config: Resource<T>.() -> R
): R {
    check(resource.index in scheduleDescriptor.writeResources) { "resource must be writeable" }
    return suspendScheduleCoroutine(dispatcherType = ScheduleScope.DispatcherType.Work) { continuation ->
        continuation.scheduleDispatcher.addWorkTask(scheduleDescriptor, schedulePriority) {
            continuation.resumeWith(runCatching { resource.config() })
        }
    }
}

internal val resourceModule = module(TypeToken.Any, "resourceModule") {
    this bind singleton { new(::ResourceService) }
    this bind prototype<Any, ActualResource<*>>(TagAll) {
        val resourceService = instance<ResourceService>()
        val key = node.key

        @Suppress("UNCHECKED_CAST")
        val valueType: TypeToken<Any> = key.targetType.getGenericParameters().first() as TypeToken<Any>
        resourceService.getResource(this[key.argType, valueType, key.tag].invoke(Unit))
    }
}