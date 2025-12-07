package cn.jzl.ecs

@ConsistentCopyVisibility
data class Archetype internal constructor(
    val id: Int,
    val archetypeType: EntityType,
    val archetypeProvider: ArchetypeProvider,
    val componentService: ComponentService,
    private val entityService: EntityService
) {
    private val componentAddEdges = mutableMapOf<Relation, Archetype>()
    private val componentRemoveEdges = mutableMapOf<Relation, Archetype>()

    val table: Table = Table(archetypeType.holdsData())

    val prefab: Entity? by lazy { archetypeType.firstOrNull { it.kind == componentService.components.instanceOf }?.target }

    val entityType: EntityType by lazy {
        val prefab = this.prefab ?: return@lazy archetypeType
        val entityType = mutableSetOf<Long>()
        entityType.addAll(archetypeType.map { it.data })
        entityService.runOn(prefab) {
            entityType.addAll(this.entityType.filter { it.kind != componentService.components.prefab }.map { it.data })
        }
        EntityType(entityType.toLongArray())
    }

    private fun Relation.hasHoldsData(): Boolean = componentService.holdsData(this)

    private fun EntityType.holdsData(): EntityType {
        return EntityType(filter { it.hasHoldsData() && !componentService.isShadedComponent(it) }.map { it.data }.toList().toLongArray())
    }

    fun isShadedComponent(relation: Relation): Boolean {
        if (!componentService.isShadedComponent(relation)) return false
        archetypeType.indexOf(relation).takeIf { it == -1 } ?: return true
        val prefab = prefab ?: return false
        return entityService.runOn(prefab) { isShadedComponent(relation) }
    }

    fun getComponentIndex(relation: Relation): ComponentIndex? {
        val entityType = if (componentService.isShadedComponent(relation)) archetypeType else table.entityType
        val index = entityType.indexOf(relation)
        if (index != -1) {
            return ComponentIndex(Entity.ENTITY_INVALID, index)
        }
        val prefab = prefab ?: return null
        return entityService.runOn(prefab) { getPrefabComponentIndex(prefab, relation) }
    }

    private fun getPrefabComponentIndex(entity: Entity, relation: Relation): ComponentIndex? {
        val entityType = if (componentService.isShadedComponent(relation)) archetypeType else table.entityType
        val index = entityType.indexOf(relation)
        if (index != -1) {
            return ComponentIndex(entity, index)
        }
        val prefab = prefab ?: return null
        return entityService.runOn(prefab) { getPrefabComponentIndex(prefab, relation) }
    }

    fun match(relation: Relation): Boolean {
        return when {
            relation.kind == componentService.components.any -> entityType.any { it.target == relation.target }
            relation.target == componentService.components.any -> archetypeType.any { it.kind == relation.kind }
            else -> entityType.any { it == relation }
        }
    }

    operator fun contains(relation: Relation): Boolean {
        archetypeType.indexOf(relation).takeIf { it == -1 } ?: return true
        val prefab = prefab ?: return false
        return entityService.runOn(prefab) { relation in this }
    }

    operator fun plus(relation: Relation): Archetype {
        if (archetypeType.indexOf(relation) != -1) return this
        return componentAddEdges.getOrPut(relation) {
            archetypeProvider.getArchetype(archetypeType + relation)
        }
    }

    operator fun minus(relation: Relation): Archetype {
        if (archetypeType.indexOf(relation) == -1) return this
        return componentRemoveEdges.getOrPut(relation) {
            archetypeProvider.getArchetype(archetypeType - relation)
        }
    }
}