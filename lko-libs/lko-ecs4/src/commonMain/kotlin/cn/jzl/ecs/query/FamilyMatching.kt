package cn.jzl.ecs.query

import cn.jzl.ecs.FamilyMatcher

interface FamilyMatching {

    val isMarkedNullable: Boolean

    val optionalGroup: OptionalGroup

    fun FamilyMatcher.FamilyBuilder.matching()
}

