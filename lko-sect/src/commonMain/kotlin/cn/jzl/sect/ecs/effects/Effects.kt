package cn.jzl.sect.ecs.effects

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.observers.emit
import cn.jzl.ecs.query.*
import cn.jzl.sect.ecs.AttributeKey
import cn.jzl.sect.ecs.attribute.AttributeService
import cn.jzl.sect.ecs.attribute.attributeAddon
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.planning.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * 效果系统包，包含效果组件、服务和addon配置
 * 
 * 主要功能：
 * 1. 定义各种类型的效果及其行为
 * 2. 提供效果应用、更新和移除机制
 * 3. 支持周期性效果和状态效果
 * 4. 实现属性修饰和堆叠机制
 * 5. 提供效果相关的状态解析器
 */

/**
 * 效果标记组件
 * 用于标识实体为效果
 */
sealed class Effect

/**
 * 效果应用时间组件
 * 记录效果被应用的游戏时间
 * 
 * @param gameTime 效果被应用的游戏时间
 */
@JvmInline
value class AppliedTime(val gameTime: Duration)

/**
 * 效果上次触发时间组件
 * 记录周期性效果上次触发的时间和次数
 * 
 * @param gameTime 上次触发的游戏时间
 * @param tickCount 触发次数
 */
data class LastTickTime(
    val gameTime: Duration,
    val tickCount: Int
)

/**
 * 效果类型枚举
 * 定义不同类型的效果
 */
enum class EffectType {
    /** 增益效果 */
    BUFF,
    /** 减益效果 */
    DEBUFF,
    /** 治疗效果 */
    HEAL,
    /** 伤害效果 */
    DAMAGE,
    /** 属性修改效果 */
    STAT_MOD,
    /** 状态效果 */
    STATUS,
    /** 可驱散效果 */
    DISPELLABLE,
    /** 永久效果 */
    PERMANENT
}

/**
 * 触发间隔组件
 * 定义周期性效果的触发间隔
 * 
 * @param duration 触发间隔时长
 */
@JvmInline
value class TickInterval(val duration: Duration) {
    /**
     * 伴生对象，提供便捷的创建方法
     */
    companion object {
        /**
         * 创建指定秒数的触发间隔
         * 
         * @param s 秒数
         * @return TickInterval实例
         */
        fun seconds(s: Int) = TickInterval(s.seconds)
        /**
         * 创建指定秒数（小数）的触发间隔
         * 
         * @param s 秒数
         * @return TickInterval实例
         */
        fun seconds(s: Double) = TickInterval(s.seconds)
        /**
         * 创建指定分钟数的触发间隔
         * 
         * @param m 分钟数
         * @return TickInterval实例
         */
        fun minutes(m: Int) = TickInterval(m.minutes)
    }
    /**
     * 转换为整秒数
     */
    val inWholeSeconds: Long get() = duration.inWholeSeconds
}

/**
 * 堆叠行为枚举
 * 定义效果堆叠时的行为
 */
enum class StackBehavior {
    /** 不堆叠，新效果替换旧效果 */
    NONE,
    /** 刷新持续时间 */
    REFRESH_DURATION,
    /** 增加堆叠次数 */
    INCREMENT_STACK
}

/**
 * 效果堆叠配置
 * 定义效果的堆叠规则
 * 
 * @param maxStacks 最大堆叠次数，默认1
 * @param currentStacks 当前堆叠次数，默认1
 * @param stackBehavior 堆叠行为，默认NONE
 */
data class EffectStack(
    val maxStacks: Int = 1,
    val currentStacks: Int = 1,
    val stackBehavior: StackBehavior = StackBehavior.NONE
) {
    init {
        require(maxStacks >= 1) { "maxStacks must be >= 1" }
        require(currentStacks in 1..maxStacks) { "currentStacks must be between 1 and maxStacks" }
    }
}

/**
 * 属性修饰类型枚举
 * 定义属性修饰的方式
 */
enum class ModifierType {
    /** 加法修饰 */
    ADD,
    /** 乘法修饰 */
    MULTIPLY,
    /** 赋值修饰 */
    SET
}

/**
 * 属性修饰器
 * 定义对特定属性的修饰
 * 
 * @param attribute 目标属性实体
 * @param modifierType 修饰类型
 * @param value 修饰值
 * @param multiplier 乘数，默认1.0f
 */
data class AttributeModifier(
    val attribute: Entity,
    val modifierType: ModifierType,
    val value: Long,
    val multiplier: Float = 1.0f
) {
    init {
        require(multiplier > 0) { "Multiplier must be greater than 0, got $multiplier" }
    }
}

/**
 * 加法属性修饰组件
 * 
 * @param value 加法修饰值
 */
@JvmInline
value class AddAttributeModifier(val  value: Long)

/**
 * 乘法属性修饰组件
 * 
 * @param value 乘法修饰值
 */
@JvmInline
value class MultiplyAttributeModifier(val value: Double)

/**
 * 赋值属性修饰组件
 * 
 * @param value 赋值修饰值
 */
@JvmInline
value class AssignmentAttributeModifier(val value: Long)

/**
 * 效果移除原因枚举
 * 定义效果被移除的原因
 */
enum class RemovalReason {
    /** 效果过期 */
    EXPIRED,
    /** 效果被驱散 */
    DISPELLED,
    /** 手动移除 */
    MANUAL,
    /** 效果出错 */
    ERROR,
    /** 目标死亡 */
    TARGET_DIED
}

/**
 * 效果应用事件
 * 当效果被应用时触发
 * 
 * @param effect 效果实体
 * @param target 目标实体
 * @param source 来源实体，可为空
 * @param gameTime 应用时间
 */
data class OnEffectApplied(
    val effect: Entity,
    val target: Entity,
    val source: Entity? = null,
    val gameTime: Duration
)

/**
 * 效果移除事件
 * 当效果被移除时触发
 * 
 * @param effect 效果实体
 * @param target 目标实体
 * @param reason 移除原因，默认EXPIRED
 * @param gameTime 移除时间
 */
data class OnEffectRemoved(
    val effect: Entity,
    val target: Entity,
    val reason: RemovalReason = RemovalReason.EXPIRED,
    val gameTime: Duration
)

/**
 * 效果触发事件
 * 当周期性效果触发时触发
 * 
 * @param effect 效果实体
 * @param target 目标实体
 * @param tickCount 触发次数
 * @param gameTime 触发时间
 */
data class OnEffectTick(
    val effect: Entity,
    val target: Entity,
    val tickCount: Int,
    val gameTime: Duration
)

/**
 * 状态效果类型枚举
 * 定义不同类型的状态效果
 */
enum class StatusEffectType {
    /** 眩晕效果 */
    STUN,
    /** 沉默效果 */
    SILENCE,
    /** 定身效果 */
    ROOT,
    /** 中毒效果 */
    POISON,
    /** 流血效果 */
    BLEED
}

/**
 * 状态效果数据类
 * 定义状态效果的属性
 * 
 * @param type 状态效果类型
 * @param duration 效果持续时间，可为空
 * @param damagePerTick 每秒伤害，默认0
 * @param tickInterval 触发间隔，可为空
 */
data class StatusEffect(
    val type: StatusEffectType,
    val duration: Duration? = null,
    val damagePerTick: Long = 0,
    val tickInterval: TickInterval? = null
) {
    /**
     * 伴生对象，提供便捷的创建方法
     */
    companion object {
        /**
         * 创建眩晕效果
         * 
         * @param duration 持续时间
         * @return StatusEffect实例
         */
        fun stun(duration: Duration) = StatusEffect(
            type = StatusEffectType.STUN,
            duration = duration
        )
        /**
         * 创建沉默效果
         * 
         * @param duration 持续时间
         * @return StatusEffect实例
         */
        fun silence(duration: Duration) = StatusEffect(
            type = StatusEffectType.SILENCE,
            duration = duration
        )
        /**
         * 创建定身效果
         * 
         * @param duration 持续时间
         * @return StatusEffect实例
         */
        fun root(duration: Duration) = StatusEffect(
            type = StatusEffectType.ROOT,
            duration = duration
        )
        /**
         * 创建中毒效果
         * 
         * @param damagePerTick 每秒伤害
         * @param tickInterval 触发间隔
         * @param duration 持续时间
         * @return StatusEffect实例
         */
        fun poison(damagePerTick: Long, tickInterval: TickInterval, duration: Duration) = StatusEffect(
            type = StatusEffectType.POISON,
            duration = duration,
            damagePerTick = damagePerTick,
            tickInterval = tickInterval
        )
        /**
         * 创建流血效果
         * 
         * @param damagePerTick 每秒伤害
         * @param tickInterval 触发间隔
         * @param duration 持续时间
         * @return StatusEffect实例
         */
        fun bleed(damagePerTick: Long, tickInterval: TickInterval, duration: Duration) = StatusEffect(
            type = StatusEffectType.BLEED,
            duration = duration,
            damagePerTick = damagePerTick,
            tickInterval = tickInterval
        )
    }
}

/**
 * 眩晕状态标记
 * 表示实体处于眩晕状态
 */
sealed class Stunned

/**
 * 沉默状态标记
 * 表示实体处于沉默状态
 */
sealed class Silenced

/**
 * 定身状态标记
 * 表示实体处于定身状态
 */
sealed class Rooted

/**
 * 中毒状态标记
 * 表示实体处于中毒状态
 */
sealed class Poisoned

/**
 * 流血状态标记
 * 表示实体处于流血状态
 */
sealed class Bleeding

/**
 * 效果应用异常
 * 当效果应用失败时抛出
 * 
 * @param message 异常信息
 */
sealed class EffectApplicationException(message: String) : Exception(message) {
    /**
     * 等级要求未满足
     * 
     * @param required 所需等级
     * @param actual 实际等级
     */
    data class LevelRequirementNotMet(
        val required: Int,
        val actual: Int
    ) : EffectApplicationException(
        "Level requirement not met: required $required, actual $actual"
    )
    /**
     * 缺少必需效果
     * 
     * @param required 必需效果实体
     */
    data class MissingRequiredEffect(
        val required: Entity
    ) : EffectApplicationException(
        "Missing required effect: $required"
    )
    /**
     * 存在不兼容效果
     * 
     * @param incompatible 不兼容效果实体
     */
    data class IncompatibleEffectPresent(
        val incompatible: Entity
    ) : EffectApplicationException(
        "Incompatible effect present: $incompatible"
    )
    /**
     * 无效目标
     * 
     * @param target 无效目标实体
     */
    data class InvalidTarget(
        val target: Entity
    ) : EffectApplicationException(
        "Invalid target: $target"
    )
    /**
     * 已达到最大堆叠次数
     * 
     * @param effectPrefab 效果预制体
     * @param maxStacks 最大堆叠次数
     */
    data class MaxStacksReached(
        val effectPrefab: Entity,
        val maxStacks: Int
    ) : EffectApplicationException(
        "Max stacks reached: $effectPrefab (max: $maxStacks)"
    )
}

/**
 * 效果应用者关系标记
 * 表示效果由谁应用
 */
sealed class AppliedBy

/**
 * 效果目标关系标记
 * 表示效果应用到谁身上
 */
sealed class AppliedTo

/**
 * 装备来源关系标记
 * 表示效果来自装备
 */
sealed class FromEquipment

class ActiveEffectContext(world: World) : EntityQueryContext(world) {
    val target by relationUp<AppliedTo>()
    val effectPrefab by prefab()
    var duration by component<Duration?>()
    val effectType by component<EffectType?>()
    val appliedTime by component<AppliedTime?>()
    val source: Entity? get() = getRelationUp<AppliedBy>()
    val effectStack by component<EffectStack?>()
    val attributeModifiers: Sequence<RelationWithData<AttributeModifier>> = getRelations<AttributeModifier>()
    val attributeModifier by component<AttributeModifier?>()
    override fun FamilyMatcher.FamilyBuilder.configure() {
        relation(relations.component<Effect>())
    }
}

class PeriodicEffectContext(world: World) : EntityQueryContext(world) {
    val target by relationUp<AppliedTo>()
    val tickInterval by component<TickInterval>()
    val lastTickTime by component<LastTickTime?>()
    val appliedTime by component<AppliedTime?>()
    override fun FamilyMatcher.FamilyBuilder.configure() {
        relation(relations.component<Effect>())
    }
}

@JvmInline
value class EffectCountKey(val effectPrefab: Entity) : StateKey<Int>

@JvmInline
value class HasEffectKey(val effectPrefab: Entity) : StateKey<Boolean>

@JvmInline
value class IsStatusAffectedKey(val statusType: StatusEffectType) : StateKey<Boolean>

data class EffectAttributeKey(val effectPrefab: Entity, val attribute: Entity) : StateKey<Long>

class EffectAttributeResolver(world: World) : StateResolver<AttributeKey, Long> {
    private val attributeService by world.di.instance<AttributeService>()
    private val effectService by world.di.instance<EffectService>()
    override fun EntityRelationContext.getWorldState(agent: Entity, key: cn.jzl.sect.ecs.AttributeKey): Long {
        val baseValue = attributeService.getAttributeValue(agent, key.attribute)?.value ?: 0L
        var addModifierTotalValue = 0L
        var multiplyModifierTotalValue = 1f
        var lastSetValue: Long? = null
        var lastTime: Duration? = null
        effectService.getActiveEffects(agent).forEach {
            attributeModifier?.let {
                when (it.modifierType) {
                    ModifierType.ADD -> addModifierTotalValue += it.value
                    ModifierType.MULTIPLY -> multiplyModifierTotalValue *= it.multiplier
                    ModifierType.SET -> {
                        val appliedTime = appliedTime?.gameTime ?: Duration.ZERO
                        val time = lastTime
                        if (time == null || time < appliedTime) {
                            lastTime = appliedTime
                            lastSetValue = it.value
                        }
                    }
                }
            }
        }
        return lastSetValue ?: run { ((baseValue + addModifierTotalValue) * multiplyModifierTotalValue).toLong() }
    }
}

class IsStatusAffectedResolver(world: World) : StateResolver<IsStatusAffectedKey, Boolean>, EntityRelationContext(world) {
    override fun EntityRelationContext.getWorldState(agent: Entity, key: IsStatusAffectedKey): Boolean {
        return when (key.statusType) {
            StatusEffectType.STUN -> agent.hasTag<Stunned>()
            StatusEffectType.SILENCE -> agent.hasTag<Silenced>()
            StatusEffectType.ROOT -> agent.hasTag<Rooted>()
            StatusEffectType.POISON -> agent.hasTag<Poisoned>()
            StatusEffectType.BLEED -> agent.hasTag<Bleeding>()
        }
    }
}

class EffectCountResolver(world: World) : StateResolver<EffectCountKey, Int> {
    private val effectService by world.di.instance<EffectService>()
    override fun EntityRelationContext.getWorldState(agent: Entity, key: EffectCountKey): Int {
        return effectService.getEffectCount(agent, key.effectPrefab)
    }
}

class HasEffectResolver(world: World) : StateResolver<HasEffectKey, Boolean> {
    private val effectService by world.di.instance<EffectService>()
    override fun EntityRelationContext.getWorldState(agent: Entity, key: HasEffectKey): Boolean {
        return effectService.hasEffect(agent, key.effectPrefab)
    }
}

class EffectAttributeKeyResolver(world: World) : StateResolver<EffectAttributeKey, Long>, EntityRelationContext(world) {
    private val effectService by world.di.instance<EffectService>()
    override fun EntityRelationContext.getWorldState(agent: Entity, key: EffectAttributeKey): Long {
        val effectInstance = effectService.getActiveEffects(agent).filter { this.effectPrefab == key.effectPrefab }.map { entity }.firstOrNull()
        if (effectInstance == null) return 0L
        val modifier = effectInstance.getComponent<AttributeModifier?>()
        if (modifier == null || modifier.attribute != key.attribute) return 0L
        return when (modifier.modifierType) {
            ModifierType.ADD -> modifier.value
            ModifierType.SET -> modifier.value
            ModifierType.MULTIPLY -> 0L
        }
    }
}

class EffectEnhancedAttributeStateResolverRegistry(world: World) : StateResolverRegistry {
    private val effectAttributeResolver = EffectAttributeResolver(world)
    private val itemAmountResolver = _root_ide_package_.cn.jzl.sect.ecs.ItemAmountStateResolver(world)
    private val isStatusAffectedResolver = IsStatusAffectedResolver(world)
    private val effectCountResolver = EffectCountResolver(world)
    private val hasEffectResolver = HasEffectResolver(world)
    private val effectAttributeKeyResolver = EffectAttributeKeyResolver(world)
    override fun <K : StateKey<T>, T> getStateHandler(key: K): StateResolver<K, T>? {
        @Suppress("UNCHECKED_CAST")
        return when (key) {
            is cn.jzl.sect.ecs.AttributeKey -> effectAttributeResolver as StateResolver<K, T>
            is cn.jzl.sect.ecs.ItemAmountKey -> itemAmountResolver as StateResolver<K, T>
            is IsStatusAffectedKey -> isStatusAffectedResolver as StateResolver<K, T>
            is EffectCountKey -> effectCountResolver as StateResolver<K, T>
            is HasEffectKey -> hasEffectResolver as StateResolver<K, T>
            is EffectAttributeKey -> effectAttributeKeyResolver as StateResolver<K, T>
            else -> null
        }
    }
}

object EffectActionEffects {
    fun addEffect(effectPrefab: Entity, duration: Duration? = null): ActionEffect {
        return ActionEffect { stateWriter, agent ->
            stateWriter.setValue(HasEffectKey(effectPrefab), true)
            val currentCount = stateWriter.getValue(agent, EffectCountKey(effectPrefab))
            stateWriter.setValue(EffectCountKey(effectPrefab), currentCount + 1)
        }
    }
    fun removeEffect(effectPrefab: Entity): ActionEffect {
        return ActionEffect { stateWriter, agent ->
            val currentCount = stateWriter.getValue(agent, EffectCountKey(effectPrefab))
            val newCount = (currentCount - 1).coerceAtLeast(0)
            stateWriter.setValue(EffectCountKey(effectPrefab), newCount)
            if (newCount == 0) {
                stateWriter.setValue(HasEffectKey(effectPrefab), false)
            }
        }
    }
    fun modifyAttribute(attributeKey: cn.jzl.sect.ecs.AttributeKey, value: Long): ActionEffect {
        return ActionEffect { stateWriter, agent ->
            val currentValue = stateWriter.getValue(agent, attributeKey)
            stateWriter.setValue(attributeKey, currentValue + value)
        }
    }
    fun setStatus(statusType: StatusEffectType, value: Boolean): ActionEffect {
        return ActionEffect { stateWriter, agent ->
            stateWriter.setValue(IsStatusAffectedKey(statusType), value)
        }
    }
    fun incrementStack(effectPrefab: Entity, stacks: Int = 1): ActionEffect {
        return ActionEffect { stateWriter, agent ->
            val currentCount = stateWriter.getValue(agent, EffectCountKey(effectPrefab))
            stateWriter.setValue(EffectCountKey(effectPrefab), currentCount + stacks)
        }
    }
    fun dispelEffects(effectPrefab: Entity? = null, maxCount: Int = Int.MAX_VALUE): ActionEffect {
        return ActionEffect { stateWriter, agent ->
            if (effectPrefab != null) {
                val currentCount = stateWriter.getValue(agent, EffectCountKey(effectPrefab))
                val dispelCount = minOf(currentCount, maxCount)
                val newCount = currentCount - dispelCount
                stateWriter.setValue(EffectCountKey(effectPrefab), newCount)
                if (newCount == 0) {
                    stateWriter.setValue(HasEffectKey(effectPrefab), false)
                }
            }
        }
    }
}

object EffectConditions {
    fun hasEffect(effectPrefab: Entity): Precondition {
        return Precondition { stateProvider, agent ->
            stateProvider.getValue(agent, HasEffectKey(effectPrefab))
        }
    }
    fun hasEffectCount(effectPrefab: Entity, minCount: Int): Precondition {
        return Precondition { stateProvider, agent ->
            stateProvider.getValue(agent, EffectCountKey(effectPrefab)) >= minCount
        }
    }
    fun canApplyEffect(effectPrefab: Entity): Precondition {
        return Precondition { stateProvider, agent ->
            true
        }
    }
    fun isAffectedBy(statusType: StatusEffectType): Precondition {
        return Precondition { stateProvider, agent ->
            stateProvider.getValue(agent, IsStatusAffectedKey(statusType))
        }
    }
    fun notHasEffect(effectPrefab: Entity): Precondition {
        return Precondition { stateProvider, agent ->
            !stateProvider.getValue(agent, HasEffectKey(effectPrefab))
        }
    }
    fun notAffectedBy(statusType: StatusEffectType): Precondition {
        return Precondition { stateProvider, agent ->
            !stateProvider.getValue(agent, IsStatusAffectedKey(statusType))
        }
    }
}

class EffectService(world: World) : EntityRelationContext(world) {
    @ECSDsl
    fun effectPrefab(
        name: String,
        type: EffectType,
        duration: Duration? = null,
        configure: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity = world.prefab {
        it.addTag<Effect>()
        it.addComponent(Named(name))
        it.addComponent(type)
        if (duration != null) {
            it.addComponent(duration)
        }
        configure(it)
    }
    @ECSDsl
    fun applyEffect(
        target: Entity,
        effectPrefab: Entity,
        source: Entity? = null,
        durationOverride: Duration? = null,
        gameTime: Duration = Duration.ZERO
    ): Entity {
        val existingEffect = checkStackable(target, effectPrefab)
        if (existingEffect != null) {
            val stackConfig = effectPrefab.getComponent<EffectStack?>()
            if (stackConfig != null) {
                return handleStacking(existingEffect, effectPrefab, stackConfig, durationOverride)
            }
        }
        val prefabDuration = durationOverride ?: effectPrefab.getComponent<Duration?>()
        val prefabStatusEffect = effectPrefab.getComponent<StatusEffect?>()
        val effectInstance = world.instanceOf(effectPrefab) {
            it.addComponent(AppliedTime(gameTime))
            if (prefabDuration != null) {
                it.addComponent(prefabDuration)
            }
            if (prefabStatusEffect != null) {
                it.addComponent(prefabStatusEffect)
            }
            it.addRelation<AppliedTo>(target)
            if (source != null) {
                it.addRelation<AppliedBy>(source)
            }
        }
        applyStatusEffect(effectInstance, target)
        world.emit(
            effectInstance, OnEffectApplied(
                effect = effectInstance,
                target = target,
                source = source,
                gameTime = gameTime
            )
        )
        return effectInstance
    }
    fun removeEffect(
        effect: Entity,
        reason: RemovalReason = RemovalReason.MANUAL,
        gameTime: Duration = Duration.ZERO
    ) {
        if (!world.isActive(effect)) return
        val target = effect.getRelationUp<AppliedTo>()
        if (target != null) {
            removeStatusEffect(effect, target)
        }
        if (target != null) {
            world.entity(effect) {
                it.removeRelation<AppliedTo>(target)
            }
            world.emit(
                effect, OnEffectRemoved(
                    effect = effect,
                    target = target,
                    reason = reason,
                    gameTime = gameTime
                )
            )
        }
        world.destroy(effect)
    }
    fun getActiveEffects(target: Entity): QueryStream<ActiveEffectContext> {
        return world.query { ActiveEffectContext(this) }.filter { this.target == target }
    }
    fun hasEffect(target: Entity, effectPrefab: Entity): Boolean {
        return getActiveEffects(target).any { this.effectPrefab == effectPrefab }
    }
    fun getEffectCount(
        target: Entity,
        effectPrefab: Entity? = null
    ): Int {
        val activeEffects = getActiveEffects(target)
        return if (effectPrefab == null) {
            activeEffects.count()
        } else {
            activeEffects.count { this.effectPrefab == effectPrefab }
        }
    }
    fun getEffectsByType(
        target: Entity,
        type: EffectType
    ): QueryStream<ActiveEffectContext> {
        return getActiveEffects(target).filter { this.effectType == type }
    }
    fun getEffectsBySource(
        target: Entity,
        source: Entity
    ): QueryStream<ActiveEffectContext> {
        return getActiveEffects(target).filter { this.source == source }
    }
    fun dispelEffects(
        target: Entity,
        dispelType: EffectType? = null,
        maxCount: Int = Int.MAX_VALUE
    ): Int {
        var count = 0
        if (dispelType != null) {
            getEffectsByType(target, dispelType)
        } else {
            getActiveEffects(target)
        }.take(maxCount).forEach {
            removeEffect(entity, RemovalReason.DISPELLED)
            count++
        }
        return count
    }
    fun updateEffects(deltaTime: Float, gameTime: Duration = Duration.ZERO) {
        val effectsToRemove = mutableListOf<Entity>()
        val errorEffects = mutableListOf<Pair<Entity, Exception>>()
        world.query { ActiveEffectContext(this) }.forEach {
            try {
                val currentDuration = duration
                if (currentDuration != null) {
                    val deltaDuration = deltaTime.toDouble().seconds
                    val newDuration = currentDuration - deltaDuration
                    if (newDuration < Duration.ZERO || newDuration == Duration.ZERO) {
                        effectsToRemove.add(entity)
                    } else {
                        duration = newDuration
                    }
                }
            } catch (e: Exception) {
                errorEffects.add(entity to e)
            }
        }
        effectsToRemove.forEach { effect ->
            try {
                removeEffect(effect, RemovalReason.EXPIRED, gameTime)
            } catch (e: Exception) {
            }
        }
        errorEffects.forEach { (effect, error) ->
            try {
                removeEffect(effect, RemovalReason.ERROR, gameTime)
            } catch (e: Exception) {
            }
        }
    }
    fun tickPeriodicEffects(gameTime: Duration) {
        data class TickUpdate(val effect: Entity, val target: Entity, val tickCount: Int)
        val tickUpdates = mutableListOf<TickUpdate>()
        val errorEffects = mutableListOf<Pair<Entity, Exception>>()
        world.query { PeriodicEffectContext(this) }.forEach {
            try {
                val tickInterval = this.tickInterval
                val lastTick = this.lastTickTime
                val timeSinceLastTick = if (lastTick == null) {
                    val appliedTime = this.appliedTime
                    if (appliedTime != null) {
                        gameTime - appliedTime.gameTime
                    } else {
                        Duration.ZERO
                    }
                } else {
                    gameTime - lastTick.gameTime
                }
                val interval = tickInterval.duration
                if (timeSinceLastTick >= interval) {
                    val triggerCount = (timeSinceLastTick / interval).toInt()
                    val newTickCount = (lastTick?.tickCount ?: 0) + triggerCount
                    tickUpdates.add(TickUpdate(entity, target, newTickCount))
                }
            } catch (e: Exception) {
                errorEffects.add(entity to e)
            }
        }
        tickUpdates.forEach { update ->
            try {
                world.entity(update.effect) { entity ->
                    entity.addComponent(LastTickTime(gameTime, update.tickCount))
                }
                world.emit(
                    update.effect, OnEffectTick(
                        effect = update.effect,
                        target = update.target,
                        tickCount = update.tickCount,
                        gameTime = gameTime
                    )
                )
            } catch (e: Exception) {
                errorEffects.add(update.effect to e)
            }
        }
        errorEffects.forEach { (effect, error) ->
            try {
                removeEffect(effect, RemovalReason.ERROR, gameTime)
            } catch (e: Exception) {
            }
        }
    }
    private fun checkStackable(target: Entity, effectPrefab: Entity): Entity? {
        return getActiveEffects(target).filter { effectPrefab == this.effectPrefab }.map { entity }.firstOrNull()
    }
    private fun handleStacking(
        existingEffect: Entity,
        effectPrefab: Entity,
        stackConfig: EffectStack,
        durationOverride: Duration?
    ): Entity {
        when (stackConfig.stackBehavior) {
            StackBehavior.NONE -> {
                throw EffectApplicationException.MaxStacksReached(effectPrefab, stackConfig.maxStacks)
            }
            StackBehavior.REFRESH_DURATION -> {
                val newDuration = durationOverride ?: effectPrefab.getComponent<Duration?>()
                if (newDuration != null) {
                    world.entity(existingEffect) {
                        it.addComponent(newDuration)
                    }
                }
                return existingEffect
            }
            StackBehavior.INCREMENT_STACK -> {
                val currentStack = existingEffect.getComponent<EffectStack?>()
                val currentStacks = currentStack?.currentStacks ?: 1
                if (currentStacks >= stackConfig.maxStacks) {
                    throw EffectApplicationException.MaxStacksReached(effectPrefab, stackConfig.maxStacks)
                }
                val newStacks = currentStacks + 1
                val newDuration = durationOverride ?: effectPrefab.getComponent<Duration?>()
                world.entity(existingEffect) {
                    it.addComponent(stackConfig.copy(currentStacks = newStacks))
                    if (newDuration != null) {
                        it.addComponent(newDuration)
                    }
                }
                return existingEffect
            }
        }
    }
    private fun applyStatusEffect(effectInstance: Entity, target: Entity) {
        val statusEffect = effectInstance.getComponent<StatusEffect?>() ?: return
        world.entity(target) {
            when (statusEffect.type) {
                StatusEffectType.STUN -> it.addTag<Stunned>()
                StatusEffectType.SILENCE -> it.addTag<Silenced>()
                StatusEffectType.ROOT -> it.addTag<Rooted>()
                StatusEffectType.POISON -> it.addTag<Poisoned>()
                StatusEffectType.BLEED -> it.addTag<Bleeding>()
            }
        }
    }
    private fun removeStatusEffect(effectInstance: Entity, target: Entity) {
        val statusEffect = effectInstance.getComponent<StatusEffect?>() ?: return
        world.entity(target) {
            when (statusEffect.type) {
                StatusEffectType.STUN -> it.removeTag<Stunned>()
                StatusEffectType.SILENCE -> it.removeTag<Silenced>()
                StatusEffectType.ROOT -> it.removeTag<Rooted>()
                StatusEffectType.POISON -> it.removeTag<Poisoned>()
                StatusEffectType.BLEED -> it.removeTag<Bleeding>()
            }
        }
    }
}

val effectAddon = createAddon("effect") {
    install(attributeAddon)
    install(planningAddon)
    injects { this bind singleton { new(::EffectService) } }
    components {
        world.componentId<Effect> { it.tag() }
        world.componentId<EffectType>()
        world.componentId<TickInterval>()
        world.componentId<AppliedTime>()
        world.componentId<LastTickTime>()
        world.componentId<RemovalReason>()
        world.componentId<AttributeModifier>()
        world.componentId<ModifierType>()
        world.componentId<EffectStack>()
        world.componentId<StackBehavior>()
        world.componentId<StatusEffect>()
        world.componentId<StatusEffectType>()
        world.componentId<AddAttributeModifier>()
        world.componentId<MultiplyAttributeModifier>()
        world.componentId<AssignmentAttributeModifier>()
        world.componentId<Stunned> { it.tag() }
        world.componentId<Silenced> { it.tag() }
        world.componentId<Rooted> { it.tag() }
        world.componentId<Poisoned> { it.tag() }
        world.componentId<Bleeding> { it.tag() }
        world.componentId<AppliedTo> {
            it.singleRelation()
            it.tag()
        }
        world.componentId<AppliedBy> {
            it.singleRelation()
            it.tag()
        }
        world.componentId<FromEquipment> {
            it.singleRelation()
            it.tag()
        }
    }
    planning { register(EffectEnhancedAttributeStateResolverRegistry(world)) }
}