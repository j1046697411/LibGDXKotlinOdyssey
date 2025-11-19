package cn.jzl.ecs.query

import cn.jzl.ecs.Archetype
import cn.jzl.ecs.FamilyMatcher
import cn.jzl.ecs.Relation
import cn.jzl.ecs.relation

abstract class AbstractCachedAccessor(val relation: Relation) : CachedAccessor {

    protected var componentIndex: Int = -1
    protected var archetype: Archetype? = null

    override fun FamilyMatcher.FamilyBuilder.matching(): Unit = relation(relation)

    override fun updateCache(archetype: Archetype) {
        this.componentIndex = archetype.table.entityType.indexOf(relation)
        this.archetype = archetype
    }
}