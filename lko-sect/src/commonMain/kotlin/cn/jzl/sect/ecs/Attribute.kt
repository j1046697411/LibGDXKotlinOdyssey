package cn.jzl.sect.ecs

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

class AttributeService(world: World) : EntityRelationContext(world), AttributeRegistry {

    private val attributes = world.query { EntityAttributeContext(this) }.associatedBy { named }
    private val attributeProviders = mutableListOf<AttributeProvider>()

    val attributeNames: Sequence<Named> get() = attributes.keys

    override fun register(provider: AttributeProvider) {
        if (provider in attributeProviders) return
        attributeProviders.add(provider)
    }

    fun attribute(named: Named, block: EntityCreateContext.(Entity) -> Unit = {}): Entity {
        return attributes[named] ?: world.entity {
            it.addTag<Attribute>()
            it.addComponent(named)
            block(it)
        }
    }

    fun getAttributeValue(owner: Entity, attribute: Entity): AttributeValue? = owner.getRelation<AttributeValue?>(attribute)

    fun setAttributeValue(context: EntityCreateContext, owner: Entity, attribute: Entity, value: AttributeValue) = context.run {
        owner.addRelation(attribute, value)
    }

    fun getAttributes(owner: Entity): Sequence<RelationWithData<AttributeValue>> = owner.getRelationsWithData<AttributeValue>()

    fun getTotalAttributeValue(owner: Entity, attribute: Entity, defaultAttributeValue: AttributeValue = AttributeValue.zero): AttributeValue {
        return attributeProviders.fold(getAttributeValue(owner, attribute) ?: defaultAttributeValue) { acc, attributeProvider ->
            acc + attributeProvider.getAttributeValue(this, owner, attribute)
        }
    }

    private class EntityAttributeContext(world: World) : EntityQueryContext(world) {

        val named by component<Named>()

        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Attribute>())
        }
    }
}

class SectAttributes(world: World) {
    private val attributeService by world.di.instance<AttributeService>()

    val attackAttribute by lazy { attributeService.attribute(Named("attack")) }
    val defenseAttribute by lazy { attributeService.attribute(Named("defense")) }
    val healthAttribute by lazy { attributeService.attribute(Named("health")) }
}
