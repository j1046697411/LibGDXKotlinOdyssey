package cn.jzl.di

interface BindingDI<C : Any, T : Any> : DirectDI<C> {

    val node: Node<C, Any?, T>

    fun overriddenFactory(): DIFactory<Any?, T>?
}