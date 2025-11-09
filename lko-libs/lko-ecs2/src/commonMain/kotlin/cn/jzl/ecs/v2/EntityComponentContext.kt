package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet

/**
 * EntityComponentContext - 实体组件访问基础上下文接口
 * 
 * 核心功能：
 * - 提供实体组件的只读访问能力
 * - 定义实体状态查询方法
 * - 作为EntityCreateContext和EntityUpdateContext的基础接口
 * - 支持安全的组件查询和检查操作
 * 
 * 此接口是ECS框架中实体组件访问的基础，它定义了如何检查和获取实体的组件信息，
 * 但不提供修改组件的能力。所有涉及组件读取的上下文都应实现此接口，
 * 确保在适当的场景中提供一致的组件访问方式。
 * 
 * 设计理念：
 * - 遵循最小权限原则，仅提供必要的只读操作
 * - 通过扩展此接口构建更强大的上下文类型
 * - 使用扩展属性和运算符重载提供优雅的API
 */
interface EntityComponentContext {
    /**
     * 对World实例的引用，用于访问ECS核心功能
     */
    val world: World

    /**
     * 获取实体的组件位集合
     * 
     * 扩展属性，允许直接通过实体访问其组件位集合
     * 用于高效判断实体的组件组合
     */
    val Entity.componentBits: BitSet
    
    /**
     * 检查实体是否处于活动状态
     * 
     * 扩展属性，允许直接检查实体的激活状态
     */
    val Entity.active: Boolean

    /**
     * 检查实体是否拥有指定类型的组件
     * 
     * 使用contains运算符重载，提供简洁的检查语法
     * 例如：`if (PositionComponent in entity)`
     * 
     * @receiver entity 要检查的实体
     * @param componentType 要检查的组件类型
     * @return 如果实体拥有此类型组件返回true
     */
    operator fun Entity.contains(componentType: ComponentReadAccesses<*>): Boolean

    /**
     * 获取实体的指定类型组件
     * 
     * 使用get运算符重载，提供简洁的组件获取语法
     * 例如：`val position = entity[PositionComponent]`
     * 
     * @receiver entity 要获取组件的实体
     * @param componentType 要获取的组件类型
     * @return 实体的组件实例
     * @throws NoSuchElementException 如果实体没有此类型组件
     */
    operator fun <C : Any> Entity.get(componentType: ComponentReadAccesses<C>): C

    /**
     * 获取实体的指定类型组件，如果不存在则返回null
     * 
     * 安全版本的组件获取方法，不会抛出异常
     * 
     * @param Entity 要获取组件的实体
     * @param componentType 要获取的组件类型
     * @return 实体的组件实例，如果不存在则返回null
     */
    fun <C : Any> Entity.getOrNull(componentType: ComponentReadAccesses<C>): C?
}