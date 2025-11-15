package cn.jzl.ecs.entity

import cn.jzl.ecs.EntityId
import cn.jzl.ecs.World

data class Entity(val world: World, val entityId: EntityId)
