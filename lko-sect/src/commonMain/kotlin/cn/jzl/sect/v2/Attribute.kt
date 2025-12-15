package cn.jzl.sect.v2

import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityCreateContext
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.FamilyMatcher
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.entity
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.associatedBy
import cn.jzl.ecs.query.query
import cn.jzl.ecs.relation
import cn.jzl.ecs.relations
import cn.jzl.sect.ecs.core.Named


sealed class Attribute

@JvmInline
value class AttributeValue(val value: Long) {
    companion object {
        val zero = AttributeValue(0)
        val one = AttributeValue(1)
    }
}

val attributeAddon = createAddon("Attribute") {
    injects { this bind singleton { new(::AttributeService) } }
    components {
        world.componentId<Attribute> { it.tag() }
        world.componentId<AttributeValue>()
    }
    planning {
        register(AttributeStateResolverRegistry(world))
    }
}

class AttributeService(world: World) : EntityRelationContext(world) {

    private val attributes = world.query { EntityAttributeContext(this) }.associatedBy { named }

    val attributeNames: Sequence<Named> get() = attributes.keys

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

    private class EntityAttributeContext(world: World) : EntityQueryContext(world) {

        val named by component<Named>()

        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Attribute>())
        }
    }
}
