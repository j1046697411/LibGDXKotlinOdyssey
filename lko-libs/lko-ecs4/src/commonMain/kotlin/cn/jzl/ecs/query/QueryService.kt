package cn.jzl.ecs.query

import cn.jzl.ecs.World

class QueryService(val world: World) {
    fun <E : QueryEntityContext> query(factory: World.() -> E): Query<E> = Query(world.factory())
}