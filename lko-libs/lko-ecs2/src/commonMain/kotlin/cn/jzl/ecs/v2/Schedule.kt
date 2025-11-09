/**
 * Schedule.kt 定义了调度器的唯一标识符类型
 * 
 * 实现逻辑：
 * 1. 作为调度系统的核心标识类，使用value class特性实现零开销抽象
 * 2. 通过单个Long值存储64位数据，高效压缩存储ID和版本信息
 * 3. 使用位操作分离低位ID和高位版本号，实现高效数据访问
 * 4. 提供伴生对象工厂方法，简化实例创建
 * 
 * 设计考虑：
 * - value class确保运行时无额外内存开销，直接操作底层Long值
 * - 64位数据结构允许最多2^32个调度器ID和2^32个版本号
 * - 版本控制机制支持调度器配置变更追踪和依赖关系重新计算
 * - 内部构造器限制，确保实例创建的一致性和可控性
 */
package cn.jzl.ecs.v2

import cn.jzl.datastructure.math.fromLowHigh
import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.low
import kotlin.jvm.JvmInline

/**
 * 表示一个调度器，用于管理ECS系统中的任务调度
 *
 * 实现逻辑：
 * 1. 利用Kotlin value class的零开销抽象，直接映射到Long类型
 * 2. 通过位操作将64位数据分为两部分：
 *    - 低位32位：存储调度器ID
 *    - 高位32位：存储调度器版本号
 * 3. 提供只读属性访问ID和版本号，确保数据一致性
 * 4. 重写toString方法提供可读的调试信息
 *
 * 设计考虑：
 * - 不可变性设计确保调度器标识的稳定性
 * - 内部构造器标记为internal，限制实例创建途径
 * - value class在JVM上被编译为基本类型操作，避免装箱开销
 * - 位操作提供O(1)时间复杂度的属性访问
 *
 * @property data 内部存储的64位数据，包含调度器的ID和版本信息
 */
@JvmInline
value class Schedule internal constructor(private val data: Long) {
    /**
     * 调度器的唯一标识符
     * 
     * 实现逻辑：
     * - 利用扩展函数low从data的低位32位提取ID值
     * - 提供只读访问，确保ID的不可变性
     * 
     * 设计考虑：
     * - 32位ID空间能够满足大型ECS系统的需求
     * - 位操作提供高效的数据提取
     * - 在整个系统中确保全局唯一性
     */
    val id: Int get() = data.low

    /**
     * 调度器的版本号，用于版本控制
     * 
     * 实现逻辑：
     * - 利用扩展函数high从data的高位32位提取版本号
     * - 提供只读访问，确保版本号的一致性
     * 
     * 设计考虑：
     * - 版本号用于检测调度器配置变更
     * - 当调度器依赖关系或组件访问权限变化时，版本号递增
     * - 版本控制确保依赖关系重新计算的触发机制
     */
    val version: Int get() = data.high

    override fun toString(): String = "Schedule(id = $id, version = $version)"

    companion object {
        /**
         * 创建一个新的Schedule实例
         * 
         * 实现步骤：
         * 1. 接收调度器ID和版本号参数
         * 2. 使用fromLowHigh工具函数将32位ID和32位版本号合并为64位Long值
         * 3. 创建并返回新的Schedule实例
         * 
         * 设计理念：
         * - 使用operator invoke语法提供简洁的工厂方法
         * - 默认版本号为0，简化初始化流程
         * - 版本号参数允许高级场景下的版本精确控制
         * - 位合并操作确保ID和版本信息的正确存储
         * 
         * @param id 调度器的唯一标识符
         * @param version 调度器的版本号，默认为0
         * @return 新创建的Schedule实例
         */
        operator fun invoke(id: Int, version: Int = 0): Schedule = Schedule(Long.Companion.fromLowHigh(id, version))
    }
}