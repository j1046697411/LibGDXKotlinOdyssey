package cn.jzl.ecs.v2

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/**
 * 调度器作用域接口的实现类
 *
 * @property world 世界实例
 * @property schedule 调度器实例
 * @property scheduleName 调度器名称
 * @property scheduleDispatcher 调度器分发器
 */
internal class ScheduleScopeImpl(
    override val world: World,
    schedule: Schedule,
    scheduleName: String,
    private val scheduleDispatcher: ScheduleDispatcher,
    private val schedulePriority: ScheduleTaskPriority
) : ScheduleScope {

    override val scheduleDescriptor: ScheduleDescriptor = ScheduleDescriptor(schedule, scheduleName)

    /**
     * 当前调度器实例
     */
    override val schedule: Schedule get() = scheduleDescriptor.schedule

    /**
     * 获取对指定组件类型的写访问权限
     *
     * @param C 组件类型参数
     * @return 写访问权限实例
     */
    override val <C> ComponentType<C>.write: ComponentWriteAccesses<C>
        get() = ComponentWriteAccessesImpl(this).also { scheduleDescriptor.addWriteAccess(it) }

    /**
     * 获取对指定组件类型的只读访问权限
     *
     * @param C 组件类型参数
     * @return 只读访问权限实例
     */
    override val <C> ComponentType<C>.read: ComponentReadAccesses<C>
        get() = ComponentReadAccessesImpl(this).also { scheduleDescriptor.addReadAccess(it) }

    /**
     * 定义并创建一个实体家族
     *
     * @param configuration 家族配置的DSL函数
     * @return 创建的家族实例
     */
    override fun family(configuration: FamilyDefinition.() -> Unit): Family {
        return world.familyService.family(configuration).also {
            scheduleDescriptor.addFamilyDefinition(it.familyDefinition)
        }
    }

    /**
     * 在调度器上下文中挂起协程
     *
     * @param block 要在世界上下文中执行的代码块
     * @return 代码块的执行结果
     * @param R 返回类型参数
     */
    override suspend fun <R> suspendScheduleCoroutine(
        dispatcherType: ScheduleScope.DispatcherType,
        priority: SchedulePriority,
        block: World.(ScheduleContinuation<R>) -> Unit
    ): R = suspendCoroutine { continuation ->
        val schedulePriority = if (priority == SchedulePriority.Auto) schedulePriority else priority
        val scheduleContinuation = object : ScheduleContinuation<R> by continuation {
            override val scheduleDispatcher: ScheduleDispatcher get() = this@ScheduleScopeImpl.scheduleDispatcher
            override fun resumeWith(result: Result<R>) {
                if (dispatcherType == ScheduleScope.DispatcherType.Main) {
                    scheduleDispatcher.addMainTask(scheduleDescriptor, schedulePriority) { delta ->
                        continuation.resumeWith(result)
                    }
                } else {
                    scheduleDispatcher.addWorkTask(scheduleDescriptor, schedulePriority) {
                        continuation.resumeWith(result)
                    }
                }
            }
        }
        world.block(scheduleContinuation)
    }

    internal fun startCoroutine(
        block: suspend ScheduleScope.() -> Unit,
        continuation: Continuation<Unit>
    ): ScheduleDescriptor {
        scheduleDispatcher.addMainTask(scheduleDescriptor, schedulePriority) { delta ->
            block.startCoroutine(this, continuation)
        }
        return scheduleDescriptor
    }
}