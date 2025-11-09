/**
 * Entity 类，表示ECS系统中的实体
 * 
 * 实体是ECS架构中的基本对象标识符，本身不包含任何数据或逻辑
 * 它只是一个唯一的标识符，用于关联组件
 * 
 * 此实现使用value class和JvmInline优化性能，将实体表示为单个Long值
 * 其中低32位存储实体ID，高32位存储版本号
 * 
 * 版本号用于在实体被重用时确保引用安全，防止悬挂引用
 */
package cn.jzl.ecs.v2

import cn.jzl.datastructure.math.fromLowHigh
import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.low
import kotlin.jvm.JvmInline

/**
 * 实体值类，将实体ID和版本号编码到单个Long值中
 * 
 * 实现逻辑：
 * 1. 使用Kotlin的value class和JvmInline注解进行性能优化，避免运行时装箱
 * 2. 采用位编码技术，将ID和版本号压缩到单个Long值中，节省内存
 * 3. 使用私有构造器和伴生对象工厂方法，确保实体创建的一致性
 * 4. 提供解构功能，支持直接访问ID和版本号属性
 * 
 * 这种紧凑的表示方式在大型游戏中特别重要，可以：
 * - 减少内存占用，每个实体只占用8字节
 * - 提高缓存命中率，因为更多实体可以同时加载到CPU缓存中
 * - 简化垃圾回收，实体引用不涉及复杂对象
 * - 支持高效的实体比较和查找操作
 * 
 * @property data 内部存储的Long值，低32位为ID，高32位为版本号
 */
@JvmInline
value class Entity private constructor(@PublishedApi internal val data: Long) {
    /**
     * 实体的唯一标识符
     * 
     * ID特性：
     * - 从内部Long值的低32位提取
     * - 在实体创建时分配，通常是顺序递增的
     * - 在实体销毁后可以被重用，但会关联新的版本号
     * - 用于在实体存储中定位实体
     */
    val id: Int get() = data.low
    
    /**
     * 实体的版本号
     * 
     * 版本号特性：
     * - 从内部Long值的高32位提取
     * - 当实体被销毁并重用时，版本号会递增
     * - 用于防止悬挂引用问题
     * - 确保即使ID被重用，旧的实体引用也无法访问新实体
     */
    val version: Int get() = data.high

    /**
     * 将实体转换为可读的字符串表示
     * 
     * 这个方法对于调试和日志记录非常有用，它提供了实体的完整标识信息，
     * 包括ID和版本号，便于跟踪实体的生命周期和状态。
     * 
     * @return 包含实体ID和版本号的格式化字符串
     */
    override fun toString(): String = "Entity(id=$id, version=$version)"

    companion object {
        /**
         * 表示无效实体的常量
         * 
         * NONE常量特性：
         * - 使用-1作为内部数据值，确保其ID为-1，版本号为-1（对于32位有符号整数）
         * - 用于初始化变量，表示"无实体"状态
         * - 作为方法返回的默认值，表示未找到符合条件的实体
         * - 可以与其他实体实例安全比较
         */
        val NONE = Entity(-1)
        
        /**
         * 创建新的实体实例 - 实体工厂方法
         * 
         * 实现逻辑：
         * 1. 接收实体ID和可选的版本号参数
         * 2. 使用fromLowHigh工具函数将ID和版本号合并为单个Long值
         * 3. 调用私有构造器创建实体实例
         * 4. 返回创建的实体实例
         * 
         * 这个工厂方法是创建实体实例的唯一公共途径，它确保了实体的正确初始化
         * 并隐藏了内部位操作的复杂性，提供了简洁的API。
         * 
         * @param id 实体ID，通常由实体存储分配
         * @param version 实体版本号，默认为0，在实体重用时会递增
         * @return 新创建的实体实例，包含指定的ID和版本号
         */
        operator fun invoke(id: Int, version: Int = 0) = Entity(Long.Companion.fromLowHigh(id, version))
    }
}