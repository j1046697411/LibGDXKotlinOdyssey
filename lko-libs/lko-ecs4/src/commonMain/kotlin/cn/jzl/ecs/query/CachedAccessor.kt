package cn.jzl.ecs.query

import cn.jzl.ecs.Archetype

interface CachedAccessor : Accessor, FamilyMatching {
    fun updateCache(archetype: Archetype)
}