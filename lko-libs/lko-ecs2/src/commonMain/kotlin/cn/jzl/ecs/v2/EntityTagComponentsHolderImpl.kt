package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet

/**
 * EntityTagComponentsHolderImpl - 实体标签组件持有器的专用实现
 * 
 * 实现逻辑：
 * 1. 使用BitSet代替HashMap存储实体标签状态，大幅优化内存占用
 * 2. 通过位运算实现O(1)复杂度的标签查询、设置和移除操作
 * 3. 专门针对布尔值类型的组件（标签）进行优化设计
 * 4. 直接使用实体ID作为BitSet的索引，避免对象创建和额外的映射
 * 
 * 设计考虑：
 * - 内存效率：对于大量实体的标签管理，BitSet比HashMap节省大量内存
 * - 性能优化：位运算操作比哈希表查找更高效，尤其在大量实体的场景下
 * - 类型适配：专为EntityTag类型的组件优化，处理布尔状态而非复杂对象
 * - 操作简化：提供简洁的标签管理接口，适合标记性组件的快速操作
 * 
 * 与普通组件持有器的区别：
 * - 存储方式不同：使用BitSet而非HashMap
 * - 数据类型简化：只处理布尔值状态
 * - 内存占用更小：每个实体只需1位存储空间
 * - 操作更高效：位运算比哈希操作更快
 */
internal class EntityTagComponentsHolderImpl(override val componentType: EntityTag) : ComponentsHolder<Boolean> {
    /**
     * 使用BitSet存储拥有此标签的实体ID
     * 
     * 数据结构特性：
     * - 使用BitSet实现，每个位表示一个实体的标签状态
     * - 位索引对应实体ID，值为1表示拥有标签，0表示不拥有
     * - 自动扩展容量，无需手动管理
     * - 支持位操作优化，包括批量查询和设置
     * 
     * 内存优化：
     * - 每个实体的标签状态只占用1位，相比HashMap节省约30-50倍内存
     * - 对于大量实体（如10000+），内存节省效果尤为明显
     * - 连续的实体ID会进一步提高内存利用率
     */
    private val entityTagBits = BitSet.Companion()
    
    /**
     * 检查实体是否拥有此标签
     * 
     * 实现逻辑：
     * 1. 提取实体的ID
     * 2. 在BitSet中检查对应索引位置的值
     * 3. 如果位为1返回true，否则返回false
     * 
     * 性能特性：
     * - 时间复杂度：O(1)，直接进行位检查操作
     * - 空间复杂度：不创建新对象，仅进行位运算
     * - 对于大量实体的标签检查，比哈希表查询更高效
     * 
     * @param entity 要检查的实体
     * @return 如果实体拥有此标签返回true，否则返回false
     */
    override fun contains(entity: Entity): Boolean = entity.id in entityTagBits
    
    /**
     * 获取实体的标签状态
     * 
     * 实现逻辑：
     * 1. 与contains方法实现相同，提取实体ID并检查BitSet中的对应位
     * 2. 由于标签是布尔类型，直接返回位状态作为组件值
     * 
     * 设计意图：
     * - 遵循ComponentsHolder接口规范，提供统一的组件获取方式
     * - 对于标签类型组件，组件值即为标签状态（布尔值）
     * - 保持API一致性，便于与普通组件处理逻辑统一
     * 
     * @param entity 要获取标签状态的实体
     * @return 实体是否拥有此标签的布尔值
     */
    override fun get(entity: Entity): Boolean = entity.id in entityTagBits
    
    /**
     * 为实体设置标签状态
     * 
     * 实现逻辑：
     * 1. 首先检查实体当前是否已拥有标签（获取旧状态）
     * 2. 根据提供的component参数（布尔值）设置实体的标签状态
     * 3. 如果component为true，将对应位设为1；如果为false，设为0
     * 4. 返回之前的标签状态（是否拥有标签）
     * 
     * 设计要点：
     * - component参数作为布尔值，直接映射到标签状态
     * - 自动处理BitSet的容量扩展，无需手动干预
     * - 返回旧状态，允许调用者了解标签状态是否发生变化
     * - 同时支持添加（设为true）和移除（设为false）标签操作
     * 
     * @param entity 要设置标签的实体
     * @param component 标签状态（true表示添加，false表示移除）
     * @return 如果实体之前已有标签返回true，否则返回false
     */
    override fun set(entity: Entity, component: Boolean): Boolean? {
        val old = entity.id in entityTagBits
        entityTagBits[entity.id] = component
        return old
    }

    /**
     * 获取实体的标签状态，如果不存在则返回null
     * 
     * 实现逻辑：
     * 1. 与contains和get方法类似，检查BitSet中实体ID对应的位
     * 2. 注意：标签组件总是返回布尔值，不会返回null，因为标签状态只有拥有或不拥有
     * 
     * 设计考量：
     * - 虽然方法名暗示可能返回null，但对于标签组件，总是返回布尔值
     * - 这样设计是为了遵循ComponentsHolder接口规范
     * - 在标签系统中，"不存在"等同于"拥有状态为false"
     * 
     * @param entity 要获取标签状态的实体
     * @return 实体的标签状态（布尔值）
     */
    override fun getOrNull(entity: Entity): Boolean? = entity.id in entityTagBits
    
    /**
     * 从实体中移除标签
     * 
     * 实现逻辑：
     * 1. 首先检查实体当前是否拥有标签（记录旧状态）
     * 2. 通过clear方法清除BitSet中对应实体ID的位
     * 3. 返回之前的标签状态，指示是否实际移除了标签
     * 
     * 设计要点：
     * - 提供明确的标签移除操作，与set方法设为false功能相同
     * - 返回旧状态，便于调用者确定操作是否实际改变了标签状态
     * - 使用BitSet的clear方法，比手动设置为0更高效
     * 
     * @param entity 要移除标签的实体
     * @return 如果实体之前有标签返回true，否则返回false
     */
    override fun remove(entity: Entity): Boolean? {
        val oldEntityTag = entity.id in entityTagBits
        entityTagBits.clear(entity.id)
        return oldEntityTag
    }
}