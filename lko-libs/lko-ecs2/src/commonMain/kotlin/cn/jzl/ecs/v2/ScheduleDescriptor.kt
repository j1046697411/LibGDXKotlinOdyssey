/**
 * ScheduleDescriptor 类，表示调度器的完整配置信息
 *
 * 实现逻辑：
 * 1. 作为调度系统的核心配置容器，负责管理调度器的所有元数据
 * 2. 使用位图（BitSet）高效管理组件访问权限，实现O(1)时间复杂度的权限查询
 * 3. 维护双向依赖关系追踪，支持显式依赖声明和隐式数据依赖检测
 * 4. 实现版本控制机制，通过递增版本号追踪配置变更
 * 5. 提供完整的依赖关系解析，用于构建正确的调度执行顺序
 *
 * 设计考虑：
 * - 使用不可变的Schedule作为调度器唯一标识，确保引用稳定性
 * - 分离强依赖（显式）和弱依赖（隐式），支持不同的依赖解析策略
 * - 采用位运算优化依赖冲突检测，提升大规模系统性能
 * - 版本追踪机制支持增量依赖解析，避免重复计算
 */
package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet

/**
 * 调度器描述符，包含调度器的完整配置信息
 *
 * @property schedule 调度器实例，用于唯一标识调度器
 * @property scheduleName 调度器名称，用于调试和日志记录
 */
data class ScheduleDescriptor(
    val schedule: Schedule,
    val scheduleName: String,
) {
    /**
     * 依赖的调度器名称集合
     *
     * 存储通过名称指定的依赖
     */
    private val dependencyNames = mutableSetOf<String>()

    /**
     * 依赖的调度器实例集合
     *
     * 存储直接引用的依赖调度器
     */
    private val dependencySchedules = mutableSetOf<Schedule>()

    /**
     * 组件读访问权限位图
     *
     * 使用BitSet高效存储和查询组件读权限
     */
    private val readAccessesBits = BitSet(256)

    /**
     * 组件写访问权限位图
     *
     * 使用BitSet高效存储和查询组件写权限
     */
    private val writeAccessesBits = BitSet(256)

    /**
     * 描述符版本号
     *
     * 当描述符配置变更时版本号递增，用于检测依赖关系是否需要重新计算
     */
    var version: Int = 0
        private set

    /**
     * 添加家族定义到调度器描述符
     *
     * 实现步骤：
     * 1. 提取家族定义中的必须组件位图（allBits）和任一组件位图（anyBits）
     * 2. 使用位运算OR操作将这些组件位图合并到读访问权限位图
     * 3. 递增版本号，表示配置已变更，触发依赖关系重新计算
     *
     * 设计理念：
     * - 家族定义需要读取指定组件才能过滤实体，因此自动获得这些组件的读访问权限
     * - 位运算合并位图确保高效的权限集合操作
     * - 版本控制确保依赖关系实时更新
     *
     * @param familyDefinition 家族定义实例，包含实体过滤的组件条件
     */
    internal fun addFamilyDefinition(familyDefinition: FamilyDefinition) {
        // 添加家族定义中的所有必须组件位和任一组件位到读访问权限
        readAccessesBits.or(familyDefinition.allBits)
        readAccessesBits.or(familyDefinition.anyBits)
        // 增加版本号，表示配置已变更
        version++
    }

    /**
     * 添加只读访问权限到调度器描述符
     *
     * 实现步骤：
     * 1. 从readAccess中获取组件类型的索引值
     * 2. 在readAccessesBits位图中设置对应位为1
     * 3. 递增版本号，表示配置已变更
     *
     * 设计理念：
     * - 通过组件索引作为位位置，实现高效的权限存储
     * - 位操作确保O(1)时间复杂度的权限设置
     * - 显式声明读权限便于依赖冲突检测
     *
     * @param readAccess 只读访问权限实例，包含要读取的组件类型
     */
    internal fun addReadAccess(readAccess: ComponentReadAccesses<*>) {
        // 在读访问权限位图中设置对应组件类型的位
        readAccessesBits.set(readAccess.type.index)
        // 增加版本号，表示配置已变更
        version++
    }

    /**
     * 添加写访问权限到调度器描述符
     *
     * 实现步骤：
     * 1. 从writeAccess中获取组件类型的索引值
     * 2. 在writeAccessesBits位图中设置对应位为1
     * 3. 递增版本号，表示配置已变更
     *
     * 设计理念：
     * - 写操作需要特殊权限控制，避免数据竞争
     * - 写访问权限在依赖检测中被视为强冲突
     * - 版本控制确保依赖关系实时更新
     *
     * 注意：虽然写访问隐含读访问权限，但此处仅设置写访问位图，
     * 读访问权限会在实际依赖计算时被考虑
     *
     * @param writeAccess 写访问权限实例，包含要修改的组件类型
     */
    internal fun addWriteAccess(writeAccess: ComponentWriteAccesses<*>) {
        // 在写访问权限位图中设置对应组件类型的位
        writeAccessesBits.set(writeAccess.type.index)
        // 增加版本号，表示配置已变更
        version++
    }

    /**
     * 声明此调度器依赖于其他调度器描述符
     *
     * 实现步骤：
     * 1. 检查输入参数是否为空，避免不必要的处理
     * 2. 遍历所有依赖的调度器描述符
     * 3. 将每个描述符的schedule实例添加到dependencySchedules集合
     * 4. 递增版本号，表示配置已变更
     * 5. 返回this引用，支持链式调用API设计
     *
     * 设计理念：
     * - 显式依赖声明确保调度器按正确顺序执行
     * - 使用引用而不是名称，提供更强的类型安全
     * - 空检查优化性能，避免对空集合的遍历
     * - 链式调用设计提供流畅的API体验
     *
     * @param scheduleDescriptors 依赖的调度器描述符列表
     * @return 当前调度器描述符，用于链式调用
     */
    fun dependsOn(vararg scheduleDescriptors: ScheduleDescriptor): ScheduleDescriptor = apply {
        // 空检查，避免不必要的操作
        if (scheduleDescriptors.isEmpty()) return this@ScheduleDescriptor

        // 添加所有依赖的调度器实例
        scheduleDescriptors.forEach { dependencySchedules.add(it.schedule) }

        // 增加版本号，表示配置已变更
        version++
    }

    /**
     * 声明此调度器依赖于指定名称的调度器
     *
     * 实现步骤：
     * 1. 检查输入参数是否为空，避免不必要的处理
     * 2. 使用addAll批量添加所有调度器名称到dependencyNames集合
     * 3. 递增版本号，表示配置已变更
     * 4. 返回this引用，支持链式调用API设计
     *
     * 设计理念：
     * - 通过名称引用支持更灵活的配置，特别是在循环依赖场景
     * - 延迟解析机制，允许依赖尚未创建的调度器
     * - 集合批量操作提升性能
     * - 版本追踪确保依赖关系实时更新
     *
     * @param scheduleNames 依赖的调度器名称列表
     * @return 当前调度器描述符，用于链式调用
     */
    fun dependsOn(vararg scheduleNames: String): ScheduleDescriptor = apply {
        // 空检查，避免不必要的操作
        if (scheduleNames.isEmpty()) return this@ScheduleDescriptor

        // 添加所有依赖的调度器名称
        dependencyNames.addAll(scheduleNames)

        // 增加版本号，表示配置已变更
        version++
    }

    /**
     * 检查当前调度器是否依赖于另一个调度器
     *
     * 实现步骤：
     * 1. 首先检查自依赖情况，避免无效的自我依赖判断
     * 2. 检查显式依赖：通过实例引用检测直接依赖关系
     * 3. 检查名称依赖：通过名称检测间接依赖关系
     * 4. 检查隐式依赖（数据依赖）：
     *    - 使用位运算intersects检测位图交叉情况
     *    - 当前读 & 另一个写：需要在写入后读取
     *    - 当前写 & 另一个读：需要在读前写入
     *    - 当前写 & 另一个写：避免同时写入冲突
     * 5. 任意依赖条件满足则返回true
     *
     * 设计理念：
     * - 优先检查显式依赖，通常这类依赖优先级更高
     * - 使用位运算优化依赖冲突检测性能
     * - 全面的依赖检测确保调度顺序正确性，避免数据竞争
     * - 自依赖保护防止循环引用错误
     *
     * @param other 另一个调度器描述符
     * @return 如果当前调度器依赖于另一个调度器，则返回true；否则返回false
     */
    internal fun isDependency(other: ScheduleDescriptor): Boolean {
        // 避免自依赖
        if (schedule == other.schedule) return false

        // 检查显式依赖：当前是否直接依赖于另一个调度器
        if (other.schedule in dependencySchedules) return true

        // 检查名称依赖：当前是否依赖于另一个调度器的名称
        if (other.scheduleName in dependencyNames) return true

        // 检查隐式依赖：
        // 1. 当前读 & 另一个写
        if (readAccessesBits.intersects(other.writeAccessesBits)) return true

        // 2. 当前写 & 另一个读（数据竞争防护）
        if (writeAccessesBits.intersects(other.readAccessesBits)) return true

        // 3. 当前写 & 另一个写（写冲突防护）
        if (writeAccessesBits.intersects(other.writeAccessesBits)) return true

        return false
    }

    /**
     * 检查当前调度器是否强依赖于另一个调度器
     *
     * 实现步骤：
     * 1. 首先检查自依赖情况，避免无效的自我依赖判断
     * 2. 仅检查显式依赖关系：
     *    - 通过调度器实例直接引用检测
     *    - 通过调度器名称间接引用检测
     * 3. 不考虑隐式数据依赖
     *
     * 设计理念：
     * - 区分强依赖和弱依赖，支持不同的依赖解析策略
     * - 强依赖在循环依赖检测和解决中有特殊意义
     * - 显式声明的依赖通常表示明确的执行顺序要求
     * - 此方法用于复杂依赖图分析和优化
     *
     * @param other 另一个调度器描述符
     * @return 如果当前调度器强依赖于另一个调度器，则返回true；否则返回false
     */
    internal fun isStrongDependency(other: ScheduleDescriptor): Boolean {
        // 避免自依赖
        if (schedule == other.schedule) return false

        // 检查显式依赖：通过调度器实例
        if (other.schedule in dependencySchedules) return true

        // 检查显式依赖：通过调度器名称
        if (other.scheduleName in dependencyNames) return true

        return false
    }
}