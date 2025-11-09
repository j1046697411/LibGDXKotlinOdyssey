package cn.jzl.ecs.v2

/**
 * 组件接口，所有组件类型的基类
 *
 * 组件是ECS架构中的数据容器，只包含状态而不包含逻辑
 * 每个组件必须指定其类型，并可以选择性地实现附加和分离回调
 *
 * @param C 组件的具体类型，用于自引用，确保类型安全
 */
interface Component<C : Component<C>> {
    /**
     * 组件的类型标识，用于在系统中唯一标识此组件类型
     */
    val type: ComponentType<C>

    /**
     * 当组件被附加到实体时调用的回调
     *
     * 可用于执行初始化逻辑或注册监听器
     *
     * @param entity 组件被附加到的实体
     */
    fun World.onAttach(entity: Entity): Unit = Unit

    /**
     * 当组件从实体中分离时调用的回调
     *
     * 可用于执行清理逻辑或移除监听器
     *
     * @param entity 组件被从其分离的实体
     */
    fun World.onDetach(entity: Entity): Unit = Unit
}