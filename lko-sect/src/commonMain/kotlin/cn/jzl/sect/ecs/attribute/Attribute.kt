package cn.jzl.sect.ecs.attribute

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.AddonSetup
import cn.jzl.ecs.addon.WorldSetup
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.associatedBy
import cn.jzl.ecs.query.query
import cn.jzl.ecs.system.Phase
import cn.jzl.sect.ecs.core.Named
import kotlin.math.roundToLong
import kotlin.random.Random

sealed class Attribute

@JvmInline
value class AttributeValue(val value: Long) {
    operator fun plus(other: AttributeValue): AttributeValue = AttributeValue(value + other.value)

    companion object {
        val zero = AttributeValue(0)
        val one = AttributeValue(1)
    }
}

interface AttributeRegistry {
    fun register(provider: AttributeProvider)
}

class AttributeBuilder {

    internal val providers = mutableListOf<WorldOwner.() -> AttributeProvider>()

    @ECSDsl
    fun provider(provider: WorldOwner.() -> AttributeProvider) {
        providers.add(provider)
    }
}

@ECSDsl
fun AddonSetup<*>.attributes(config: AttributeBuilder.() -> Unit) {
    install(attributeAddon, config)
}

@ECSDsl
fun WorldSetup.attributes(config: AttributeBuilder.() -> Unit) {
    install(attributeAddon, config)
}

val attributeAddon = createAddon("Attribute", { AttributeBuilder() }) {
    injects {
        this bind singleton { new(::AttributeService) }
        this bind singleton { new(::SectAttributes) }
    }
    components {
        world.componentId<Attribute> { it.tag() }
        world.componentId<AttributeValue>()
    }
    on(Phase.ADDONS_CONFIGURED) {
        val attributeService by world.di.instance<AttributeService>()
        configuration.providers.forEach { provider ->
            attributeService.register(provider())
        }
    }
}

fun interface AttributeProvider {
    fun getAttributeValue(attributeService: AttributeService, entity: Entity, attribute: Entity): AttributeValue
}

interface AttributeAssistant {

    fun generateAttribute(attribute: Entity, minPer: Float, maxPer: Float): AttributeValue

    fun assistAttribute(context: EntityCreateContext, owner: Entity, attribute: Entity, baseValue: AttributeValue)

    class RangeAttributeAssistant(val range: LongRange) : AttributeAssistant {

        override fun generateAttribute(attribute: Entity, minPer: Float, maxPer: Float): AttributeValue {
            val interval = range.last + 1 - range.first
            val minOffset = (interval * minPer.coerceIn(0f, maxPer)).roundToLong()
            val maxOffset = (interval * maxPer.coerceIn(minPer, 1f)).roundToLong()
            return AttributeValue(Random.nextLong(range.first + minOffset, range.first + maxOffset))
        }

        override fun assistAttribute(context: EntityCreateContext, owner: Entity, attribute: Entity, baseValue: AttributeValue) = context.run {
            owner.addRelation(attribute, AttributeValue(baseValue.value.coerceIn(range.first, range.last)))
        }
    }
}


class AttributeService(world: World) : EntityRelationContext(world), AttributeRegistry {

    private val attributes = world.query { EntityAttributeContext(this) }.associatedBy { named }
    private val attributeProviders = mutableListOf<AttributeProvider>()

    val attributeNames: Sequence<Named> get() = attributes.keys

    override fun register(provider: AttributeProvider) {
        if (provider in attributeProviders) return
        attributeProviders.add(provider)
    }

    fun attribute(named: Named, attributeAssistant: AttributeAssistant): Entity {
        return world.entity {
            it.addTag<Attribute>()
            it.addComponent(named)
            it.addComponent(attributeAssistant)
        }
    }

    fun attribute(named: Named, min: Long, max: Long): Entity = attribute(named, AttributeAssistant.RangeAttributeAssistant(min..max))

    fun attribute(named: Named, block: EntityCreateContext.(Entity) -> Unit = {}): Entity {
        return attributes[named] ?: world.entity {
            it.addTag<Attribute>()
            it.addComponent(named)
            block(it)
        }
    }

    fun generateAttribute(context: EntityCreateContext, owner: Entity, attribute: Entity, minPer: Float = 0f, maxPer: Float = 1f) {
        val attributeAssistant = attribute.getComponent<AttributeAssistant>()
        attributeAssistant.assistAttribute(context, owner, attribute, attributeAssistant.generateAttribute(attribute, minPer, maxPer))
    }

    fun getAttributeValue(owner: Entity, attribute: Entity): AttributeValue? = owner.getRelation<AttributeValue?>(attribute)

    fun setAttributeValue(context: EntityCreateContext, owner: Entity, attribute: Entity, value: AttributeValue) {
        attribute.getComponent<AttributeAssistant>().assistAttribute(context, owner, attribute, value)
    }

    fun getAttributes(owner: Entity): Sequence<RelationWithData<AttributeValue>> = owner.getRelationsWithData<AttributeValue>()

    fun getTotalAttributeValue(owner: Entity, attribute: Entity, defaultAttributeValue: AttributeValue = AttributeValue.zero): AttributeValue {
        return attributeProviders.fold(getAttributeValue(owner, attribute) ?: defaultAttributeValue) { acc, attributeProvider ->
            acc + attributeProvider.getAttributeValue(this, owner, attribute)
        }
    }

    private class EntityAttributeContext(world: World) : EntityQueryContext(world) {

        val named by component<Named>()
        val attributeAssistant by component<AttributeAssistant>()

        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Attribute>())
        }
    }
}

class SectAttributes(world: World) {
    private val attributeService by world.di.instance<AttributeService>()

    // 五行之灵根属性
    val metalSpiritRoot: Entity by lazy { attributeService.attribute(Named("MetalSpiritRoot"), 0L, 100L) }
    val woodSpiritRoot: Entity by lazy { attributeService.attribute(Named("WoodSpiritRoot"), 0, 100) }
    val waterSpiritRoot: Entity by lazy { attributeService.attribute(Named("WaterSpiritRoot"), 0, 100) }
    val fireSpiritRoot: Entity by lazy { attributeService.attribute(Named("FireSpiritRoot"), 0, 100) }
    val earthSpiritRoot: Entity by lazy { attributeService.attribute(Named("EarthSpiritRoot"), 0, 100) }

    // 核心资质
    val rootBone: Entity by lazy { attributeService.attribute(Named("RootBone"), 0, 100) }
    val comprehension: Entity by lazy { attributeService.attribute(Named("comprehension"), 0, 100) }
    val fortune: Entity by lazy { attributeService.attribute(Named("fortune"), 0, 100) }
    val constitution: Entity by lazy { attributeService.attribute(Named("constitution"), 0, 100) }
    val soulPower: Entity by lazy { attributeService.attribute(Named("soulPower"), 0, 100) }

    // 特殊资质
    val swordTalent: Entity by lazy { attributeService.attribute(Named("swordTalent"), 0, 150) }
    val alchemyTalent: Entity by lazy { attributeService.attribute(Named("alchemyTalent"), 0, 150) }
    val forgingTalent: Entity by lazy { attributeService.attribute(Named("forgingTalent"), 0, 150) }
    val formationTalent: Entity by lazy { attributeService.attribute(Named("formationTalent"), 0, 150) }
    val beastTamingTalent: Entity by lazy { attributeService.attribute(Named("beastTamingTalent"), 0, 150) }

    // 生命
    val health: Entity by lazy { attributeService.attribute(Named("Health"), 1L, Long.MAX_VALUE) }
    val maxHealth: Entity by lazy { attributeService.attribute(Named("MaxHealth"), 1L, Long.MAX_VALUE) }

    //灵力
    val spiritualEnergy: Entity by lazy { attributeService.attribute(Named("SpiritualEnergy"), 1L, Long.MAX_VALUE) }
    val maxSpiritualEnergy: Entity by lazy { attributeService.attribute(Named("MaxSpiritualEnergy"), 1L, Long.MAX_VALUE) }

    // 修为/ 最高修为/ 境界
    val cultivation: Entity by lazy { attributeService.attribute(Named("Cultivation"), 0L, Long.MAX_VALUE) }
    val maxCultivation: Entity by lazy { attributeService.attribute(Named("MaxCultivation"), 0L, Long.MAX_VALUE) }
    val realm: Entity by lazy { attributeService.attribute(Named("Realm"), 1L, Long.MAX_VALUE) }

    // 寿命
    val lifespan: Entity by lazy { attributeService.attribute(Named("Lifespan"), 1L, Long.MAX_VALUE) }
}