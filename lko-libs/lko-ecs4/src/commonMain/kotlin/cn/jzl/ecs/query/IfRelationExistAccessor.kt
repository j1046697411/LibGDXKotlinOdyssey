package cn.jzl.ecs.query

import cn.jzl.ecs.Archetype
import cn.jzl.ecs.FamilyMatcher
import cn.jzl.ecs.Relation
import kotlin.reflect.KProperty

class IfRelationExistAccessor(private val relation: Relation) : CachedAccessor, ReadOnlyAccessor<Boolean> {

    private var archetype: Archetype? = null
    private var exist: Boolean = false

    override fun updateCache(archetype: Archetype) {
        this.archetype = archetype
        this.exist = relation in archetype
    }

    override val isMarkedNullable: Boolean get() = true
    override val optionalGroup: OptionalGroup get() = OptionalGroup.Ignore

    override fun FamilyMatcher.FamilyBuilder.matching() = Unit

    override fun getValue(thisRef: EntityQueryContext, property: KProperty<*>): Boolean = exist
}