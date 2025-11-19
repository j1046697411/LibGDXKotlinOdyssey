package cn.jzl.ecs.observers

interface ObserverContextWithData<E> : ObserverContext {
    val event: E
}