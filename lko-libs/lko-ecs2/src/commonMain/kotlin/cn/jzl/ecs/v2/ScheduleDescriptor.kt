package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet

/**
 * 调度器描述符，包含调度器的完整配置信息
 *
 * @property schedule 调度器实例
 * @property scheduleName 调度器名称
 */
data class ScheduleDescriptor(
    val schedule: Schedule,
    val scheduleName: String,
) {
    private val dependencyNames = mutableSetOf<String>()
    private val dependencySchedules = mutableSetOf<Schedule>()

    private val readAccessesBits = BitSet(256)
    private val writeAccessesBits = BitSet(256)

    var version: Int = 0
        private set

    /**
     * 添加家族定义到调度器描述符
     *
     * @param familyDefinition 家族定义实例
     */
    internal fun addFamilyDefinition(familyDefinition: FamilyDefinition) {
        readAccessesBits.or(familyDefinition.allBits)
        readAccessesBits.or(familyDefinition.anyBits)
        version++
    }

    /**
     * 添加只读访问权限到调度器描述符
     *
     * @param readAccess 只读访问权限实例
     */
    internal fun addReadAccess(readAccess: ComponentReadAccesses<*>) {
        readAccessesBits.set(readAccess.type.index)
        version++
    }

    /**
     * 添加写访问权限到调度器描述符
     *
     * @param writeAccess 写访问权限实例
     */
    internal fun addWriteAccess(writeAccess: ComponentWriteAccesses<*>) {
        writeAccessesBits.set(writeAccess.type.index)
        version++
    }

    fun dependsOn(vararg scheduleDescriptors: ScheduleDescriptor): ScheduleDescriptor = apply {
        if (scheduleDescriptors.isEmpty()) return this@ScheduleDescriptor
        scheduleDescriptors.forEach { dependencySchedules.add(it.schedule) }
        version++
    }

    fun dependsOn(vararg scheduleNames: String): ScheduleDescriptor = apply {
        if (scheduleNames.isEmpty()) return this@ScheduleDescriptor
        dependencyNames.addAll(scheduleNames)
        version++
    }

    /**
     * 检查当前调度器是否依赖于另一个调度器
     *
     * 如果存在以下情况，则认为当前调度器依赖于另一个调度器：
     * 1. 当前调度器读取某个组件，而另一个调度器写入该组件
     * 2. 当前调度器写入某个组件，而另一个调度器读取或写入该组件
     *
     * @param other 另一个调度器描述符
     * @return 如果当前调度器依赖于另一个调度器，则返回true；否则返回false
     */
    internal fun isDependency(other: ScheduleDescriptor): Boolean {
        // 避免自依赖
        if (schedule == other.schedule) return false
        // 检查依赖关系：当前是否依赖于另一个调度器
        if (other.schedule in dependencySchedules) return true
        // 检查依赖关系：当前是否依赖于另一个调度器的名称
        if (other.scheduleName in dependencyNames) return true
        // 检查读取依赖：当前读 & 另一个写
        if (readAccessesBits.intersects(other.writeAccessesBits)) return true
        // 检查写入依赖：当前写 & 另一个写
        if (writeAccessesBits.intersects(other.writeAccessesBits)) return true
        return false
    }

    /**
     * 检查当前调度器是否强依赖于另一个调度器
     *
     * @param other 另一个调度器描述符
     * @return 如果当前调度器强依赖于另一个调度器，则返回true；否则返回false
     */
    internal fun isStrongDependency(other: ScheduleDescriptor): Boolean {
        if (schedule == other.schedule) return false
        if (other.schedule in dependencySchedules) return true
        if (other.scheduleName in dependencyNames) return true
        return false
    }
}