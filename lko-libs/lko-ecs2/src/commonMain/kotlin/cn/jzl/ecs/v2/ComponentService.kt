/**
 * 组件服务类，负责管理ECS系统中的组件类型和组件实例
 * 
 * 实现逻辑：
 * 1. 使用延迟初始化策略，只有在实际需要时才为实体分配组件位集合
 * 2. 为每种组件类型维护一个专用的组件持有器
 * 3. 通过组件位集合快速判断实体是否拥有特定组件
 * 4. 提供统一的组件访问接口，确保类型安全
 * 
 * 设计考虑：
 * - 性能优化：使用稀疏数组和延迟初始化，减少内存占用
 * - 类型安全：通过泛型确保组件操作的类型安全
 * - 可扩展性：支持不同类型的组件持有器实现
 * - 高效查询：使用位集合实现O(1)复杂度的组件存在性检查
 * 
 * 内存使用策略：
 * - 使用稀疏数组（ObjectFastList）存储组件位集合和组件持有器
 * - 只有实际使用的实体和组件类型才会分配内存
 * - 位集合（BitSet）提供紧凑的组件类型表示
 */
package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.ObjectFastList

/**
 * 组件服务，管理组件类型和组件实例
 * 
 * @property world World实例，提供对ECS系统其他部分的访问
 */
class ComponentService(@PublishedApi internal val world: World) {
    
    /**
     * 实体组件位集合，每个实体有一个对应的BitSet，表示其拥有的组件类型
     * 
     * 数据结构特性：
     * - 使用ObjectFastList作为稀疏数组实现，高效利用内存
     * - 索引为实体ID，值为该实体的组件位集合
     * - 只有实际分配了组件的实体会在此集合中占用空间
     * - 每个BitSet的每一位代表一种组件类型，1表示拥有，0表示不拥有
     * 
     * 性能特性：
     * - 提供O(1)复杂度的组件存在性检查
     * - 通过ensureCapacity方法确保动态扩展能力
     * - 支持快速的组件集合操作（交集、并集、差集）
     */
    private val componentBits = ObjectFastList<BitSet?>()
    
    /**
     * 组件持有器集合，每种组件类型有一个对应的持有器
     * 
     * 数据结构特性：
     * - 使用ObjectFastList作为稀疏数组实现
     * - 索引为组件类型索引，值为该类型的组件持有器
     * - 只有实际使用的组件类型才会在此集合中占用空间
     * - 支持不同类型的组件持有器实现（如普通组件和实体标签）
     * 
     * 设计考虑：
     * - 通过组件类型索引快速查找对应的持有器
     * - 延迟创建持有器，减少不必要的内存分配
     * - 支持组件类型的动态注册和扩展
     */
    private val componentsHolders = ObjectFastList<ComponentsHolder<*>?>()

    /**
     * 获取实体的组件位集合，用于表示实体拥有的所有组件类型
     * 
     * 实现逻辑：
     * 1. 首先确保componentBits数组容量足够大，避免索引越界异常
     * 2. 检查实体ID位置是否已有对应的BitSet
     * 3. 如果没有，则创建一个新的BitSet实例并存储到对应位置
     * 4. 返回实体的组件位集合
     * 
     * 设计要点：
     * - 采用延迟初始化策略，只有在需要时才为实体创建组件位集合
     * - 位集合中，组件类型的索引对应位的位置，值为1表示拥有该组件
     * - 使用ensureCapacity方法确保数组可以安全扩展
     * - 这种设计允许快速判断实体是否拥有特定组件（O(1)复杂度）
     * 
     * @param entity 需要获取组件位集合的实体
     * @return 实体的组件位集合，永远不会返回null
     */
    fun componentBits(entity: Entity): BitSet {
        // 确保数组容量足够，避免索引越界
        componentBits.ensureCapacity(entity.id + 1, null)
        
        // 如果实体还没有组件位集合，创建一个新的
        return componentBits[entity.id] ?: run {
            val bits = BitSet.Companion()
            componentBits[entity.id] = bits
            bits
        }
    }

    /**
     * 获取指定组件类型的持有器
     * 
     * 实现逻辑：
     * 1. 首先尝试获取现有持有器
     * 2. 如果持有器不存在，则根据组件类型创建适当的实现：
     *    - 对于EntityTag类型，创建EntityTagComponentsHolderImpl
     *    - 对于普通Component类型，创建ComponentsHolderImpl
     * 3. 确保componentsHolders数组容量足够，并存储新创建的持有器
     * 4. 返回组件持有器（进行安全的类型转换）
     * 
     * 设计考虑：
     * - 延迟创建模式：只有在实际需要时才为组件类型创建持有器
     * - 多态设计：支持不同类型的组件持有器实现
     * - 类型安全：通过类型参数确保返回的持有器类型正确
     * - 性能优化：使用组件类型索引作为映射键，提供快速访问
     * 
     * @param componentType 组件类型
     * @return 对应组件类型的持有器
     */
    @Suppress("UNCHECKED_CAST")
    fun <C> holder(componentType: ComponentType<C>): ComponentsHolder<C> {
        return holderOrNull(componentType.index) ?: run {
            // 根据组件类型创建适当的持有器
            val componentsHolder = if (componentType is EntityTag) {
                EntityTagComponentsHolderImpl(componentType)
            } else {
                ComponentsHolderImpl(world, componentType)
            }
            
            // 确保数组容量足够
            componentsHolders.ensureCapacity(componentType.index + 1, null)
            componentsHolders[componentType.index] = componentsHolder
            componentsHolder as ComponentsHolder<C>
        }
    }

    /**
     * 尝试获取指定组件索引的持有器，但不创建新的
     * 
     * 实现逻辑：
     * 1. 尝试从componentsHolders数组中获取指定索引位置的持有器
     * 2. 如果持有器不存在，直接返回null（与holder方法不同，不创建新持有器）
     * 3. 执行索引验证，确保返回的持有器索引与请求的索引一致
     * 4. 进行安全的类型转换并返回结果
     * 
     * 安全措施：
     * - 执行严格的索引匹配检查，防止类型混淆
     * - 如果索引不匹配，抛出IllegalArgumentException异常
     * - 使用as?操作符进行安全类型转换，避免类型转换异常
     * 
     * 设计意图：
     * - 提供只读访问方式，不会修改内部状态
     * - 允许检查组件类型是否已注册对应的持有器
     * - 支持条件性的组件持有器访问
     * 
     * @param componentIndex 组件索引
     * @return 组件持有器，如果不存在则返回null
     * @throws IllegalArgumentException 如果组件索引与持有器索引不匹配
     */
    @Suppress("UNCHECKED_CAST")
    fun <C> holderOrNull(componentIndex: Int): ComponentsHolder<C>? {
        val holder = componentsHolders.getOrNull(componentIndex) ?: return null
        
        // 验证组件索引与持有器索引匹配，确保类型安全
        check(holder.componentType.index == componentIndex) {
            "Component index $componentIndex does not match holder index ${holder.componentType.index}"
        }
        
        return holder as? ComponentsHolder<C>
    }
}