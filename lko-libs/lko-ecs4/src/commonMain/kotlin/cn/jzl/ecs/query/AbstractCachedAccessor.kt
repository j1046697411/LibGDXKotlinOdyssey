package cn.jzl.ecs.query

import cn.jzl.ecs.*
import kotlin.reflect.KType

abstract class AbstractCachedAccessor(
    private val type: KType,
    val relation: Relation,
    override val optionalGroup: OptionalGroup,
    private val provider: Archetype.(Relation) -> ComponentIndex?,
) : CachedAccessor {

    override val isMarkedNullable: Boolean get() = type.isMarkedNullable

    protected var componentIndex: ComponentIndex? = null
    protected var archetype: Archetype? = null

    override fun FamilyMatcher.FamilyBuilder.matching(): Unit = relation(relation)

    override fun updateCache(archetype: Archetype) {
        this.archetype = archetype
        this.componentIndex = archetype.provider(relation)
    }
}