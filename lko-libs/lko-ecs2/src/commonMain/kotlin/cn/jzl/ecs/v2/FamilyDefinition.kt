/**
 * FamilyDefinition.kt 定义了实体家族的过滤条件
 * 
 * 实体家族（Family）是ECS架构中的核心概念，用于：
 * 1. 根据组件组合过滤实体
 * 2. 提供高效的实体查询机制
 * 3. 支持系统（Systems）对相关实体的处理
 * 
 * FamilyDefinition通过三种过滤器组合定义实体集合：
 * - all：实体必须包含的所有组件
 * - any：实体必须包含的任意一个组件
 * - none：实体必须不包含的所有组件
 * 
 * 使用BitSet实现高效的位运算，提高实体匹配性能
 */
package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet

/**
 * 实体家族定义类
 * 
 * 提供声明式API定义实体过滤条件，支持链式调用
 */
class FamilyDefinition {

    /**
     * 所有必须包含的组件位集
     * 
     * 实体必须拥有此位集中的所有组件才会匹配此家族
     */
    internal val allBits = BitSet()
    
    /**
     * 任意包含的组件位集
     * 
     * 实体至少拥有此位集中的一个组件才会匹配此家族
     */
    internal val anyBits = BitSet()
    
    /**
     * 必须排除的组件位集
     * 
     * 实体不能拥有此位集中的任何组件才会匹配此家族
     */
    internal val noneBits = BitSet()

    /**
     * 添加必须包含的组件类型
     * 
     * @param componentTypes 实体必须包含的组件类型列表
     * @return 当前家族定义实例，用于链式调用
     */
    fun all(vararg componentTypes: ComponentType<*>): FamilyDefinition = allBits.setComponentTypes(componentTypes)
    
    /**
     * 添加可选包含的组件类型
     * 
     * @param componentTypes 实体至少包含其中一个的组件类型列表
     * @return 当前家族定义实例，用于链式调用
     */
    fun any(vararg componentTypes: ComponentType<*>): FamilyDefinition = anyBits.setComponentTypes(componentTypes)
    
    /**
     * 添加必须排除的组件类型
     * 
     * @param componentTypes 实体必须不包含的组件类型列表
     * @return 当前家族定义实例，用于链式调用
     */
    fun none(vararg componentTypes: ComponentType<*>): FamilyDefinition = noneBits.setComponentTypes(componentTypes)

    /**
     * 扩展函数：为BitSet设置组件类型
     * 
     * 将组件类型的索引设置到位集中，用于高效的组件查询
     * 
     * @param componentTypes 要设置的组件类型数组
     * @return 所属的家族定义实例，用于链式调用
     */
    fun BitSet.setComponentTypes(componentTypes: Array<out ComponentType<*>>): FamilyDefinition {
        // 空检查，避免不必要的操作
        if (componentTypes.isEmpty()) return this@FamilyDefinition
        
        // 将每个组件类型的索引设置到位集中
        componentTypes.forEach { set(it.index) }
        
        return this@FamilyDefinition
    }

    /**
     * 检查实体是否匹配当前家族定义
     * 
     * 实现逻辑：
     * 1. 获取实体的组件位集，包含实体当前拥有的所有组件类型
     * 2. 执行三重检查策略：
     *    - ALL条件检查：确保实体包含allBits中指定的所有组件类型（子集检查）
     *    - ANY条件检查：确保实体至少包含anyBits中指定的一个组件类型（交集检查）
     *    - NONE条件检查：确保实体不包含noneBits中指定的任何组件类型（无交集检查）
     * 3. 所有条件都满足时，实体才被判定为匹配
     * 
     * 优化点：
     * - 对空条件集进行短路判断，避免不必要的位运算
     * - 使用高效的位运算（子集、交集检查）代替集合操作
     * - 检查顺序从最严格的ALL条件开始，尽早排除不匹配实体
     * 
     * @param entity 要检查的实体
     * @return 如果实体满足所有过滤条件返回true，否则返回false
     */
    internal fun Family.checkEntity(entity: Entity): Boolean {
        val componentBits = entity.componentBits
        
        // 检查1 - ALL条件：实体必须包含allBits中的所有组件
        // 如果allBits不为空且不是componentBits的子集，则实体不匹配
        if (allBits.isNotEmpty() && allBits !in componentBits) return false
        
        // 检查2 - ANY条件：实体必须至少包含anyBits中的一个组件
        // 如果anyBits不为空且与componentBits没有交集，则实体不匹配
        if (anyBits.isNotEmpty() && !anyBits.intersects(componentBits)) return false
        
        // 检查3 - NONE条件：实体不能包含noneBits中的任何组件
        // 如果noneBits不为空且与componentBits有交集，则实体不匹配
        if (noneBits.isNotEmpty() && noneBits in componentBits) return false
        
        // 所有检查都通过，实体完全匹配家族定义
        return true
    }

    /**
     * 判断两个家族定义是否相等
     * 
     * 当且仅当所有位集（allBits、anyBits、noneBits）都相等时，两个家族定义才相等
     * 
     * @param other 要比较的对象
     * @return 如果相等返回true，否则返回false
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FamilyDefinition

        if (allBits != other.allBits) return false
        if (anyBits != other.anyBits) return false
        if (noneBits != other.noneBits) return false

        return true
    }

    /**
     * 计算家族定义的哈希码
     * 
     * 基于三个位集的哈希码计算，确保相等的家族定义产生相同的哈希码
     * 
     * @return 家族定义的哈希码值
     */
    override fun hashCode(): Int {
        var result = allBits.hashCode()
        result = 31 * result + anyBits.hashCode()
        result = 31 * result + noneBits.hashCode()
        return result
    }
}