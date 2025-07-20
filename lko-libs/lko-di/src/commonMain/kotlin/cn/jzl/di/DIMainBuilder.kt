package cn.jzl.di

import org.kodein.type.TypeToken


interface DIMainBuilder : DI.Builder<Any> {

    val context: DIContext<*>

    val callbacks: Sequence<DICallback>

    override val contextType: TypeToken<Any> get() = TypeToken.Any
}

