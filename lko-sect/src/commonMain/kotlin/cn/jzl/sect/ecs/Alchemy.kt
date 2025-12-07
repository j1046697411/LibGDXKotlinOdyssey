package cn.jzl.sect.ecs

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
import cn.jzl.ecs.query.*
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy

sealed class AlchemyFurnace
sealed class Idle

val alchemyAddon = createAddon("alchemy", {}) {
    injects {
        this bind singleton { new(::AlchemyService) }
    }
    components { world.componentId<AlchemyFurnace> { it.tag() } }
}

class AlchemyService(world: World) : EntityRelationContext(world) {
    private val alchemyFurnaces = world.query {
        object : EntityQueryContext(this) {
            val owner: Entity get() = requireNotNull(getRelationUp<OwnedBy>()) {}
            override fun FamilyMatcher.FamilyBuilder.configure() {
                component<Idle>()
                component<AlchemyFurnace>()
            }
        }
    }.groupedBy { owner }

    @ECSDsl
    fun createAlchemyFurnace(named: Named, sect: Entity, block: EntityCreateContext.(Entity) -> Unit): Entity = world.entity {
        it.addTag<AlchemyFurnace>()
        it.addRelation<OwnedBy>(sect)
        it.addComponent(named)
        it.addTag<Idle>()
        block(it)
    }

    fun getIdleAlchemyFurnace(sect: Entity): Entity? {
        return alchemyFurnaces[sect]?.map { entity }?.firstOrNull()
    }

    fun alchemy(sect: Entity, player: Entity, formula: Entity) {
        val alchemy = getIdleAlchemyFurnace(sect) ?: return

    }
}
