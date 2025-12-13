# API 合同: 宗门任务服务 (TaskService)

**功能分支**: `001-sect-system-enhancement`
**创建时间**: 2025-12-13
**模块**: `lko-sect/ecs/task/`

## 概述

TaskService 提供宗门任务的创建、领取、提交和奖励发放功能。

---

## 服务接口

```kotlin
package cn.jzl.sect.ecs.task

class TaskService(world: World) : EntityRelationContext(world) {

    // ==================== 任务创建 ====================
    
    /**
     * 创建宗门任务
     * @param sect 宗门实体
     * @param named 任务名称
     * @param type 任务类型
     * @param requirement 完成条件
     * @param rewardConfig 奖励配置
     * @param limit 任务限制
     * @param block 额外配置
     * @return 任务实体
     * @throws IllegalArgumentException 如果宗门不存在
     */
    @ECSDsl
    fun createTask(
        sect: Entity,
        named: Named,
        type: TaskType,
        requirement: TaskRequirement,
        rewardConfig: TaskRewardConfig,
        limit: TaskLimit = TaskLimit(),
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity

    // ==================== 任务领取 ====================
    
    /**
     * 弟子领取任务
     * @param task 任务实体
     * @param disciple 弟子实体
     * @throws IllegalArgumentException 如果任务不存在
     * @throws IllegalStateException 如果任务已满员
     * @throws IllegalStateException 如果弟子任务数已达上限
     * @throws IllegalStateException 如果弟子等级不足
     * @throws IllegalStateException 如果弟子角色不允许
     */
    fun acceptTask(task: Entity, disciple: Entity)
    
    /**
     * 弟子放弃任务
     * @param task 任务实体
     * @param disciple 弟子实体
     * @throws IllegalArgumentException 如果弟子未领取该任务
     */
    fun cancelTask(task: Entity, disciple: Entity)

    // ==================== 任务提交 ====================
    
    /**
     * 提交任务物品
     * @param task 任务实体
     * @param disciple 弟子实体
     * @param items 提交的物品 (物品预制体 -> 数量)
     * @throws IllegalArgumentException 如果弟子未领取该任务
     * @throws IllegalStateException 如果物品不足
     */
    fun submitItems(task: Entity, disciple: Entity, items: Map<Entity, Int>)
    
    /**
     * 完成任务并领取奖励
     * @param task 任务实体
     * @param disciple 弟子实体
     * @return 实际发放的奖励
     * @throws IllegalArgumentException 如果弟子未领取该任务
     * @throws IllegalStateException 如果任务条件未满足
     */
    fun completeTask(task: Entity, disciple: Entity): TaskReward

    // ==================== 查询 ====================
    
    /**
     * 获取宗门所有任务
     * @param sect 宗门实体
     * @return 任务实体序列
     */
    fun getTasksBySect(sect: Entity): Sequence<Entity>
    
    /**
     * 获取可领取的任务
     * @param sect 宗门实体
     * @param disciple 弟子实体 (用于过滤等级和角色要求)
     * @return 可领取的任务实体序列
     */
    fun getAvailableTasks(sect: Entity, disciple: Entity): Sequence<Entity>
    
    /**
     * 获取弟子正在进行的任务
     * @param disciple 弟子实体
     * @return 任务实体和进度的序列
     */
    fun getActiveTasksForDisciple(disciple: Entity): Sequence<Pair<Entity, TaskProgress>>
    
    /**
     * 获取任务的所有领取者
     * @param task 任务实体
     * @return 弟子实体和进度的序列
     */
    fun getTaskAcceptors(task: Entity): Sequence<Pair<Entity, TaskProgress>>
    
    /**
     * 检查任务是否可领取
     * @param task 任务实体
     * @param disciple 弟子实体
     * @return 可领取返回 null，否则返回不可领取原因
     */
    fun canAcceptTask(task: Entity, disciple: Entity): TaskAcceptError?
    
    /**
     * 检查任务是否可完成
     * @param task 任务实体
     * @param disciple 弟子实体
     * @return 可完成返回 true
     */
    fun canCompleteTask(task: Entity, disciple: Entity): Boolean

    // ==================== 配置常量 ====================
    
    companion object {
        /** 弟子最大并发任务数 */
        const val MAX_CONCURRENT_TASKS = 5
    }
}
```

---

## 错误类型

```kotlin
sealed class TaskAcceptError {
    object TaskNotFound : TaskAcceptError()
    object TaskFull : TaskAcceptError()
    object MaxTasksReached : TaskAcceptError()
    data class LevelTooLow(val required: Long, val actual: Long) : TaskAcceptError()
    data class RoleNotAllowed(val role: MemberRole) : TaskAcceptError()
    object AlreadyAccepted : TaskAcceptError()
}
```

---

## 事件发射

| 事件 | 触发时机 |
|------|----------|
| `OnTaskAccepted` | `acceptTask` 成功后 |
| `OnTaskCompleted` | `completeTask` 成功后 |
| `OnTaskFailed` | 任务超时或取消后 |

---

## 使用示例

```kotlin
// 创建采集任务
val task = taskService.createTask(
    sect = sectEntity,
    named = Named("采集灵草"),
    type = TaskType.GATHERING,
    requirement = TaskRequirement(
        requiredItems = mapOf(lingcaoPrefab to 10)
    ),
    rewardConfig = TaskRewardConfig(
        baseContribution = 50,
        baseExperience = 100,
        items = mapOf(lingshiPrefab to 20)
    ),
    limit = TaskLimit(
        maxAcceptors = 3,
        timeLimit = 1.hours,
        minLevel = 5
    )
) {}

// 弟子领取任务
taskService.acceptTask(task, discipleEntity)

// 提交任务物品
taskService.submitItems(task, discipleEntity, mapOf(lingcaoPrefab to 10))

// 完成任务
val reward = taskService.completeTask(task, discipleEntity)
```

