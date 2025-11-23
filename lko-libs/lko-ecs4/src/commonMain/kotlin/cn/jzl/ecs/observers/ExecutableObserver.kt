package cn.jzl.ecs.observers

import cn.jzl.ecs.query.QueriedEntity
import cn.jzl.ecs.query.Query

interface ExecutableObserver<Context> {
    fun filter(vararg query: Query<out QueriedEntity>): ExecutableObserver<Context>

    fun exec(handle: Context.()-> Unit) : Observer
}