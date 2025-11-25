package cn.jzl.ecs.query

import cn.jzl.ecs.Archetype
import cn.jzl.ecs.FamilyMatcher
import cn.jzl.ecs.Relation
import cn.jzl.ecs.relation
import kotlin.reflect.KType

abstract class AbstractCachedAccessor(
    private val type: KType,
    val relation: Relation,
    override val optionalGroup: OptionalGroup,
    private val provider: Archetype.(Relation) -> Int,
) : CachedAccessor {

    override val isMarkedNullable: Boolean get() = type.isMarkedNullable

    protected var componentIndex: Int = -1
    protected var archetype: Archetype? = null

    override fun FamilyMatcher.FamilyBuilder.matching(): Unit = relation(relation)

    override fun updateCache(archetype: Archetype) {
        this.archetype = archetype
        this.componentIndex = archetype.provider(relation)
    }
}