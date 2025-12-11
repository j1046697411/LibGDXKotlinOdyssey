package cn.jzl.ecs.query

import cn.jzl.ecs.Archetype
import cn.jzl.ecs.ComponentId
import cn.jzl.ecs.ComponentIndex
import cn.jzl.ecs.Entity
import cn.jzl.ecs.FamilyMatcher
import cn.jzl.ecs.Relation
import cn.jzl.ecs.kind
import kotlin.reflect.KProperty

class RelationUpAccessor(private val kind: ComponentId) : CachedAccessor, ReadWriteAccessor<Entity> {

    private var componentIndex: ComponentIndex? = null
    private var relation: Relation? = null
    private var archetype: Archetype? = null

    override val isMarkedNullable: Boolean get() = false
    override val optionalGroup: OptionalGroup get() = OptionalGroup.Ignore
    override fun FamilyMatcher.FamilyBuilder.matching(): Unit = kind(kind)

    override fun updateCache(archetype: Archetype) {
        this.archetype = archetype
        this.relation = archetype.entityType.filter { it.kind == kind }.single()
        this.componentIndex = relation?.let { archetype.getComponentIndex(it) }
    }

    override fun setValue(thisRef: EntityQueryContext, property: KProperty<*>, value: Entity) {
        val relation = requireNotNull(relation) {}
        if (relation.target != value) {
            thisRef.batchEntityEditor.addRelation(thisRef.entity, Relation.Companion(kind, value))
        }
    }

    override fun getValue(thisRef: EntityQueryContext, property: KProperty<*>): Entity {
        return requireNotNull(relation) { "" }.target
    }
}