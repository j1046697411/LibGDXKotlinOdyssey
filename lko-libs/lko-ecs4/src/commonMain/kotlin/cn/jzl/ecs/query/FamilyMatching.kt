package cn.jzl.ecs.query

import cn.jzl.ecs.FamilyMatcher

interface FamilyMatching {
    fun FamilyMatcher.FamilyBuilder.matching()
}