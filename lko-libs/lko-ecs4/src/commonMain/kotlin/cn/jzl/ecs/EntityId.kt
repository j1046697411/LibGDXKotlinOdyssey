@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ecs

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.IntFastList
import cn.jzl.datastructure.list.ObjectFastList
import cn.jzl.datastructure.math.*
import cn.jzl.di.DI
import cn.jzl.di.instance
import cn.jzl.ecs.component.ComponentId
import cn.jzl.ecs.component.ComponentService
import cn.jzl.ecs.component.Components
import cn.jzl.ecs.entity.EntityService
import cn.jzl.ecs.entity.EntityStore
import cn.jzl.ecs.entity.EntityUpdateContext
import kotlin.getValue
import kotlin.jvm.JvmInline

@JvmInline
value class EntityId @PublishedApi internal constructor(val data: Int) {
    val id: Int get() = data.extract24(0)
    val version: Int get() = data.extract08(24)

    override fun toString(): String = "EntityId($id, $version)"

    companion object {
        operator fun invoke(id: Int, version: Int): EntityId = EntityId(id.insert08(version, 24))
    }
}

@JvmInline
value class Relation @PublishedApi internal constructor(@PublishedApi internal val data: Long) {
    val kind: ComponentId get() = EntityId(data.low)
    val target: EntityId get() = EntityId(data.high)

    override fun toString(): String = "Relation(kind = $kind, target = $target)"

    companion object {
        operator fun invoke(kind: ComponentId, target: EntityId): Relation {
            return Relation(Long.fromLowHigh(kind.data, target.data))
        }
    }
}

@JvmInline
value class RelationService(private val world: World) {

    @PublishedApi
    internal val entityService: EntityService get() = world.entityService

    fun hasRelation(entityId: EntityId, relation: Relation): Boolean = entityService.runOn(entityId) {
        relation in this
    }

    fun getRelation(entityId: EntityId, relation: Relation): Any? = entityService.runOn(entityId) { entityIndex ->
        val componentIndex = table.entityType.indexOf(relation)
        if (componentIndex != -1) table[componentIndex, entityIndex] else null
    }

    fun addRelation(entityId: EntityId, relation: Relation, data: Any): Unit = entityService.runOn(entityId) { entityIndex ->
        val componentIndex = table.entityType.indexOf(relation)
        if (componentIndex != -1) {
            table[entityIndex, componentIndex] = data
            return@runOn
        }
        val newArchetype = this + relation
        var index = 0
        val newEntityIndex = newArchetype.table.insert(entityId) {
            if (relation == this) data else table[index++, entityIndex]
        }
        val isNotLast = entityIndex != table.size - 1
        table.remove(entityIndex)
        entityService.updateEntityRecord(entityId, newArchetype, newEntityIndex)
        if (table.size > 0 && isNotLast) {
            entityService.updateEntityRecord(table[entityIndex], this, entityIndex)
        }
    }

    fun addRelation(entityId: EntityId, relation: Relation): Unit = entityService.runOn(entityId) { entityIndex ->
        if (relation in this) return@runOn
        val newArchetype = this + relation
        val newEntityIndex = newArchetype.table.insert(entityId) { table[it, entityIndex] }
        table.remove(entityIndex)
        entityService.updateEntityRecord(entityId, newArchetype, newEntityIndex)
        if (table.size > 0) {
            entityService.updateEntityRecord(table[entityIndex], this, entityIndex)
        }
    }

    fun removeRelation(entityId: EntityId, relation: Relation): Unit = entityService.runOn(entityId) { entityIndex ->
        if (relation !in this) return@runOn
        val newArchetype = this - relation
        val componentIndex = table.entityType.indexOf(relation)
        val newEntityIndex = newArchetype.table.insert(entityId) {
            if (componentIndex > it) table[it, entityIndex] else table[it + 1, entityIndex]
        }
        table.remove(entityIndex)
        entityService.updateEntityRecord(entityId, newArchetype, newEntityIndex)
        if (table.size > 0) {
            entityService.updateEntityRecord(table[entityIndex], newArchetype, entityIndex)
        }
    }
}

@JvmInline
value class EntityType @PublishedApi internal constructor(@PublishedApi internal val data: LongArray) : Sequence<Relation> {
    init {
        data.sort()
    }

    val size: Int get() = data.size

    fun isEmpty(): Boolean = size == 0
    fun isNotEmpty(): Boolean = size > 0

    override fun iterator(): Iterator<Relation> = iterator {
        for (i in data.indices) {
            yield(Relation(data[i]))
        }
    }

    override fun toString(): String = joinToString(", ", "EntityType[", "]")

    companion object {
        val ENTITY_TYPE_EMPTY = EntityType(LongArray(0))
    }
}

inline operator fun EntityType.plus(relation: Relation): EntityType = EntityType(data + relation.data)
inline operator fun EntityType.minus(relation: Relation): EntityType = EntityType(data.filter { it != relation.data }.toLongArray())
inline fun EntityType.contains(relation: Relation): Boolean = binarySearch(relation) >= 0
inline fun EntityType.indexOf(relation: Relation): Int = binarySearch(relation)

@PublishedApi
internal fun EntityType.binarySearch(relation: Relation): Int {
    var left = 0
    var right = data.size - 1
    while (left <= right) {
        val mid = (left + right) ushr 1
        val midVal = data[mid]
        when {
            midVal < relation.data -> left = mid + 1
            midVal > relation.data -> right = mid - 1
            else -> return mid // 找到元素
        }
    }
    return -1
}

class ArchetypeService(private val world: World) : ArchetypeProvider {
    override val components: Components get() = world.componentService.components
    private val typeToArchetypeMap = mutableMapOf<EntityType, Archetype>()
    private val archetypes = ObjectFastList<Archetype>()

    override val rootArchetype: Archetype = getArchetype(EntityType.ENTITY_TYPE_EMPTY)

    override fun getArchetype(entityType: EntityType): Archetype {
        return typeToArchetypeMap.getOrPut(entityType) { createArchetype(entityType) }
    }

    private fun createArchetype(entityType: EntityType): Archetype {
        val archetype = Archetype(typeToArchetypeMap.size, entityType, this)
        archetypes.insertLast(archetype)
        return archetype
    }

    @PublishedApi
    internal operator fun get(id: Int): Archetype {
        check(id in archetypes.indices) { "archetype id $id is out of bounds" }
        return archetypes[id]
    }
}

interface ArchetypeProvider {
    val rootArchetype: Archetype
    val components: Components
    fun getArchetype(entityType: EntityType): Archetype
}

data class Archetype(val id: Int, val entityType: EntityType, val archetypeProvider: ArchetypeProvider) {
    private val componentAddEdges = mutableMapOf<Relation, Archetype>()
    private val componentRemoveEdges = mutableMapOf<Relation, Archetype>()

    val table: Table = Table(entityType.holdsData())

    init {
        println("create archetype $id ${archetypeProvider.components.tag} with entityType $entityType ${table.entityType}")
    }

    // TODO: 2023/12/20 这里的判断有问题，应该是判断是target 的组件包含 tag 才需要 holdsData
    private fun Relation.hasHoldsData(): Boolean = target != archetypeProvider.components.tag
    private fun EntityType.holdsData(): EntityType {
        return EntityType(filter { it.hasHoldsData() }.map { it.data }.toList().toLongArray())
    }

    operator fun contains(relation: Relation): Boolean = entityType.indexOf(relation) >= 0

    operator fun plus(relation: Relation): Archetype {
        return componentAddEdges.getOrPut(relation) {
            archetypeProvider.getArchetype(entityType + relation)
        }
    }

    operator fun minus(relation: Relation): Archetype {
        return componentRemoveEdges.getOrPut(relation) {
            archetypeProvider.getArchetype(entityType - relation)
        }
    }
}


data class Table(val entityType: EntityType) {
    @PublishedApi
    internal val entityIds = IntFastList()

    @PublishedApi
    internal val entityIdBits = BitSet()

    @PublishedApi
    internal val componentArrays by lazy { Array(entityType.size) { ObjectFastList<Any>() } }

    val size: Int get() = entityIds.size

    operator fun contains(entityId: EntityId): Boolean = entityIdBits[entityId.id]

    @PublishedApi
    internal inline fun check(entityIndex: Int, componentIndex: Int) {
        check(entityIndex in entityIds.indices) { "entityIndex $entityIndex is out of bounds" }
        check(componentIndex in componentArrays.indices) { "componentIndex $componentIndex is out of bounds" }
    }

    inline fun insert(entityId: EntityId, provider: Relation.(Int) -> Any): Int {
        val size = entityIds.size
        entityIds.insertLast(entityId.data)
        entityIdBits.set(entityId.id)
        if (entityType.isNotEmpty()) {
            entityType.forEachIndexed { index, relation ->
                componentArrays[index].insertLast(relation.provider(index))
            }
        }
        return size
    }

    inline operator fun get(entityIndex: Int): EntityId {
        check(entityIndex in entityIds.indices) { "entityIndex $entityIndex is [0, ${entityIds.size - 1}] out of bounds" }
        return EntityId(entityIds[entityIndex])
    }

    inline operator fun get(entityIndex: Int, componentIndex: Int): Any {
        check(entityIndex, componentIndex)
        return componentArrays[componentIndex][entityIndex]
    }

    inline operator fun set(entityIndex: Int, componentIndex: Int, value: Any) {
        check(entityIndex, componentIndex)
        componentArrays[componentIndex][entityIndex] = value
    }

    inline fun remove(entityIndex: Int) {
        check(entityIndex in entityIds.indices) { "entityIndex $entityIndex is out of bounds" }
        entityIds.removeAt(entityIndex)
        entityIdBits.clear(entityIndex)
        if (entityType.isNotEmpty()) {
            componentArrays.forEach { it.removeAt(entityIndex) }
        }
    }
}

class World(@PublishedApi internal val di: DI) {

    @PublishedApi
    internal val entityStore by di.instance<EntityStore>()

    @PublishedApi
    internal val entityService by di.instance<EntityService>()

    @PublishedApi
    internal val archetypeService by di.instance<ArchetypeService>()

    @PublishedApi
    internal val componentService by di.instance<ComponentService>()

    @PublishedApi
    internal val relationService by di.instance<RelationService>()

    @PublishedApi
    internal val entityUpdateContext by di.instance<EntityUpdateContext>()
}

