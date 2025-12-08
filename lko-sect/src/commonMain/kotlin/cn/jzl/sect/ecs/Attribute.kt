package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.FamilyMatcher
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.AddonSetup
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.entity
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.associatedBy
import cn.jzl.ecs.query.query
import cn.jzl.sect.ecs.core.Description
import cn.jzl.sect.ecs.core.Named

@JvmInline
value class AttributeValue(val value: Long) {
    companion object {
        val ZERO = AttributeValue(0)
        val ONE = AttributeValue(1)
    }
}

sealed class Attribute

@JvmInline
value class AttributeRegistry(val attributes: MutableSet<AttributeData> = mutableSetOf()) {
    fun register(named: Named, description: Description? = null) {
        attributes.add(AttributeData(named, description))
    }

    data class AttributeData(val named: Named, val description: Description?)
}

private val attributeAddon = createAddon("attribute", { AttributeRegistry() }) {
    injects { this bind singleton { new(::AttributeService) } }
    components {
        world.componentId<Named>()
        world.componentId<AttributeValue>()
        world.componentId<Attribute> { it.tag() }
    }

    entities {
        val attributeService by world.di.instance<AttributeService>()
        configuration.attributes.forEach(attributeService::attributePrefab)
    }
}

@ECSDsl
fun AddonSetup<*>.attributes(block: AttributeRegistry.() -> Unit): Unit = install(attributeAddon, block)

class AttributeService(world: World) : EntityRelationContext(world) {

    private val attributes = world.query { EntityAttributeContext(this) }.associatedBy { named }
    val attributeNames: Sequence<Named> get() = attributes.keys

    internal fun attributePrefab(attributeData: AttributeRegistry.AttributeData): Entity {
        require(attributeData.named !in attributes) { "" }
        return world.entity { attribute ->
            attribute.addComponent(attributeData.named)
            attributeData.description?.let { attribute.addComponent(it) }
            attribute.addTag<Attribute>()
        }
    }

    operator fun get(named: Named): Entity? = attributes[named]

    fun attribute(named: Named): Entity = requireNotNull(this[named])

    @ECSDsl
    fun attribute(named: Named, block: EntityAttributeContext.() -> Unit): Unit = attributes.associate(named, block)

    class EntityAttributeContext(world: World) : EntityQueryContext(world) {

        val named by component<Named>()
        val description by component<Description?>()

        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<Attribute>()
        }
    }
}
