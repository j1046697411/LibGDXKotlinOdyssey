package cn.jzl.ecs

import cn.jzl.datastructure.list.ObjectFastList

data class Family(val world: World, val familyMatcher: FamilyMatcher) : Sequence<Archetype> {
    @PublishedApi
    internal val archetypes = ObjectFastList<Archetype>(8)

    val size: Int get() = archetypes.sumOf { archetype -> archetype.table.entities.size }

    internal fun addArchetype(archetype: Archetype): Unit = archetypes.insertLast(archetype)

    override fun iterator(): Iterator<Archetype> = archetypes.iterator()
}