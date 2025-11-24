package cn.jzl.ecs.observers

import cn.jzl.ecs.Entity
import cn.jzl.ecs.Relation
import cn.jzl.ecs.World

interface ObserverContext {
    val world: World
    val entity: Entity
    val involvedRelation: Relation
}