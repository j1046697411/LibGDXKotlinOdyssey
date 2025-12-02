package cn.jzl.ecs

@ConsistentCopyVisibility
data class Archetype internal constructor(
    val id: Int,
    val entityType: EntityType,
    val archetypeProvider: ArchetypeProvider,
    val componentService: ComponentService,
    private val entityService: EntityService
) {
    private val componentAddEdges = mutableMapOf<Relation, Archetype>()
    private val componentRemoveEdges = mutableMapOf<Relation, Archetype>()

    val table: Table = Table(entityType.holdsData())

    val prefab: Entity? by lazy { entityType.firstOrNull { it.kind == componentService.components.instanceOf }?.target }

    val prefabEntityType: EntityType by lazy {
        val prefabEntity = prefab
        if (prefabEntity != null) {
            entityService.runOn(prefabEntity) { instanceEntityType }
        } else {
            EntityType.ENTITY_TYPE_EMPTY
        }
    }

    val instanceEntityType: EntityType by lazy {
        if (prefab == null) return@lazy entityType
        val instanceEntityTypeSet = mutableSetOf<Long>()
        instanceEntityTypeSet.addAll(entityType.map { it.data })
        // 实例实体类型不包含预制体组件
        instanceEntityTypeSet.addAll(prefabEntityType.filter { it.kind != componentService.components.prefab }.map { it.data })
        EntityType(instanceEntityTypeSet.toLongArray())
    }

    private fun Relation.hasHoldsData(): Boolean = componentService.holdsData(this)

    private fun EntityType.holdsData(): EntityType {
        return EntityType(filter { it.hasHoldsData() && !componentService.isShadedComponent(it) }.map { it.data }.toList().toLongArray())
    }

    fun isShadedComponent(relation: Relation): Boolean {
        if (!componentService.isShadedComponent(relation)) return false
        entityType.indexOf(relation).takeIf { it == -1 } ?: return true
        val prefab = prefab ?: return false
        return entityService.runOn(prefab) { isShadedComponent(relation) }
    }

    fun getComponentIndex(relation: Relation): ComponentIndex? {
        val entityType = if (componentService.isShadedComponent(relation)) entityType else table.entityType
        val index = entityType.indexOf(relation)
        if (index != -1) {
            return ComponentIndex(Entity.ENTITY_INVALID, index)
        }
        val prefab = prefab ?: return null
        return entityService.runOn(prefab) { getPrefabComponentIndex(prefab, relation) }
    }

    private fun getPrefabComponentIndex(entity: Entity, relation: Relation): ComponentIndex? {
        val entityType = if (componentService.isShadedComponent(relation)) entityType else table.entityType
        val index = entityType.indexOf(relation)
        if (index != -1) {
            return ComponentIndex(entity, index)
        }
        val prefab = prefab ?: return null
        return entityService.runOn(prefab) { getPrefabComponentIndex(prefab, relation) }
    }

    fun match(relation: Relation): Boolean {
        return when {
            relation.kind == componentService.components.any -> instanceEntityType.any { it.target == relation.target }
            relation.target == componentService.components.any -> entityType.any { it.kind == relation.kind }
            else -> instanceEntityType.any { it == relation }
        }
    }

    operator fun contains(relation: Relation): Boolean {
        entityType.indexOf(relation).takeIf { it == -1 } ?: return true
        val prefab = prefab ?: return false
        return entityService.runOn(prefab) { relation in this }
    }

    operator fun plus(relation: Relation): Archetype {
        if (entityType.indexOf(relation) != -1) return this
        return componentAddEdges.getOrPut(relation) {
            archetypeProvider.getArchetype(entityType + relation)
        }
    }

    operator fun minus(relation: Relation): Archetype {
        if (entityType.indexOf(relation) == -1) return this
        return componentRemoveEdges.getOrPut(relation) {
            archetypeProvider.getArchetype(entityType - relation)
        }
    }
}