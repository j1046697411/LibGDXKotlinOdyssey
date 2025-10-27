package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.signal.Signal
import kotlinx.atomicfu.atomic

class Family(
    override val world: World,
    internal val familyDefinition: FamilyDefinition
) : EntityComponentContext by world.entityUpdateContext {
    private val entityBits = BitSet()
    private val entityCount = atomic(0)

    val entities: Sequence<Entity> get() = entityBits.map { entityId -> world.entityService[entityId] }
    val size: Int get() = entityCount.value

    val onEntityInserted: Signal<Entity> = Signal()
    val onEntityRemoved: Signal<Entity> = Signal()

    internal fun entityChanged(entity: Entity) {
        when {
            entity in this && entity.id !in entityBits -> insertEntity(entity)
            entity !in this && entity.id in entityBits -> removeEntity(entity)
        }
    }

    private fun insertEntity(entity: Entity) {
        entityBits.set(entity.id)
        entityCount.incrementAndGet()
        onEntityInserted(entity)
    }

    private fun removeEntity(entity: Entity) {
        entityBits.clear(entity.id)
        entityCount.decrementAndGet()
        onEntityRemoved(entity)
    }

    operator fun contains(entity: Entity): Boolean = familyDefinition.run { checkEntity(entity) }
}