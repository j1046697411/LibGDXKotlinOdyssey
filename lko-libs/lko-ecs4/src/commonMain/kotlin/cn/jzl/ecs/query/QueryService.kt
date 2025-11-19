package cn.jzl.ecs.query

import cn.jzl.ecs.World

class QueryService(val world: World) {
    inline fun <reified E : QueriedEntity> query(factory: World.() -> E): Query<E> = Query(world.factory())
}