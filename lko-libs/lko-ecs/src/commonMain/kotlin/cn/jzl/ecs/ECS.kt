@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ecs

import cn.jzl.di.*
import cn.jzl.ecs.util.Bits
import cn.jzl.ecs.util.Signal
import cn.jzl.ecs.util.bitsOf
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import org.kodein.type.TypeToken
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.coroutines.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalAtomicApi::class)
internal val id = AtomicInt(-1)

abstract class ComponentType<C> {
    @OptIn(ExperimentalAtomicApi::class)
    val index: Int = id.addAndFetch(1)
}

abstract class EntityTag() : ComponentType<Boolean>()

data class Entity(val id: Int, val version: Int)

interface Component<C> {

    val type: ComponentType<C>

    fun World.onAttach(entity: Entity): Unit = Unit

    fun World.onDetach(entity: Entity): Unit = Unit
}

interface EntityStore : Sequence<Entity> {

    val size: Int

    fun create(): Entity

    fun create(entityId: Int): Entity

    operator fun get(entityId: Int): Entity

    operator fun contains(entity: Entity): Boolean

    operator fun minusAssign(entity: Entity)
}

internal class EntityStoreImpl : EntityStore {

    private val recycledEntities = arrayListOf<Entity>()
    private val entities = arrayListOf<Entity>()
    private val activeEntities = bitsOf()

    override val size: Int get() = activeEntities.countOneBits()

    override fun create(): Entity {
        val entity = if (recycledEntities.isNotEmpty()) {
            recycledEntities.removeLast().upgrade()
        } else {
            val entity = Entity(entities.size, 0)
            entities.add(entity)
            entity
        }
        activeEntities.setBit(entity.id)
        entities[entity.id] = entity
        return entity
    }

    override fun create(entityId: Int): Entity {
        if (entityId >= entities.size) {
            for (i in entities.size..entityId) {
                val entity = Entity(i, -1)
                recycledEntities.add(entity)
                entities.add(entity)
            }
        }
        val index = recycledEntities.indexOfLast { it.id == entityId }
        check(index != -1) { "entityId($entityId) is active" }
        val entity = recycledEntities.removeAt(index)
        val newEntity = entity.upgrade()
        entities[newEntity.id] = newEntity
        activeEntities.setBit(entity.id)
        return newEntity
    }

    override fun contains(entity: Entity): Boolean {
        return entity.id in activeEntities && entities[entity.id].version == entity.version
    }

    override fun get(entityId: Int): Entity {
        check(entityId in activeEntities) { "$entityId is not active" }
        return entities[entityId]
    }

    override fun minusAssign(entity: Entity) {
        if (entity in this) {
            recycledEntities.add(entity)
            activeEntities.clearBit(entity.id)
        }
    }

    override fun iterator(): Iterator<Entity> {
        return activeEntities.map { entities[it] }.iterator()
    }

    private fun Entity.upgrade(): Entity {
        return Entity(id, version + 1)
    }
}


class EntityService(@PublishedApi internal val world: World) : Sequence<Entity> {

    val size: Int get() = world.entityStore.size

    val onEntityChanged = Signal<Entity>()
    val onEntityCreated = Signal<Entity>()
    val onEntityRemoved = Signal<Entity>()

    fun contains(entity: Entity): Boolean {
        return entity in world.entityStore
    }

    inline fun create(configuration: EntityCreateContext.(Entity) -> Unit): Entity {
        return postCreate(world.entityStore.create(), configuration)
    }

    inline fun create(
        entityId: Int,
        configuration: EntityCreateContext.(Entity) -> Unit
    ): Entity = postCreate(world.entityStore.create(entityId), configuration)

    @PublishedApi
    internal inline fun postCreate(entity: Entity, configuration: EntityCreateContext.(Entity) -> Unit): Entity {
        world.componentService.componentBits(entity).clearAll()
        world.entityUpdateContext.configuration(entity)
        onEntityCreated(entity)
        return entity
    }

    inline fun configure(entity: Entity, configuration: EntityUpdateContext.(Entity) -> Unit) {
        require(entity in world.entityStore) { "" }
        world.entityUpdateContext.configuration(entity)
        onEntityChanged(entity)
    }

    operator fun get(entityId: Int): Entity = world.entityStore[entityId]

    operator fun minusAssign(entity: Entity) {
        if (entity in world.entityStore) {
            val componentBits = world.componentService.componentBits(entity)
            componentBits.forEach { world.componentService.holderOrNull(it)?.minusAssign(entity) }
            componentBits.clearAll()
            onEntityRemoved(entity)
            world.entityStore -= entity
        }
    }

    override fun iterator(): Iterator<Entity> = world.entityStore.iterator()
}

interface ComponentsHolder<C> {

    val componentType: ComponentType<C>

    operator fun contains(entity: Entity): Boolean

    operator fun get(entity: Entity): C

    fun getOrNull(entity: Entity): C?

    operator fun set(entity: Entity, component: C)

    operator fun minusAssign(entity: Entity)
}

internal class ComponentsHolderImpl<C>(
    private val world: World,
    override val componentType: ComponentType<C>
) : ComponentsHolder<C> {

    private val components = hashMapOf<Int, C>()

    override fun contains(entity: Entity): Boolean {
        return entity.id in components
    }

    override fun get(entity: Entity): C {
        return components[entity.id] ?: throw NullPointerException("${entity.id} $componentType is null")
    }

    override fun getOrNull(entity: Entity): C? = components[entity.id]

    override fun set(entity: Entity, component: C) {
        components.put(entity.id, component)?.onDetach(entity)
        component.onAttach(entity)
    }

    override fun minusAssign(entity: Entity) {
        components.remove(entity.id).onDetach(entity)
    }

    private fun Any?.onAttach(entity: Entity) {
        if (this is Component<*>) world.onAttach(entity)
    }

    private fun Any?.onDetach(entity: Entity) {
        if (this is Component<*>) world.onDetach(entity)
    }
}

internal class EntityTagComponentsHolder(override val componentType: EntityTag) : ComponentsHolder<Boolean> {

    private val bits = bitsOf()

    override fun contains(entity: Entity): Boolean = entity.id in bits

    override fun get(entity: Entity): Boolean = entity.id in bits

    override fun getOrNull(entity: Entity): Boolean = entity.id in bits

    override fun set(entity: Entity, component: Boolean) {
        bits.setBit(entity.id)
    }

    override fun minusAssign(entity: Entity) {
        bits.clearBit(entity.id)
    }
}

interface EntityComponentContext {

    val world: World

    val Entity.componentBits: Bits

    operator fun Entity.contains(componentType: ComponentType<*>): Boolean

    operator fun <C> Entity.get(componentType: ComponentType<C>): C

    fun <C> Entity.getOrNull(componentType: ComponentType<C>): C?
}


interface EntityCreateContext : EntityComponentContext {

    operator fun <C> Entity.set(componentType: ComponentType<C>, component: C)

    fun <C> Entity.getOrPut(componentType: ComponentType<C>, provider: DIProvider<C>): C

    operator fun <C : Component<C>> Entity.plusAssign(component: C)

    operator fun Entity.plusAssign(tag: EntityTag)

    fun Entity.tags(vararg tags: EntityTag)
}

interface EntityUpdateContext : EntityCreateContext {

    operator fun Entity.minusAssign(componentType: ComponentType<*>)

    operator fun <C : Component<C>> Entity.minusAssign(component: Component<C>)
}

@PublishedApi
internal class DebugEntityUpdateContext(
    private val entityUpdateContext: EntityUpdateContext,
    private val entity: Entity
) : EntityUpdateContext by entityUpdateContext {

    private fun Entity.checkEntity() {
        check(this.id == entity.id) { "plusAssign modify entity $id != target entity ${entity.id}" }
    }

    override fun Entity.minusAssign(componentType: ComponentType<*>) {
        checkEntity()
        with(entityUpdateContext) { this@minusAssign -= componentType }
    }

    override fun <C> Entity.set(componentType: ComponentType<C>, component: C) {
        checkEntity()
        with(entityUpdateContext) { this@set[componentType] = component }
    }

    override fun <C> Entity.getOrPut(componentType: ComponentType<C>, provider: DIProvider<C>): C {
        checkEntity()
        return with(entityUpdateContext) { this@getOrPut.getOrPut(componentType, provider) }
    }

    override fun <C : Component<C>> Entity.minusAssign(component: Component<C>) {
        checkEntity()
        with(entityUpdateContext) { this@minusAssign -= component }
    }

    override fun Entity.tags(vararg tags: EntityTag) {
        checkEntity()
        with(entityUpdateContext) { tags(*tags) }
    }

    override fun Entity.plusAssign(tag: EntityTag) {
        checkEntity()
        with(entityUpdateContext) { this@plusAssign += tag }
    }

    override fun <C : Component<C>> Entity.plusAssign(component: C) {
        checkEntity()
        with(entityUpdateContext) { this@plusAssign += component }
    }
}

class EntityUpdateContextImpl(override val world: World) : EntityUpdateContext {

    override val Entity.componentBits: Bits get() = world.componentService.componentBits(this)

    override fun Entity.minusAssign(componentType: ComponentType<*>) {
        world.componentService.holder(componentType) -= this
        componentBits.clearBit(componentType.index)
    }

    override fun <C : Component<C>> Entity.minusAssign(component: Component<C>) {
        world.componentService.holder(component.type) -= this
        componentBits.clearBit(component.type.index)
    }

    override fun <C> Entity.set(componentType: ComponentType<C>, component: C) {
        world.componentService.holder(componentType)[this] = component
        componentBits.setBit(componentType.index)
    }

    override fun <C> Entity.getOrPut(componentType: ComponentType<C>, provider: DIProvider<C>): C {
        val componentHandler = world.componentService.holder(componentType)
        return componentHandler.getOrNull(this) ?: run {
            val component = provider()
            componentHandler[this] = component
            component
        }
    }

    override fun <C : Component<C>> Entity.plusAssign(component: C) {
        world.componentService.holder(component.type)[this] = component
        componentBits.setBit(component.type.index)
    }

    override fun Entity.plusAssign(tag: EntityTag) {
        world.componentService.holder(tag)[this] = true
        componentBits.setBit(tag.index)
    }

    override fun Entity.tags(vararg tags: EntityTag) {
        if (tags.isEmpty()) return
        for (tag in tags) {
            world.componentService.holder(tag)[this] = true
            componentBits.setBit(tag.index)
        }
    }

    override fun Entity.contains(componentType: ComponentType<*>): Boolean {
        return this in world.componentService.holder(componentType)
    }

    override fun <C> Entity.get(componentType: ComponentType<C>): C {
        return world.componentService.holder(componentType)[this]
    }

    override fun <C> Entity.getOrNull(componentType: ComponentType<C>): C? {
        return world.componentService.holder(componentType).getOrNull(this)
    }
}

class ComponentService(val world: World) {

    private val componentBits = hashMapOf<Int, Bits>()
    private val componentsHolders = hashMapOf<Int, ComponentsHolder<*>>()

    private val proxies: List<ComponentsHolderProxy>

    init {
        val proxies by world.allInstance<ComponentsHolderProxy>()
        val mutableProxies = proxies.toMutableList()
        mutableProxies.sortByDescending { if (it is Prioritized) it.priority else 0 }
        this.proxies = mutableProxies
    }

    fun componentBits(entity: Entity): Bits {
        return componentBits.getOrPut(entity.id) { bitsOf() }
    }

    @Suppress("UNCHECKED_CAST")
    fun <C> holder(componentType: ComponentType<C>): ComponentsHolder<C> {
        return componentsHolders.getOrPut(componentType.index) { createComponentsHolder(componentType) } as ComponentsHolder<C>
    }

    private fun <C> createComponentsHolder(componentType: ComponentType<C>): ComponentsHolder<C> {
        val holder = if (componentType is EntityTag) {
            @Suppress("UNCHECKED_CAST")
            EntityTagComponentsHolder(componentType) as ComponentsHolder<C>
        } else {
            ComponentsHolderImpl(world, componentType)
        }
        return if (proxies.isNotEmpty()) proxies.fold(holder) { acc, proxy -> proxy.proxy(acc) } else holder
    }

    fun holderOrNull(index: Int): ComponentsHolder<*>? = componentsHolders[index]
}

interface ComponentsHolderProxy {
    fun <C> proxy(componentsHolder: ComponentsHolder<C>): ComponentsHolder<C>
}

class Family(
    override val world: World,
    val familyDefinition: FamilyDefinition
) : EntityComponentContext by world.entityUpdateContext, Sequence<Entity> {

    private val entityBits = bitsOf()

    val size: Int get() = entityBits.size

    val onInsertedEntity = Signal<Entity>()

    val onRemovedEntity = Signal<Entity>()

    internal fun entityChanges(entity: Entity) {
        when {
            entity.id in entityBits && entity !in this -> remove(entity)
            entity.id !in entityBits && entity in this -> insert(entity)
        }
    }

    private fun insert(entity: Entity) {
        entityBits.setBit(entity.id)
        onInsertedEntity(entity)
    }

    private fun remove(entity: Entity) {
        entityBits.clearBit(entity.id)
        onRemovedEntity(entity)
    }

    operator fun contains(entity: Entity): Boolean {
        return familyDefinition.run { checkEntity(entity) }
    }

    override fun iterator(): Iterator<Entity> {
        return entityBits.map { world.entityService[it] }.iterator()
    }
}


class FamilyDefinition {

    private val allBits = bitsOf()
    private val anyBits = bitsOf()
    private val noneBits = bitsOf()

    fun all(vararg componentTypes: ComponentType<*>): FamilyDefinition = allBits.setComponentTypes(componentTypes)

    fun any(vararg componentTypes: ComponentType<*>): FamilyDefinition = anyBits.setComponentTypes(componentTypes)

    fun none(vararg componentTypes: ComponentType<*>): FamilyDefinition = noneBits.setComponentTypes(componentTypes)

    internal fun Family.checkEntity(entity: Entity): Boolean {
        val componentBits = entity.componentBits
        if (allBits.isNotEmpty() && allBits !in componentBits) return false
        if (noneBits.isNotEmpty() && noneBits in componentBits) return false
        if (anyBits.isNotEmpty() && !anyBits.intersects(componentBits)) return false
        return true
    }

    private fun Bits.setComponentTypes(componentTypes: Array<out ComponentType<*>>): FamilyDefinition {
        if (componentTypes.isEmpty()) {
            return this@FamilyDefinition
        }
        componentTypes.forEach { setBit(it.index) }
        return this@FamilyDefinition
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FamilyDefinition) return false
        if (allBits != other.allBits) return false
        if (anyBits != other.anyBits) return false
        if (noneBits != other.noneBits) return false

        return true
    }

    override fun hashCode(): Int {
        var result = allBits.hashCode()
        result = 31 * result + anyBits.hashCode()
        result = 31 * result + noneBits.hashCode()
        return result
    }
}

class FamilyService(@PublishedApi internal val world: World) : Sequence<Family> {

    @PublishedApi
    internal val families = hashMapOf<FamilyDefinition, Family>()

    init {
        world.entityService.onEntityChanged.add(::entityChanges)
        world.entityService.onEntityRemoved.add(::entityChanges)
        world.entityService.onEntityCreated.add(::entityChanges)
    }

    private fun entityChanges(entity: Entity) {
        if (families.isEmpty()) return
        families.values.forEach { it.entityChanges(entity) }
    }

    inline fun family(configuration: FamilyDefinition.() -> Unit): Family {
        val familyDefinition = FamilyDefinition()
        familyDefinition.configuration()
        return families.getOrPut(familyDefinition) { createFamily(familyDefinition) }
    }

    @PublishedApi
    internal fun createFamily(familyDefinition: FamilyDefinition): Family {
        val family = Family(world, familyDefinition)
        for (entity in world.entityService) family.entityChanges(entity)
        return family
    }

    override fun iterator(): Iterator<Family> = families.values.iterator()
}

class World(override val di: DI) : DIAware by di {
    @PublishedApi
    internal val entityStore by instance<EntityStore>()

    @PublishedApi
    internal val entityService by instance<EntityService>()

    @PublishedApi
    internal val componentService by instance<ComponentService>()

    @PublishedApi
    internal val entityUpdateContext by instance<EntityUpdateContext>()

    @PublishedApi
    internal val familyService by instance<FamilyService>()

    @PublishedApi
    internal val systemService by instance<SystemService>()

    @PublishedApi
    internal val scheduleService by instance<ScheduleService>()

    @PublishedApi
    internal val delayedRemovalComponentSystem by instance<DelayedRemovalComponentSystem>()
}

val coreModule = module(TypeToken.Any) {
    this bind singleton { World(di) }
    this bind singleton { new(::EntityUpdateContextImpl) }
    this bind singleton { new(::ComponentService) }
    this bind singleton { new(::EntityStoreImpl) }
    this bind singleton { new(::EntityService) }
    this bind singleton { new(::FamilyService) }
    this bind singleton { new(::SystemService) }
    this bind singleton { new(::ScheduleService) }
    this bind singleton { DelayedRemovalComponentSystem(instance()) }
}

interface Updatable {
    fun update(deltaTime: Duration)
}

interface Prioritized {
    val priority: Int
}

interface NeedInitialized {
    fun initialize(world: World)
}

abstract class System(override val world: World) : EntityComponentContext by world.entityUpdateContext

abstract class UpdateSystem(world: World) : System(world), Updatable

abstract class IntervalSystem(world: World, private val interval: Interval = EachFrame) : UpdateSystem(world) {

    private var accumulator: Duration = 0.seconds

    final override fun update(deltaTime: Duration) {
        when (interval) {
            is EachFrame -> onTick(deltaTime)
            is Fixed -> {
                accumulator += deltaTime
                while (accumulator >= interval.step) {
                    onTick(interval.step)
                    accumulator -= interval.step
                }
                onAlpha(accumulator.inWholeNanoseconds.toFloat() / interval.step.inWholeNanoseconds)
            }
        }
    }

    protected abstract fun onTick(deltaTime: Duration)

    protected open fun onAlpha(alpha: Float): Unit = Unit

    sealed interface Interval
    data object EachFrame : Interval
    data class Fixed(val step: Duration) : Interval
}

abstract class IteratingSystem(
    world: World,
    interval: Interval = EachFrame,
    configuration: (FamilyDefinition) -> Unit
) : IntervalSystem(world, interval) {

    val family: Family = world.family(configuration)

    final override fun onTick(deltaTime: Duration) {
        onBeforeTick(deltaTime)
        iteratorCallback(family) { onTickEntity(it, deltaTime) }
        onAfterTick(deltaTime)
    }

    protected open fun iteratorCallback(family: Family, block: (Entity) -> Unit) {
        family.forEach(block)
    }

    protected open fun onBeforeTick(deltaTime: Duration) {
    }

    protected open fun onAfterTick(deltaTime: Duration) {
    }

    protected abstract fun onTickEntity(entity: Entity, deltaTime: Duration)
}

class SystemService(world: World) {

    private val sortedSystems: List<Updatable>

    init {
        val systems: Sequence<Updatable> by world.allInstance<Updatable>()
        val sortedSystems = systems.toMutableList()
        sortedSystems.sortByDescending { if (it is Prioritized) it.priority else 0 }
        this.sortedSystems = sortedSystems
    }

    fun update(deltaTime: Duration) {
        if (sortedSystems.isEmpty()) return
        sortedSystems.forEach { it.update(deltaTime) }
    }
}

@PublishedApi
internal class ScheduleService(val world: World) : Updatable {

    private val entityComponentContext by world.instance<EntityComponentContext>()

    private val delayFrameTasks = arrayListOf<DelayFrameTask>()
    private val frameTasks = arrayListOf<FrameTask>()
    private val waitNextFrameTasks = arrayListOf<FrameTask>()
    private val idGenerator = atomic(0)
    private val activeScheduleBits = bitsOf()
    private val lock = SynchronizedObject()

    fun schedule(scheduleTask: suspend ScheduleScore.() -> Unit): Schedule {
        val schedule = Schedule(idGenerator.getAndIncrement())
        val scheduleScore = ScheduleScoreImpl(world, schedule, entityComponentContext)
        activeScheduleBits.setBit(schedule.id)
        addNextFrameTask(schedule) {
            scheduleTask.startCoroutine(
                scheduleScore,
                Continuation(EmptyCoroutineContext) {
                    activeScheduleBits.clearBit(schedule.id)
                }
            )
        }
        return schedule
    }

    private fun addNextFrameTask(schedule: Schedule, task: (Duration) -> Unit): Unit = synchronized(lock) {
        waitNextFrameTasks.add(FrameTask(schedule, task))
    }

    private fun addDelayFrameTask(schedule: Schedule, delay: Duration, task: (Duration) -> Unit): Unit = synchronized(lock) {
        delayFrameTasks.add(DelayFrameTask(delay, 0.seconds, FrameTask(schedule, task)))
    }

    override fun update(deltaTime: Duration) {
        synchronized(lock) {
            if (delayFrameTasks.isNotEmpty()) {
                val iterator = delayFrameTasks.iterator()
                while (iterator.hasNext()) {
                    val delayFrameTask = iterator.next()
                    delayFrameTask.waitTime += deltaTime
                    if (delayFrameTask.waitTime >= delayFrameTask.delay) {
                        frameTasks.add(delayFrameTask.frameTask)
                        iterator.remove()
                    }
                }
            }

            if (waitNextFrameTasks.isNotEmpty()) {
                frameTasks.addAll(waitNextFrameTasks)
                waitNextFrameTasks.clear()
            }
        }
        if (frameTasks.isEmpty()) return
        frameTasks.forEach { (schedule, task) -> if (schedule.id in activeScheduleBits) task(deltaTime) }
        frameTasks.clear()
    }

    private inner class ScheduleScoreImpl(
        override val world: World,
        private val schedule: Schedule,
        entityComponentContext: EntityComponentContext
    ) : ScheduleScore, EntityComponentContext by entityComponentContext {

        override suspend fun waitNextFrame(): Duration = suspendCoroutine { continuation ->
            addNextFrameTask(schedule) { continuation.resume(it) }
        }

        override suspend fun delay(delay: Duration): Unit = suspendCoroutine { continuation ->
            addDelayFrameTask(schedule, delay) { continuation.resume(Unit) }
        }
    }

    private data class FrameTask(val schedule: Schedule, val task: (Duration) -> Unit)

    private data class DelayFrameTask(
        val delay: Duration,
        var waitTime: Duration,
        val frameTask: FrameTask
    )
}

@RestrictsSuspension
interface ScheduleScore : EntityComponentContext {

    suspend fun waitNextFrame(): Duration

    suspend fun delay(delay: Duration)
}

data class Schedule(val id: Int)

@PublishedApi
internal class DelayedRemovalComponentSystem(
    world: World,
    override val priority: Int = Int.MAX_VALUE
) : UpdateSystem(world), Prioritized, ComponentsHolderProxy {

    private val delayedRemovalComponentBits = bitsOf()
    private val delayedRemovalComponentsHolders = arrayListOf<DelayedRemovalComponentsHolder<*>>()

    fun enableDelayedRemoval(vararg componentTypes: ComponentType<*>) {
        if (componentTypes.isEmpty()) return
        componentTypes.forEach { delayedRemovalComponentBits.setBit(it.index) }
    }

    override fun <C> proxy(componentsHolder: ComponentsHolder<C>): ComponentsHolder<C> {
        if (componentsHolder.componentType.index !in delayedRemovalComponentBits) return componentsHolder
        val delayedRemovalComponentsHolder = DelayedRemovalComponentsHolder(world, componentsHolder)
        delayedRemovalComponentsHolders.add(delayedRemovalComponentsHolder)
        return delayedRemovalComponentsHolder
    }

    override fun update(deltaTime: Duration) {
        if (delayedRemovalComponentsHolders.isEmpty()) return
        delayedRemovalComponentsHolders.forEach { it.update(deltaTime) }
    }

    private class DelayedRemovalComponentsHolder<C>(
        private val world: World,
        private val componentsHolder: ComponentsHolder<C>
    ) : ComponentsHolder<C> by componentsHolder, Updatable {
        private val entities = bitsOf()

        override fun contains(entity: Entity): Boolean {
            return entity.id !in entities && entity in componentsHolder
        }

        override fun set(entity: Entity, component: C) {
            componentsHolder[entity] = component
            entities.clearBit(entity.id)
        }

        override fun minusAssign(entity: Entity) {
            entities.setBit(entity.id)
        }

        override fun update(deltaTime: Duration) {
            entities.map { world.entityService[it] }.forEach { entity -> componentsHolder -= entity }
            entities.clearAll()
        }
    }
}

inline fun World.create(configuration: EntityCreateContext.(Entity) -> Unit = {}): Entity = entityService.create(configuration)
inline fun World.create(entityId: Int, configuration: EntityCreateContext.(Entity) -> Unit): Entity = entityService.create(entityId, configuration)
inline fun World.configure(entity: Entity, configuration: EntityUpdateContext.(Entity) -> Unit): Unit = entityService.configure(entity, configuration)
inline fun World.family(configuration: (FamilyDefinition) -> Unit): Family = familyService.family(configuration)
inline fun World.update(deltaTime: Duration): Unit = systemService.update(deltaTime)
inline fun World.schedule(noinline scheduleTask: suspend ScheduleScore.() -> Unit): Schedule = scheduleService.schedule(scheduleTask)

inline operator fun World.minusAssign(entity: Entity) = entityService.minusAssign(entity)
inline fun World.delete(entity: Entity): Unit = entityService.minusAssign(entity)

inline val World.onEntityCreated: Signal<Entity> get() = entityService.onEntityCreated
inline val World.onEntityChanged: Signal<Entity> get() = entityService.onEntityChanged
inline val World.onEntityRemoved: Signal<Entity> get() = entityService.onEntityRemoved

inline fun World.enableDelayedRemoval(vararg componentTypes: ComponentType<*>) {
    delayedRemovalComponentSystem.enableDelayedRemoval(*componentTypes)
}

val World.entitySize: Int get() = entityService.size

fun world(configuration: DIMainBuilder.() -> Unit): World {
    val di = DI {
        module(coreModule)
        configuration()
    }
    val world by di.instance<World>()
    val systems by world.allInstance<System>()
    val sortSystems = systems.toMutableList()
    sortSystems.sortByDescending { if (it is Prioritized) it.priority else 0 }
    sortSystems.forEach { if (it is NeedInitialized) it.initialize(world) }
    return world
}
