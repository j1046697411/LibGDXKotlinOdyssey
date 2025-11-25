package cn.jzl.ecs.query

import cn.jzl.ecs.Archetype
import cn.jzl.ecs.Family

class Query<E : QueriedEntity>(val entity: E) : Sequence<E> {

    private val family: Family = entity.build()

    val size: Int get() = family.size

    internal operator fun contains(archetype: Archetype): Boolean {
        println("Query contains ${archetype.id} ${family.familyMatcher.match(archetype)}")

        return family.familyMatcher.match(archetype)
    }

    override fun iterator(): Iterator<E> = iterator {
        family.archetypes.forEach { archetype ->
            entity.updateCache(archetype)
            for (entityIndex in 0 until archetype.table.entities.size) {
                entity.entityIndex = entityIndex
                entity.batchEntityEditor.entity = entity.entity
                yield(entity)
                check(entityIndex == entity.entityIndex)
            }
        }
    }
}