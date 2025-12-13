package cn.jzl.sect.ecs.task

import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityCreateContext
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World
import cn.jzl.ecs.observers.emit
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.FamilyMatcher
import cn.jzl.ecs.query.forEach
import cn.jzl.ecs.query.query
import cn.jzl.sect.ecs.*
import cn.jzl.sect.ecs.core.Description
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.sect.SectConfig
import cn.jzl.sect.ecs.sect.SectResourceService
import kotlin.time.Duration

/**
 * 任务服务
 * 提供宗门任务的创建、领取、完成等功能
 */
class TaskService(world: World) : EntityRelationContext(world) {

    private val sectService by world.di.instance<SectService>()
    private val levelingService by world.di.instance<LevelingService>()
    private val inventoryService by world.di.instance<InventoryService>()
    private val sectResourceService by world.di.instance<SectResourceService>()

    /**
     * 创建宗门任务
     */
    @ECSDsl
    inline fun createTask(
        sect: Entity,
        named: Named,
        type: TaskType,
        requirement: TaskRequirement,
        rewardConfig: TaskRewardConfig,
        limit: TaskLimit = TaskLimit(),
        description: Description? = null,
        block: EntityCreateContext.(Entity) -> Unit
    ): Entity {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return world.entity {
            it.addTag<SectTask>()
            it.addComponent(named)
            description?.let { desc -> it.addComponent(desc) }
            it.addComponent(TaskTypeComponent(type))
            it.addComponent(requirement)
            it.addComponent(rewardConfig)
            it.addComponent(limit)
            it.addRelation<OwnedBy>(sect)
            block(it)
        }
    }

    /**
     * 领取任务
     */
    fun acceptTask(task: Entity, disciple: Entity): TaskAcceptError? {
        require(task.hasTag<SectTask>()) { "实体${task.id}不是任务" }

        // 获取任务所属宗门
        val sect = task.getRelation<OwnedBy>()?.relation?.target
            ?: return TaskAcceptError.TaskNotAvailable

        // 检查弟子是否是宗门成员
        val memberData = sectService.getMemberData(sect, disciple)
            ?: return TaskAcceptError.NotSectMember

        val limit = task.getComponent<TaskLimit?>() ?: TaskLimit()

        // 检查角色是否允许
        if (memberData.role !in limit.allowedRoles) {
            return TaskAcceptError.RoleNotAllowed(memberData.role)
        }

        // 检查等级要求
        val discipleLevel = levelingService.getLevel(disciple)
        if (discipleLevel < limit.minLevel) {
            return TaskAcceptError.LevelTooLow(limit.minLevel, discipleLevel)
        }

        // 检查弟子当前进行中的任务数
        val activeTaskCount = getActiveTasksForDisciple(disciple).count()
        val maxTasks = sect.getComponent<SectConfig?>()?.maxConcurrentTasks ?: 5
        if (activeTaskCount >= maxTasks) {
            return TaskAcceptError.TooManyActiveTasks(activeTaskCount, maxTasks)
        }

        // 检查是否已领取此任务
        val existingProgress = task.getRelation<TaskProgress?>(disciple)
        if (existingProgress != null && existingProgress.status == TaskStatus.IN_PROGRESS) {
            return TaskAcceptError.AlreadyAccepted
        }

        // 检查任务领取人数
        val currentAcceptors = getTaskAcceptors(task).count()
        if (currentAcceptors >= limit.maxAcceptors) {
            return TaskAcceptError.TaskFull
        }

        // 创建任务进度
        world.entity(task) {
            it.addRelation(disciple, TaskProgress(
                disciple = disciple,
                status = TaskStatus.IN_PROGRESS,
                startTime = currentTimeMillis()
            ))
        }

        // 如果有时间限制，添加倒计时
        limit.timeLimit?.let { timeLimit ->
            world.entity(task) {
                it.addRelation(disciple, Countdown(timeLimit))
            }
        }

        world.emit(task, OnTaskAccepted(task, disciple))
        return null
    }

    /**
     * 取消任务
     */
    fun cancelTask(task: Entity, disciple: Entity): Boolean {
        require(task.hasTag<SectTask>()) { "实体${task.id}不是任务" }

        val progress = task.getRelation<TaskProgress?>(disciple)
        if (progress == null || progress.status != TaskStatus.IN_PROGRESS) {
            return false
        }

        world.entity(task) {
            it.addRelation(disciple, progress.copy(status = TaskStatus.CANCELLED))
            it.removeRelation<Countdown>(disciple)
        }

        world.emit(task, OnTaskFailed(task, disciple, TaskFailReason.CANCELLED))
        return true
    }

    /**
     * 提交物品
     */
    fun submitItems(task: Entity, disciple: Entity, items: Map<Entity, Int>): TaskSubmitError? {
        require(task.hasTag<SectTask>()) { "实体${task.id}不是任务" }

        val progress = task.getRelation<TaskProgress?>(disciple)
        if (progress == null || progress.status != TaskStatus.IN_PROGRESS) {
            return TaskSubmitError.TaskNotInProgress
        }

        // 检查弟子是否有足够物品
        val missingItems = mutableMapOf<Entity, Int>()
        items.forEach { (itemPrefab, amount) ->
            val has = inventoryService.getItemCount(disciple, itemPrefab)
            if (has < amount) {
                missingItems[itemPrefab] = amount - has
            }
        }

        if (missingItems.isNotEmpty()) {
            return TaskSubmitError.InsufficientItems(missingItems)
        }

        // 扣除物品
        items.forEach { (itemPrefab, amount) ->
            inventoryService.removeItem(disciple, itemPrefab, amount)
        }

        // 更新进度
        val newSubmitted = progress.submittedItems.toMutableMap()
        items.forEach { (itemPrefab, amount) ->
            newSubmitted[itemPrefab] = (newSubmitted[itemPrefab] ?: 0) + amount
        }

        world.entity(task) {
            it.addRelation(disciple, progress.copy(submittedItems = newSubmitted))
        }

        return null
    }

    /**
     * 完成任务
     */
    fun completeTask(task: Entity, disciple: Entity): Result<TaskReward> {
        require(task.hasTag<SectTask>()) { "实体${task.id}不是任务" }

        val progress = task.getRelation<TaskProgress?>(disciple)
        if (progress == null || progress.status != TaskStatus.IN_PROGRESS) {
            return Result.failure(IllegalStateException("任务未在进行中"))
        }

        val requirement = task.getComponent<TaskRequirement?>() ?: TaskRequirement()

        // 检查是否满足完成条件
        // 检查物品
        requirement.requiredItems.forEach { (itemPrefab, required) ->
            val submitted = progress.submittedItems[itemPrefab] ?: 0
            if (submitted < required) {
                return Result.failure(IllegalStateException("物品不足: 需要 $required, 已提交 $submitted"))
            }
        }

        // 检查击杀
        if (progress.killedCount < requirement.requiredKills) {
            return Result.failure(IllegalStateException("击杀数量不足: 需要 ${requirement.requiredKills}, 已击杀 ${progress.killedCount}"))
        }

        // 检查修炼时长
        requirement.requiredDuration?.let { required ->
            val current = Duration.parse("${progress.cultivationDuration}ms")
            if (current < required) {
                return Result.failure(IllegalStateException("修炼时长不足"))
            }
        }

        // 计算奖励
        val rewardConfig = task.getComponent<TaskRewardConfig?>() ?: TaskRewardConfig(0, 0L)
        val completionTime = Duration.parse("${currentTimeMillis() - progress.startTime}ms")
        val discipleLevel = levelingService.getLevel(disciple)
        val bonusMultiplier = rewardConfig.bonusFormula?.calculate(completionTime, discipleLevel, 1.0f) ?: 1.0f

        val reward = TaskReward(
            contribution = (rewardConfig.baseContribution * bonusMultiplier).toInt(),
            experience = (rewardConfig.baseExperience * bonusMultiplier).toLong(),
            items = rewardConfig.items
        )

        // 发放奖励
        val sect = task.getRelation<OwnedBy>()!!.relation.target
        sectService.addContribution(sect, disciple, reward.contribution)
        levelingService.addExperience(disciple, reward.experience)
        reward.items.forEach { (itemPrefab, amount) ->
            inventoryService.addItem(disciple, itemPrefab, amount)
        }

        // 更新任务状态
        world.entity(task) {
            it.addRelation(disciple, progress.copy(status = TaskStatus.COMPLETED))
            it.removeRelation<Countdown>(disciple)
        }

        world.emit(task, OnTaskCompleted(task, disciple, reward))
        return Result.success(reward)
    }

    /**
     * 获取宗门的所有任务
     */
    fun getTasksBySect(sect: Entity): Sequence<Entity> {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return world.query { TaskQueryContext(this) }.entities.filter { task ->
            task.getRelation<OwnedBy>()?.relation?.target == sect
        }
    }

    /**
     * 获取宗门可领取的任务
     */
    fun getAvailableTasks(sect: Entity): Sequence<Entity> {
        return getTasksBySect(sect).filter { task ->
            val limit = task.getComponent<TaskLimit?>() ?: TaskLimit()
            val currentAcceptors = getTaskAcceptors(task).count()
            currentAcceptors < limit.maxAcceptors
        }
    }

    /**
     * 获取弟子正在进行的任务
     */
    fun getActiveTasksForDisciple(disciple: Entity): Sequence<Entity> {
        return world.query { TaskQueryContext(this) }.entities.filter { task ->
            val progress = task.getRelation<TaskProgress?>(disciple)
            progress?.status == TaskStatus.IN_PROGRESS
        }
    }

    /**
     * 获取任务的所有接受者
     */
    fun getTaskAcceptors(task: Entity): Sequence<Entity> {
        require(task.hasTag<SectTask>()) { "实体${task.id}不是任务" }
        return task.getRelationsWithData<TaskProgress>()
            .filter { it.data.status == TaskStatus.IN_PROGRESS }
            .map { it.data.disciple }
    }

    /**
     * 检查弟子是否可以领取任务
     */
    fun canAcceptTask(task: Entity, disciple: Entity): TaskAcceptError? {
        // 复用 acceptTask 的验证逻辑，但不实际执行
        require(task.hasTag<SectTask>()) { "实体${task.id}不是任务" }

        val sect = task.getRelation<OwnedBy>()?.relation?.target
            ?: return TaskAcceptError.TaskNotAvailable

        val memberData = sectService.getMemberData(sect, disciple)
            ?: return TaskAcceptError.NotSectMember

        val limit = task.getComponent<TaskLimit?>() ?: TaskLimit()

        if (memberData.role !in limit.allowedRoles) {
            return TaskAcceptError.RoleNotAllowed(memberData.role)
        }

        val discipleLevel = levelingService.getLevel(disciple)
        if (discipleLevel < limit.minLevel) {
            return TaskAcceptError.LevelTooLow(limit.minLevel, discipleLevel)
        }

        val activeTaskCount = getActiveTasksForDisciple(disciple).count()
        val maxTasks = sect.getComponent<SectConfig?>()?.maxConcurrentTasks ?: 5
        if (activeTaskCount >= maxTasks) {
            return TaskAcceptError.TooManyActiveTasks(activeTaskCount, maxTasks)
        }

        val existingProgress = task.getRelation<TaskProgress?>(disciple)
        if (existingProgress != null && existingProgress.status == TaskStatus.IN_PROGRESS) {
            return TaskAcceptError.AlreadyAccepted
        }

        val currentAcceptors = getTaskAcceptors(task).count()
        if (currentAcceptors >= limit.maxAcceptors) {
            return TaskAcceptError.TaskFull
        }

        return null
    }

    /**
     * 获取任务进度
     */
    fun getTaskProgress(task: Entity, disciple: Entity): TaskProgress? {
        require(task.hasTag<SectTask>()) { "实体${task.id}不是任务" }
        return task.getRelation<TaskProgress?>(disciple)
    }

    /**
     * 更新击杀计数
     */
    fun addKills(task: Entity, disciple: Entity, count: Int) {
        require(task.hasTag<SectTask>()) { "实体${task.id}不是任务" }
        val progress = task.getRelation<TaskProgress?>(disciple) ?: return
        if (progress.status != TaskStatus.IN_PROGRESS) return

        world.entity(task) {
            it.addRelation(disciple, progress.copy(killedCount = progress.killedCount + count))
        }
    }

    /**
     * 更新修炼时长
     */
    fun addCultivationTime(task: Entity, disciple: Entity, durationMs: Long) {
        require(task.hasTag<SectTask>()) { "实体${task.id}不是任务" }
        val progress = task.getRelation<TaskProgress?>(disciple) ?: return
        if (progress.status != TaskStatus.IN_PROGRESS) return

        world.entity(task) {
            it.addRelation(disciple, progress.copy(cultivationDuration = progress.cultivationDuration + durationMs))
        }
    }

    private fun LevelingService.getLevel(entity: Entity): Long {
        val levelAttribute = attributeService.attribute(ATTRIBUTE_LEVEL)
        return entity.getRelation<AttributeValue?>(levelAttribute)?.value ?: 1L
    }

    private val attributeService by world.di.instance<AttributeService>()

    @PublishedApi
    internal class TaskQueryContext(world: World) : EntityQueryContext(world) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<SectTask>()
        }
    }

    companion object {
        // 简单的时间获取，实际应该使用平台时间服务
        private fun currentTimeMillis(): Long = System.currentTimeMillis()
    }
}

