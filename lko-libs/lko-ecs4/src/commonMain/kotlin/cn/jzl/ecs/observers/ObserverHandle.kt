package cn.jzl.ecs.observers

import cn.jzl.ecs.Entity
import cn.jzl.ecs.Relation

fun interface ObserverHandle {
    fun handle(entity: Entity, event: Any?, involved: Relation)
}