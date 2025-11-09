package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.ObjectFastList
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlin.sequences.forEach

internal class DependencyGraph {

    private val nodeRegistry = ObjectFastList<ScheduleNode?>(256)
    private val structureLock = ReentrantLock()

    private fun getOrCreateNode(scheduleDescriptor: ScheduleDescriptor): ScheduleNode {
        val schedule = scheduleDescriptor.schedule
        val scheduleId = schedule.id

        // 快速路径：直接获取已存在的节点
        val existingNode = nodeRegistry.getOrNull(scheduleId)
        if (existingNode != null) return existingNode

        // 慢速路径：创建新节点
        return structureLock.withLock {
            nodeRegistry.getOrNull(scheduleId) ?: run {
                nodeRegistry.ensureCapacity(scheduleId + 1, null)
                val node = ScheduleNode(schedule, scheduleDescriptor)
                nodeRegistry[scheduleId] = node
                node
            }
        }
    }

    fun updateDependencies(scheduleDescriptor: ScheduleDescriptor) {
        val scheduleNode = getOrCreateNode(scheduleDescriptor)
        if (scheduleNode.requiresUpdate(scheduleDescriptor)) {
            structureLock.withLock {
                resetNodeDependencies(scheduleNode, scheduleDescriptor)
                updateNodeDependencies(scheduleNode)
            }
        }
    }

    fun getScheduleNode(scheduleId: Int): ScheduleNode? = nodeRegistry.getOrNull(scheduleId)

    private fun updateNodeDependencies(scheduleNode: ScheduleNode) {
        val scheduleDescriptor = scheduleNode.scheduleDescriptor
        for (i in 0 until nodeRegistry.size) {
            val otherNode = nodeRegistry.getOrNull(i) ?: continue
            if (otherNode === scheduleNode) continue

            val otherScheduleDescriptor = otherNode.scheduleDescriptor

            // 检查双向依赖关系
            if (scheduleDescriptor.isDependency(otherScheduleDescriptor)) {
                scheduleNode.addDependency(otherNode)
            }

            if (otherScheduleDescriptor.isDependency(scheduleDescriptor)) {
                otherNode.addDependency(scheduleNode)
            }
        }
    }

    private fun resetNodeDependencies(scheduleNode: ScheduleNode, scheduleDescriptor: ScheduleDescriptor) {
        // 清除其他节点对此节点的依赖
        scheduleNode.dependentNodes.forEach { nodeId ->
            val node = nodeRegistry.getOrNull(nodeId) ?: return@forEach
            node.dependencies.clear(scheduleNode.schedule.id)
            node.hardDependencies.clear(scheduleNode.schedule.id)
        }

        // 重置此节点的依赖关系
        scheduleNode.resetDependencies(scheduleDescriptor)
    }

    data class ScheduleNode(
        var schedule: Schedule,
        var scheduleDescriptor: ScheduleDescriptor,
        val hardDependencies: BitSet = BitSet.Companion(256),
        val dependencies: BitSet = BitSet.Companion(256),
        val dependentNodes: BitSet = BitSet.Companion(256),
        var descriptorVersion: Int = -1
    ) {
        val executionState = atomic(STATE_PENDING)

        fun updateState(state: Int) {
            executionState.value = state
        }

        fun requiresUpdate(newScheduleDescriptor: ScheduleDescriptor): Boolean {
            return schedule != newScheduleDescriptor.schedule || descriptorVersion != newScheduleDescriptor.version
        }

        fun addDependency(otherNode: ScheduleNode) {
            val otherScheduleId = otherNode.schedule.id

            dependencies.set(otherScheduleId)
            if (scheduleDescriptor.isStrongDependency(otherNode.scheduleDescriptor)) {
                hardDependencies.set(otherScheduleId)
            }

            otherNode.dependentNodes.set(schedule.id)
        }

        fun resetDependencies(newScheduleDescriptor: ScheduleDescriptor) {
            this.schedule = newScheduleDescriptor.schedule
            this.scheduleDescriptor = newScheduleDescriptor
            hardDependencies.clear()
            dependencies.clear()
            dependentNodes.clear()
            descriptorVersion = newScheduleDescriptor.version
        }

        companion object {
            const val STATE_PENDING = 0
            const val STATE_READY = 1
            const val STATE_RUNNING = 2
            const val STATE_FINISHED = 3
        }
    }
}