package cn.jzl.ecs.v2

import kotlin.NoSuchElementException

/**
 * ComponentsHolderImpl - 组件持有器的标准实现
 * 
 * 实现逻辑：
 * 1. 使用泛型设计支持任意类型的组件
 * 2. 采用HashMap作为主要数据结构，提供O(1)时间复杂度的组件访问
 * 3. 实现完整的组件生命周期管理，包括附加、更新和分离回调
 * 4. 提供类型安全的组件存取API
 * 5. 通过实体ID而不是实体对象作为键，减少对象创建和内存使用
 * 
 * 设计考虑：
 * - 性能优化：使用HashMap和实体ID作为键，确保即使在大量实体的情况下也能保持高效访问
 * - 类型安全：通过泛型和显式的类型参数确保组件操作的类型安全
 * - 生命周期管理：完整处理组件的附加和分离回调，确保组件可以正确初始化和清理
 * - 内存效率：仅存储必要的数据，优化内存使用
 * 
 * @param C 组件类型的泛型参数
 * @param world World实例，用于执行组件生命周期回调和提供上下文
 * @param componentType 此持有器管理的特定组件类型，用于类型标识
 */
internal class ComponentsHolderImpl<C>(
    private val world: World,
    override val componentType: ComponentType<C>
) : ComponentsHolder<C> {
    /**
     * 存储实体ID到组件实例的映射
     * 
     * 数据结构特性：
     * - 使用HashMap实现，提供接近O(1)的查找、插入和删除时间复杂度
     * - 使用Int类型(实体ID)作为键，而非Entity对象，减少对象创建
     * - 为每种组件类型单独维护一个映射表，实现类型隔离
     * - 初始容量根据预期实体数量自动调整
     */
    private val components = hashMapOf<Int, C>()

    /**
     * 检查实体是否拥有此类型的组件
     * 
     * 实现逻辑：
     * 1. 提取实体的ID
     * 2. 在内部HashMap中检查ID是否存在
     * 3. 返回检查结果
     * 
     * 性能特性：
     * - 时间复杂度：O(1)，直接通过HashMap的containsKey方法实现
     * - 空间复杂度：不创建新对象，仅进行简单的ID检查
     * 
     * @param entity 要检查的实体对象
     * @return 如果实体拥有此类型组件返回true，否则返回false
     */
    override operator fun contains(entity: Entity): Boolean = entity.id in components

    /**
     * 获取实体的组件，如果不存在则抛出异常
     * 
     * 实现逻辑：
     * 1. 使用实体ID作为键在HashMap中查找组件
     * 2. 如果找到组件，直接返回
     * 3. 如果未找到组件，抛出带有详细错误信息的NoSuchElementException
     * 
     * 设计意图：
     * - 提供严格的类型安全保证，确保调用者总是获得有效的组件实例
     * - 适用于已知实体一定拥有该组件的场景（如通过家族查询获得的实体）
     * - 错误信息包含实体和组件类型的详细信息，便于调试
     * 
     * @param entity 要获取组件的实体
     * @return 实体的组件实例，类型为C
     * @throws NoSuchElementException 如果实体没有此类型组件
     */
    override operator fun get(entity: Entity): C = components[entity.id] ?: throw NoSuchElementException("Entity $entity does not have component $componentType")

    /**
     * 设置实体的组件，处理组件替换和回调
     * 
     * 实现逻辑：
     * 1. 使用实体ID作为键，在HashMap中存储新组件并获取旧组件引用
     * 2. 如果存在旧组件，先调用其onDetach回调进行清理
     * 3. 然后调用新组件的onAttach回调进行初始化
     * 4. 最后返回旧组件实例供调用者处理
     * 
     * 设计要点：
     * - 遵循先分离后附加的顺序，确保状态转换的一致性
     * - 自动处理组件生命周期事件，无需调用者手动管理
     * - 通过返回旧组件，允许调用者执行额外的清理或比较操作
     * - 支持组件的添加（当旧组件为null时）和更新（当旧组件存在时）
     * 
     * @param entity 要设置组件的实体
     * @param component 新组件实例
     * @return 替换前的组件实例，如果不存在则返回null
     */
    override operator fun set(entity: Entity, component: C): C? {
        val oldComponent = components.put(entity.id, component)
        oldComponent?.onDetach(entity)
        component.onAttach(entity)
        return oldComponent
    }

    /**
     * 获取实体的组件，如果不存在则返回null
     * 
     * 实现逻辑：
     * 1. 使用实体ID作为键在HashMap中查找组件
     * 2. 如果找到组件，直接返回组件实例
     * 3. 如果未找到组件，返回null
     * 
     * 设计意图：
     * - 提供空安全的组件访问方式，避免异常处理
     * - 适用于需要条件性处理组件的场景
     * - 与Kotlin的空安全机制配合使用，简化代码
     * 
     * 性能特性：
     * - 时间复杂度：O(1)，直接利用HashMap的get方法
     * - 空间复杂度：不创建新对象，返回现有引用或null
     * 
     * @param entity 要获取组件的实体
     * @return 实体的组件实例，如果不存在则返回null
     */
    override fun getOrNull(entity: Entity): C? = components[entity.id]

    /**
     * 从实体中移除组件并调用回调
     * 
     * 实现逻辑：
     * 1. 使用实体ID从HashMap中移除并获取组件实例
     * 2. 如果组件存在，调用其onDetach回调进行清理
     * 3. 返回被移除的组件实例
     * 
     * 设计要点：
     * - 确保在组件移除前调用生命周期回调，允许组件清理资源
     * - 返回被移除的组件，支持调用者执行额外的处理
     * - 当实体没有此类型组件时，优雅地返回null而不抛出异常
     * 
     * @param entity 要移除组件的实体
     * @return 被移除的组件实例，如果不存在则返回null
     */
    override fun remove(entity: Entity): C? {
        val component = components.remove(entity.id)
        component?.onDetach(entity)
        return component
    }

    /**
     * 安全地调用组件的onAttach回调
     * 
     * 实现逻辑：
     * 1. 接收可能为null的对象引用
     * 2. 使用Kotlin的类型检查确保对象是Component类型
     * 3. 如果是Component，调用其onAttach方法并传入当前world和实体
     * 4. 如果不是Component或为null，则不执行任何操作
     * 
     * 设计意图：
     * - 提供统一的组件回调调用机制
     * - 通过类型安全的方式处理可能不是Component的情况
     * - 避免null检查的样板代码
     * - 确保组件生命周期事件的正确触发
     * 
     * @param entity 组件被附加到的实体
     */
    private fun Any?.onAttach(entity: Entity) {
        if (this is Component<*>) world.onAttach(entity)
    }

    /**
     * 安全地调用组件的onDetach回调
     * 
     * 实现逻辑：
     * 1. 接收可能为null的对象引用
     * 2. 使用Kotlin的类型检查确保对象是Component类型
     * 3. 如果是Component，调用其onDetach方法并传入当前world和实体
     * 4. 如果不是Component或为null，则不执行任何操作
     * 
     * 设计意图：
     * - 提供统一的组件回调调用机制
     * - 通过类型安全的方式处理可能不是Component的情况
     * - 避免null检查的样板代码
     * - 确保组件生命周期事件的正确触发
     * 
     * @param entity 组件被从其分离的实体
     */
    private fun Any?.onDetach(entity: Entity) {
        if (this is Component<*>) world.onDetach(entity)
    }
}