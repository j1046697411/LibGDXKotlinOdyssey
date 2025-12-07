package cn.jzl.sect.ecs

import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.associatedBy
import cn.jzl.ecs.query.query
import cn.jzl.sect.ecs.core.Named

sealed class Sect

val sectAddon = createAddon("sect", {}) {
    injects {
        this bind singleton { new(::SectService) }
    }
    components {
        world.componentId<Sect> { it.tag() }
    }
}

class SectService(world: World) : EntityRelationContext(world) {

    @PublishedApi
    internal val sects = world.query { SectContext(this) }.associatedBy { named }

    @ECSDsl
    inline fun createSect(named: Named, block: EntityCreateContext.(Entity) -> Unit): Entity = world.entity {
        require(named !in sects) { "Sect named ${named.name} already exists" }
        it.addComponent(named)
        it.addTag<Sect>()
        block(it)
    }

    @ECSDsl
    fun createSect(name: String, block: EntityCreateContext.(Entity) -> Unit): Entity = createSect(Named(name), block)

    operator fun get(name: String): Entity? = sects[Named(name)]

    @PublishedApi
    internal class SectContext(world: World) : EntityQueryContext(world) {

        val named by component<Named>()

        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<Sect>()
        }
    }
}
