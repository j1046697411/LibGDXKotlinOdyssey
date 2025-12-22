package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.observers.emit
import cn.jzl.ecs.query.*
import cn.jzl.sect.ecs.core.Named
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

sealed class Effect

@JvmInline
value class AppliedTime(val gameTime: Duration)

data class LastTickTime(
    val gameTime: Duration,
    val tickCount: Int
)

enum class EffectType {
    BUFF,
    DEBUFF,
    HEAL,
    DAMAGE,
    STAT_MOD,
    STATUS,
    DISPELLABLE,
    PERMANENT
}

@JvmInline
value class TickInterval(val duration: Duration) {
    companion object {
        fun seconds(s: Int) = TickInterval(s.seconds)
        fun seconds(s: Double) = TickInterval(s.seconds)
        fun minutes(m: Int) = TickInterval(m.minutes)
    }
    val inWholeSeconds: Long get() = duration.inWholeSeconds
}

enum class StackBehavior {
    NONE,
    REFRESH_DURATION,
    INCREMENT_STACK
}

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

enum class ModifierType {
    ADD,
    MULTIPLY,
    SET
}

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

@JvmInline
value class AddAttributeModifier(val  value: Long)
@JvmInline
value class MultiplyAttributeModifier(val value: Double)
@JvmInline
value class AssignmentAttributeModifier(val value: Long)

enum class RemovalReason {
    EXPIRED,
    DISPELLED,
    MANUAL,
    ERROR,
    TARGET_DIED
}

data class OnEffectApplied(
    val effect: Entity,
    val target: Entity,
    val source: Entity? = null,
    val gameTime: Duration
)

data class OnEffectRemoved(
    val effect: Entity,
    val target: Entity,
    val reason: RemovalReason = RemovalReason.EXPIRED,
    val gameTime: Duration
)

data class OnEffectTick(
    val effect: Entity,
    val target: Entity,
    val tickCount: Int,
    val gameTime: Duration
)

enum class StatusEffectType {
    STUN,
    SILENCE,
    ROOT,
    POISON,
    BLEED
}

data class StatusEffect(
    val type: StatusEffectType,
    val duration: Duration? = null,
    val damagePerTick: Long = 0,
    val tickInterval: TickInterval? = null
) {
    companion object {
        fun stun(duration: Duration) = StatusEffect(
            type = StatusEffectType.STUN,
            duration = duration
        )
        fun silence(duration: Duration) = StatusEffect(
            type = StatusEffectType.SILENCE,
            duration = duration
        )
        fun root(duration: Duration) = StatusEffect(
            type = StatusEffectType.ROOT,
            duration = duration
        )
        fun poison(damagePerTick: Long, tickInterval: TickInterval, duration: Duration) = StatusEffect(
            type = StatusEffectType.POISON,
            duration = duration,
            damagePerTick = damagePerTick,
            tickInterval = tickInterval
        )
        fun bleed(damagePerTick: Long, tickInterval: TickInterval, duration: Duration) = StatusEffect(
            type = StatusEffectType.BLEED,
            duration = duration,
            damagePerTick = damagePerTick,
            tickInterval = tickInterval
        )
    }
}

sealed class Stunned
sealed class Silenced
sealed class Rooted
sealed class Poisoned
sealed class Bleeding

sealed class EffectApplicationException(message: String) : Exception(message) {
    data class LevelRequirementNotMet(
        val required: Int,
        val actual: Int
    ) : EffectApplicationException(
        "Level requirement not met: required $required, actual $actual"
    )
    data class MissingRequiredEffect(
        val required: Entity
    ) : EffectApplicationException(
        "Missing required effect: $required"
    )
    data class IncompatibleEffectPresent(
        val incompatible: Entity
    ) : EffectApplicationException(
        "Incompatible effect present: $incompatible"
    )
    data class InvalidTarget(
        val target: Entity
    ) : EffectApplicationException(
        "Invalid target: $target"
    )
    data class MaxStacksReached(
        val effectPrefab: Entity,
        val maxStacks: Int
    ) : EffectApplicationException(
        "Max stacks reached: $effectPrefab (max: $maxStacks)"
    )
}

sealed class AppliedBy
sealed class AppliedTo
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
    override fun EntityRelationContext.getWorldState(agent: Entity, key: AttributeKey): Long {
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
    private val itemAmountResolver = ItemAmountStateResolver(world)
    private val isStatusAffectedResolver = IsStatusAffectedResolver(world)
    private val effectCountResolver = EffectCountResolver(world)
    private val hasEffectResolver = HasEffectResolver(world)
    private val effectAttributeKeyResolver = EffectAttributeKeyResolver(world)
    override fun <K : StateKey<T>, T> getStateHandler(key: K): StateResolver<K, T>? {
        @Suppress("UNCHECKED_CAST")
        return when (key) {
            is AttributeKey -> effectAttributeResolver as StateResolver<K, T>
            is ItemAmountKey -> itemAmountResolver as StateResolver<K, T>
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
    fun modifyAttribute(attributeKey: AttributeKey, value: Long): ActionEffect {
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