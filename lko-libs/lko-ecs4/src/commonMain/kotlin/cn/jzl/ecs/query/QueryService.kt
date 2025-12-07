package cn.jzl.ecs.query

import cn.jzl.ecs.World

class QueryService(val world: World) {

    fun <E : EntityQueryContext> query(factory: World.() -> E): Query<E> = Query(world.factory())

    fun <E : EntityQueryContext> singleQuery(factory: World.() -> E): SingleQuery<E> = SingleQuery(world.factory())
}