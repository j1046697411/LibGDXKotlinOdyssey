package cn.jzl.ecs.v2

/**
 * EntityUpdateContext - 实体更新上下文接口
 * 
 * 核心功能：
 * - 扩展EntityCreateContext，提供实体组件的移除能力
 * - 定义实体组件操作的上下文环境
 * - 支持在安全环境中修改实体的组件集合
 * 
 * 此接口是ECS框架中实体操作的核心上下文，允许在特定范围内（如系统处理过程中）
 * 安全地修改实体组件。通过继承EntityCreateContext，它同时支持组件的添加和移除操作，
 * 提供完整的实体组件生命周期管理能力。
 * 
 * 典型用法场景：
 * - 系统中处理实体时修改组件
 * - 家族中实体变化的响应处理
 * - 实体状态的动态调整
 */
interface EntityUpdateContext : EntityCreateContext {
    /**
     * 从实体中移除指定类型的组件
     * 
     * 使用减等于运算符重载，提供简洁的语法移除实体组件
     * 例如：`entity -= PositionComponent`
     * 
     * @param entity 要移除组件的实体
     * @param componentType 要移除的组件类型
     */
    operator fun Entity.minusAssign(componentType: ComponentWriteAccesses<*>)
}