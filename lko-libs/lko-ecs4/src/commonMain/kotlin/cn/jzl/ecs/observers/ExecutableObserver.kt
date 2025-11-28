package cn.jzl.ecs.observers

import cn.jzl.ecs.query.QueryEntityContext
import cn.jzl.ecs.query.Query

interface ExecutableObserver<Context> {
    fun filter(vararg query: Query<out QueryEntityContext>): ExecutableObserver<Context>

    fun exec(handle: Context.()-> Unit) : Observer
}