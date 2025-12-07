package cn.jzl.ecs

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.LongFastList

interface FamilyMatcher {

    fun match(archetype: Archetype): Boolean

    fun FamilyMatchScope.getArchetypeBits(): BitSet

    interface FamilyMatchScope {
        val allArchetypeBits: BitSet

        fun getArchetypeBits(relation: Relation): BitSet
    }

    interface FamilyBuilder : WorldOwner {
        override val world: World
        val keys: LongFastList
        fun matcher(familyMatcher: FamilyMatcher)
    }
}