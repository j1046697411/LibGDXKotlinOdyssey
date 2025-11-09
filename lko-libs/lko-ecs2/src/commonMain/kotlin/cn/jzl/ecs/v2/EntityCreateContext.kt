package cn.jzl.ecs.v2

import cn.jzl.di.DIProvider

/**
 * EntityCreateContext - 实体创建上下文接口
 * 
 * 核心功能：
 * - 扩展EntityComponentContext，提供实体组件的添加和初始化能力
 * - 定义安全的组件创建和注入环境
 * - 支持组件的设置、获取或创建、标签添加等操作
 * 
 * 此接口在ECS框架中负责实体构建和组件配置阶段，确保组件的正确添加和初始化。
 * 通过定义明确的上下文边界，它确保组件操作在合适的时机和环境中执行，
 * 避免在不适当的情况下修改实体结构。
 * 
 * 典型用法场景：
 * - 实体工厂中创建新实体并配置组件
 * - 系统初始化时设置实体的初始状态
 * - 从配置数据构建实体
 */
interface EntityCreateContext : EntityComponentContext {
    /**
     * 为实体设置指定类型的组件
     * 
     * 使用set运算符重载，提供简洁的组件设置语法
     * 例如：`entity[PositionComponent] = Position(10f, 20f)`
     * 
     * @param entity 要设置组件的实体
     * @param componentType 组件类型
     * @param component 组件实例
     * @return 替换前的组件实例，如果不存在则返回null
     */
    operator fun <C : Any> Entity.set(componentType: ComponentWriteAccesses<C>, component: C): C?

    /**
     * 获取实体的组件，如果不存在则使用提供者创建
     * 
     * 结合了获取和创建的便捷方法，避免手动检查组件是否存在
     * 适合惰性初始化组件的场景
     * 
     * @param entity 要操作的实体
     * @param componentType 组件类型
     * @param provider 组件提供者，用于创建新的组件实例
     * @return 实体的组件实例（已有或新创建）
     */
    fun <C : Any> Entity.getOrPut(componentType: ComponentWriteAccesses<C>, provider: DIProvider<C>): C

    /**
     * 为实体添加标签
     * 
     * 使用加等于运算符重载，提供简洁的标签添加语法
     * 例如：`entity += ActiveTag`
     * 
     * @param entity 要添加标签的实体
     * @param tag 要添加的标签组件类型
     */
    operator fun Entity.plusAssign(tag: ComponentWriteAccesses<Boolean>)
}