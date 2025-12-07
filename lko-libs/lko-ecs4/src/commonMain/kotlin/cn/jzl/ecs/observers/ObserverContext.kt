package cn.jzl.ecs.observers

import cn.jzl.ecs.Entity
import cn.jzl.ecs.Relation
import cn.jzl.ecs.World
import cn.jzl.ecs.WorldOwner

interface ObserverContext : WorldOwner {
    override val world: World
    val entity: Entity
    val involvedRelation: Relation
}