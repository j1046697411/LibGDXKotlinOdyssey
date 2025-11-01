package cn.jzl.ecs.v2

import kotlin.jvm.JvmInline

/**
 * 表示对组件类型的只读访问权限
 *
 * @property type 组件类型
 * @param C 组件类型参数
 */
@JvmInline
internal value class ComponentReadAccessesImpl<C>(
    override val type: ComponentType<C>
) : ComponentReadAccesses<C>

/**
 * 表示对组件类型的写访问权限
 *
 * @property type 组件类型
 * @param C 组件类型参数
 */
@JvmInline
internal value class ComponentWriteAccessesImpl<C>(
    override val type: ComponentType<C>
) : ComponentWriteAccesses<C>

interface ComponentReadAccesses<C> {
    val type: ComponentType<C>
}

interface ComponentWriteAccesses<C> : ComponentReadAccesses<C> {
    override val type: ComponentType<C>
}
