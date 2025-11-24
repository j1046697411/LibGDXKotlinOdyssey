package cn.jzl.ecs

data class Archetype(
    val id: Int,
    val entityType: EntityType,
    val archetypeProvider: ArchetypeProvider,
    val componentService: ComponentService
) {
    private val componentAddEdges = mutableMapOf<Relation, Archetype>()
    private val componentRemoveEdges = mutableMapOf<Relation, Archetype>()

    val table: Table = Table(entityType.holdsData())

    private fun Relation.hasHoldsData(): Boolean = componentService.holdsData(this)

    private fun EntityType.holdsData(): EntityType {
        return EntityType(filter { it.hasHoldsData() }.map { it.data }.toList().toLongArray())
    }

    operator fun contains(relation: Relation): Boolean = entityType.indexOf(relation) >= 0

    operator fun plus(relation: Relation): Archetype {
        if (relation in this) return this
        return componentAddEdges.getOrPut(relation) {
            archetypeProvider.getArchetype(entityType + relation)
        }
    }

    operator fun minus(relation: Relation): Archetype {
        if (relation !in this) return this
        return componentRemoveEdges.getOrPut(relation) {
            archetypeProvider.getArchetype(entityType - relation)
        }
    }
}